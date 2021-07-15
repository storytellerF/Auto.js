package org.autojs.autojs.ui.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputLayout;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.pio.PFiles;
import com.storyteller_f.bandage.Bandage;
import com.storyteller_f.bandage.Click;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityProjectConfigBinding;
import org.autojs.autojs.model.explorer.ExplorerDirPage;
import org.autojs.autojs.model.explorer.ExplorerFileItem;
import org.autojs.autojs.model.explorer.Explorers;
import org.autojs.autojs.model.project.ProjectTemplate;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.shortcut.ShortcutIconSelectActivity;
import org.autojs.autojs.ui.widget.SimpleTextWatcher;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ProjectConfigActivity extends BaseActivity {

    public static final String EXTRA_PARENT_DIRECTORY = "parent_directory";

    public static final String EXTRA_NEW_PROJECT = "new_project";

    public static final String EXTRA_DIRECTORY = "directory";

    private static final int REQUEST_CODE = 12477;
    private static final Pattern REGEX_PACKAGE_NAME = Pattern.compile("^([A-Za-z][A-Za-z\\d_]*\\.)+([A-Za-z][A-Za-z\\d_]*)$");

    private File mDirectory;
    private File mParentDirectory;
    private ProjectConfig mProjectConfig;
    private boolean mNewProject;
    private Bitmap mIconBitmap;
    private ActivityProjectConfigBinding inflate;

    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context mContext) {
        return new ActivityIntentBuilder<I>(mContext, ProjectConfigActivity.class) {
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                context.startActivity(intent);
                return null;
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityProjectConfigBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setupViews();
        inflate.fab.setTag("fab");
        inflate.icon.setTag("icon");
        Bandage.bind(this,inflate.getRoot());
        mNewProject = getIntent().getBooleanExtra(EXTRA_NEW_PROJECT, false);
        String parentDirectory = getIntent().getStringExtra(EXTRA_PARENT_DIRECTORY);
        if (mNewProject) {
            if (parentDirectory == null) {
                finish();
                return;
            }
            mParentDirectory = new File(parentDirectory);
            mProjectConfig = new ProjectConfig();
        } else {
            String dir = getIntent().getStringExtra(EXTRA_DIRECTORY);
            if (dir == null) {
                finish();
                return;
            }
            mDirectory = new File(dir);
            mProjectConfig = ProjectConfig.fromProjectDir(dir);
            if (mProjectConfig == null) {
                new ThemeColorMaterialDialogBuilder(this)
                        .title(R.string.text_invalid_project)
                        .positiveText(R.string.ok)
                        .dismissListener(dialogInterface -> finish())
                        .show();
            }
        }
    }

    void setupViews() {
        if (mProjectConfig == null) {
            return;
        }
        setToolbarAsBack(mNewProject ? getString(R.string.text_new_project) : mProjectConfig.getName());
        if (mNewProject) {
            inflate.appName.addTextChangedListener(new SimpleTextWatcher(s ->
                    inflate.projectLocation.setText(new File(mParentDirectory, s.toString()).getPath()))
            );
        } else {
            inflate.appName.setText(mProjectConfig.getName());
            inflate.versionCode.setText(String.valueOf(mProjectConfig.getVersionCode()));
            inflate.packageName.setText(mProjectConfig.getPackageName());
            inflate.versionName.setText(mProjectConfig.getVersionName());
            inflate.mainFileName.setText(mProjectConfig.getMainScriptFile());
            inflate.projectLocation.setVisibility(View.GONE);
            String icon = mProjectConfig.getIcon();
            if (icon != null) {
                Glide.with(this)
                        .load(new File(mDirectory, icon))
                        .into(inflate.icon);
            }
        }
    }

    @SuppressLint("CheckResult")
    @Click(tag="fab")
    void commit() {
        if (!checkInputs()) {
            return;
        }
        syncProjectConfig();
        if (mIconBitmap != null) {
            Disposable subscribe = saveIcon(mIconBitmap)
                    .subscribe(ignored -> saveProjectConfig(), e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(subscribe);
        } else {
            saveProjectConfig();
        }

    }
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    @SuppressLint("CheckResult")
    private void saveProjectConfig() {
        if (mNewProject) {
            Disposable subscribe = new ProjectTemplate(mProjectConfig, mDirectory)
                    .newProject()
                    .subscribe(ignored -> {
                        Explorers.workspace().notifyChildrenChanged(new ExplorerDirPage(mParentDirectory, null));
                        finish();
                    }, e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(subscribe);
        } else {
            Disposable subscribe = Observable.fromCallable(() -> {
                PFiles.write(ProjectConfig.configFileOfDir(mDirectory.getPath()),
                        mProjectConfig.toJson());
                return Void.TYPE;
            })
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(ignored -> {
                        ExplorerFileItem item = new ExplorerFileItem(mDirectory, null);
                        Explorers.workspace().notifyItemChanged(item, item);
                        finish();
                    }, e -> {
                        e.printStackTrace();
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            compositeDisposable.add(subscribe);
        }
    }

    @Click(tag = "icon")
    void selectIcon() {
        ShortcutIconSelectActivity.intent(this)
                .startForResult(REQUEST_CODE);
    }

    private void syncProjectConfig() {
        mProjectConfig.setName(inflate.appName.getText().toString());
        mProjectConfig.setVersionCode(Integer.parseInt(inflate.versionCode.getText().toString()));
        mProjectConfig.setVersionName(inflate.versionName.getText().toString());
        mProjectConfig.setMainScriptFile(inflate.mainFileName.getText().toString());
        mProjectConfig.setPackageName(inflate.packageName.getText().toString());
        if (mNewProject) {
            String location = inflate.projectLocation.getText().toString();
            mDirectory = new File(location);
        }
        //mProjectConfig.getLaunchConfig().setHideLogs(true);
    }

    private boolean checkInputs() {
        boolean inputValid = true;
        inputValid &= checkNotEmpty(inflate.appName);
        inputValid &= checkNotEmpty(inflate.versionCode);
        inputValid &= checkNotEmpty(inflate.versionName);
        inputValid &= checkPackageNameValid(inflate.packageName);
        return inputValid;
    }

    private boolean checkPackageNameValid(EditText editText) {
        Editable text = editText.getText();
        String hint = ((TextInputLayout) editText.getParent().getParent()).getHint().toString();
        if(TextUtils.isEmpty(text)){
            editText.setError(hint + getString(R.string.text_should_not_be_empty));
            return false;
        }
        if(!REGEX_PACKAGE_NAME.matcher(text).matches()){
            editText.setError(getString(R.string.text_invalid_package_name));
            return false;
        }
        return true;

    }

    private boolean checkNotEmpty(EditText editText) {
        if (!TextUtils.isEmpty(editText.getText()))
            return true;
        // TODO: 2017/12/8 more beautiful ways?
        String hint = ((TextInputLayout) editText.getParent().getParent()).getHint().toString();
        editText.setError(hint + getString(R.string.text_should_not_be_empty));
        return false;
    }


    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        ShortcutIconSelectActivity.getBitmapFromIntent(getApplicationContext(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                            inflate.icon.setImageBitmap(bitmap);
                            mIconBitmap = bitmap;
                        },
                        Throwable::printStackTrace);
    }

    @SuppressLint("CheckResult")
    private Observable<String> saveIcon(Bitmap b) {
        return Observable.just(b)
                .map(bitmap -> {
                    String iconPath = mProjectConfig.getIcon();
                    if (iconPath == null) {
                        iconPath = "res/logo.png";
                    }
                    File iconFile = new File(mDirectory, iconPath);
                    PFiles.ensureDir(iconFile.getPath());
                    FileOutputStream fos = new FileOutputStream(iconFile);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    return iconPath;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(iconPath -> mProjectConfig.setIcon(iconPath));

    }

}
