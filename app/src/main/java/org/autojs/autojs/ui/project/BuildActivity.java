package org.autojs.autojs.ui.project;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.material.textfield.TextInputLayout;
import com.stardust.autojs.project.ProjectConfig;
import com.stardust.util.IntentUtil;
import com.storyteller_f.bandage.Bandage;
import com.storyteller_f.bandage.Click;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.build.ApkBuilder;
import org.autojs.autojs.build.ApkBuilderPluginHelper;
import org.autojs.autojs.databinding.ActivityBuildBinding;
import org.autojs.autojs.external.fileprovider.AppFileProvider;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.tool.BitmapTool;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.ui.filechooser.FileChooserDialogBuilder;
import org.autojs.autojs.ui.shortcut.ShortcutIconSelectActivity;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/22.
 */
public class BuildActivity extends BaseActivity implements ApkBuilder.ProgressCallback {

    public static final String EXTRA_SOURCE = BuildActivity.class.getName() + ".extra_source_file";
    private static final int REQUEST_CODE = 44401;
    private static final String LOG_TAG = "BuildActivity";
    private static final Pattern REGEX_PACKAGE_NAME = Pattern.compile("^([A-Za-z][A-Za-z\\d_]*\\.)+([A-Za-z][A-Za-z\\d_]*)$");

    @NonNull
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ProjectConfig mProjectConfig;
    @Nullable
    private MaterialDialog mProgressDialog;
    private String mSource;
    private boolean mIsDefaultIcon = true;
    private ActivityBuildBinding inflate;

    @NonNull
    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context mContext) {
        return new ActivityIntentBuilder<I>(mContext, BuildActivity.class) {
            @Nullable
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
        inflate = ActivityBuildBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setupViews();
        Bandage.bind(this, inflate.getRoot());
    }

    void setupViews() {
        setToolbarAsBack(getString(R.string.text_build_apk));
        mSource = getIntent().getStringExtra(EXTRA_SOURCE);
        if (mSource != null) {
            setupWithSourceFile(new ScriptFile(mSource));
        }
        checkApkBuilderPlugin();
    }

    private void checkApkBuilderPlugin() {
        if (!ApkBuilderPluginHelper.isPluginAvailable(this)) {
            showPluginDownloadDialog(R.string.no_apk_builder_plugin, true);
            return;
        }
        int version = ApkBuilderPluginHelper.getPluginVersion(this);
        if (version < 0) {
            showPluginDownloadDialog(R.string.no_apk_builder_plugin, true);
            return;
        }
        if (version < ApkBuilderPluginHelper.getSuitablePluginVersion()) {
            showPluginDownloadDialog(R.string.apk_builder_plugin_version_too_low, false);
        }
    }

    private void showPluginDownloadDialog(int msgRes, boolean finishIfCanceled) {
        new ThemeColorMaterialDialogBuilder(this)
                .content(msgRes)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> downloadPlugin())
                .onNegative((dialog, which) -> {
                    if (finishIfCanceled) finish();
                })
                .show();

    }

    private void downloadPlugin() {
        IntentUtil.browse(this, String.format(Locale.getDefault(),
                "https://i.autojs.org/autojs/plugin/%d.apk", ApkBuilderPluginHelper.getSuitablePluginVersion()));
    }

    private void setupWithSourceFile(@NonNull ScriptFile file) {
        String dir = file.getParent();
        if (dir.startsWith(getFilesDir().getPath())) {
            dir = Pref.getScriptDirPath();
        }
        inflate.outputPath.setText(dir);
        inflate.appName.setText(file.getSimplifiedName());
        inflate.packageName.setText(getString(R.string.format_default_package_name, System.currentTimeMillis()));
        setSource(file);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }

    @Click(tag = "select_source")
    void selectSourceFilePath() {
        String initialDir = new File(Objects.requireNonNull(inflate.sourcePath.getText()).toString()).getParent();
        new FileChooserDialogBuilder(this)
                .title(R.string.text_source_file_path)
                .dir(Environment.getExternalStorageDirectory().getPath(),
                        initialDir == null ? Pref.getScriptDirPath() : initialDir)
                .singleChoice(this::setSource)
                .show();
    }

    private void setSource(@NonNull File file) {
        if (!file.isDirectory()) {
            inflate.sourcePath.setText(file.getPath());
            return;
        }
        mProjectConfig = ProjectConfig.fromProjectDir(file.getPath());
        if (mProjectConfig == null) {
            return;
        }
        inflate.outputPath.setText(new File(mSource, mProjectConfig.getBuildDir()).getPath());
        inflate.appConfig.setVisibility(View.GONE);
        inflate.sourcePathContainer.setVisibility(View.GONE);
    }

    @Click(tag = "select_output")
    void selectOutputDirPath() {
        String initialDir = new File(Objects.requireNonNull(inflate.outputPath.getText()).toString()).exists() ?
                inflate.outputPath.getText().toString() : Pref.getScriptDirPath();
        new FileChooserDialogBuilder(this)
                .title(R.string.text_output_apk_path)
                .dir(initialDir)
                .chooseDir()
                .singleChoice(dir -> inflate.outputPath.setText(dir.getPath()))
                .show();
    }

    @Click(tag = "icon")
    void selectIcon() {
        ShortcutIconSelectActivity.intent(this)
                .startForResult(REQUEST_CODE);
    }

    @Click(tag = "fab")
    void buildApk() {
        if (!ApkBuilderPluginHelper.isPluginAvailable(this)) {
            Toast.makeText(this, R.string.text_apk_builder_plugin_unavailable, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkInputs()) {
            return;
        }
        doBuildingApk();
    }

    private boolean checkInputs() {
        boolean inputValid;
        inputValid = checkNotEmpty(inflate.sourcePath);
        inputValid &= checkNotEmpty(inflate.outputPath);
        inputValid &= checkNotEmpty(inflate.appName);
        inputValid &= checkNotEmpty(inflate.sourcePath);
        inputValid &= checkNotEmpty(inflate.versionCode);
        inputValid &= checkNotEmpty(inflate.versionName);
        inputValid &= checkPackageNameValid(inflate.packageName);
        return inputValid;
    }

    private boolean checkPackageNameValid(@NonNull EditText editText) {
        Editable text = editText.getText();
        String hint = Objects.requireNonNull(((TextInputLayout) editText.getParent().getParent()).getHint()).toString();
        if (TextUtils.isEmpty(text)) {
            editText.setError(hint + getString(R.string.text_should_not_be_empty));
            return false;
        }
        if (!REGEX_PACKAGE_NAME.matcher(text).matches()) {
            editText.setError(getString(R.string.text_invalid_package_name));
            return false;
        }
        return true;

    }

    private boolean checkNotEmpty(@NonNull EditText editText) {
        if (!TextUtils.isEmpty(editText.getText()) || !editText.isShown())
            return true;
        // TODO: 2017/12/8 more beautiful ways?
        String hint = Objects.requireNonNull(((TextInputLayout) editText.getParent().getParent()).getHint()).toString();
        editText.setError(hint + getString(R.string.text_should_not_be_empty));
        return false;
    }

    @SuppressLint("CheckResult")
    private void doBuildingApk() {
        ApkBuilder.AppConfig appConfig = createAppConfig();
        File tmpDir = new File(getCacheDir(), "build/");
        File outApk = new File(Objects.requireNonNull(inflate.outputPath.getText()).toString(),
                String.format("%s_v%s.apk", appConfig.getAppName(), appConfig.getVersionName()));
        showProgressDialog();
        Disposable subscribe = Observable.fromCallable(() -> callApkBuilder(tmpDir, outApk, appConfig))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apkBuilder -> onBuildSuccessful(outApk),
                        this::onBuildFailed);
        compositeDisposable.add(subscribe);
    }

    private ApkBuilder.AppConfig createAppConfig() {
        if (mProjectConfig != null) {
            return ApkBuilder.AppConfig.fromProjectConfig(mSource, mProjectConfig);
        }
        String jsPath = Objects.requireNonNull(inflate.sourcePath.getText()).toString();
        String versionName = Objects.requireNonNull(inflate.versionName.getText()).toString();
        int versionCode = Integer.parseInt(Objects.requireNonNull(inflate.versionCode.getText()).toString());
        String appName = Objects.requireNonNull(inflate.appName.getText()).toString();
        String packageName = Objects.requireNonNull(inflate.packageName.getText()).toString();
        return new ApkBuilder.AppConfig()
                .setAppName(appName)
                .setSourcePath(jsPath)
                .setPackageName(packageName)
                .setVersionCode(versionCode)
                .setVersionName(versionName)
                .setIcon(mIsDefaultIcon ? null : () ->
                        BitmapTool.drawableToBitmap(inflate.icon.getDrawable())
                );
    }

    private ApkBuilder callApkBuilder(@NonNull File tmpDir, @NonNull File outApk, @NonNull ApkBuilder.AppConfig appConfig) throws Exception {
        InputStream templateApk = ApkBuilderPluginHelper.openTemplateApk(BuildActivity.this);
        return new ApkBuilder(templateApk, outApk, tmpDir.getPath())
                .setProgressCallback(BuildActivity.this)
                .prepare()
                .withConfig(appConfig)
                .build()
                .sign()
                .cleanWorkspace();
    }

    private void showProgressDialog() {
        mProgressDialog = new MaterialDialog.Builder(this)
                .progress(true, 100)
                .content(R.string.text_on_progress)
                .cancelable(false)
                .show();
    }

    private void onBuildFailed(@NonNull Throwable error) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
        Toast.makeText(this, getString(R.string.text_build_failed) + error.getMessage(), Toast.LENGTH_SHORT).show();
        Log.e(LOG_TAG, "Build failed", error);
    }

    private void onBuildSuccessful(@NonNull File outApk) {
        mProgressDialog.dismiss();
        mProgressDialog = null;
        new MaterialDialog.Builder(this)
                .title(R.string.text_build_successfully)
                .content(getString(R.string.format_build_successfully, outApk.getPath()))
                .positiveText(R.string.text_install)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) ->
                        IntentUtil.installApkOrToast(BuildActivity.this, outApk.getPath(), AppFileProvider.AUTHORITY)
                )
                .show();

    }

    @Override
    public void onPrepare(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_prepare);
    }

    @Override
    public void onBuild(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_build);

    }

    @Override
    public void onSign(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_package);

    }

    @Override
    public void onClean(ApkBuilder builder) {
        mProgressDialog.setContent(R.string.apk_builder_clean);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        Disposable subscribe = ShortcutIconSelectActivity.getBitmapFromIntent(getApplicationContext(), data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    inflate.icon.setImageBitmap(bitmap);
                    mIsDefaultIcon = false;
                }, Throwable::printStackTrace);
        compositeDisposable.add(subscribe);
    }

}
