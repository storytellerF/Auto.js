package org.autojs.autojs.ui.shortcut;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityShortcutIconSelectBinding;
import org.autojs.autojs.tool.BitmapTool;
import org.autojs.autojs.ui.BaseActivity;
import org.autojs.autojs.workground.WrapContentGridLayoutManger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/25.
 */
public class ShortcutIconSelectActivity extends BaseActivity {

    public static final String EXTRA_PACKAGE_NAME = "extra_package_name";
    private final List<AppItem> mAppList = new ArrayList<>();
    @NonNull
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ActivityShortcutIconSelectBinding inflate;
    private PackageManager mPackageManager;

    @NonNull
    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context mContext) {
        return new ActivityIntentBuilder<I>(mContext, ShortcutIconSelectActivity.class) {
            @Nullable
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                context.startActivity(intent);
                return null;
            }
        };
    }

    @NonNull
    public static Observable<Bitmap> getBitmapFromIntent(@NonNull Context context, @NonNull Intent data) {
        String packageName = data.getStringExtra(EXTRA_PACKAGE_NAME);
        if (packageName != null) {
            return Observable.fromCallable(() -> {
                Drawable drawable = context.getPackageManager().getApplicationIcon(packageName);
                return BitmapTool.drawableToBitmap(drawable);
            });
        }
        Uri uri = data.getData();
        if (uri == null) {
            return Observable.error(new IllegalArgumentException("invalid intent"));
        }
        return Observable.fromCallable(() ->
                BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri))
        );
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityShortcutIconSelectBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setupViews();
    }

    void setupViews() {
        mPackageManager = getPackageManager();
        setToolbarAsBack(getString(R.string.text_select_icon));
        setupApps();
    }

    private void setupApps() {
        inflate.apps.setAdapter(new AppsAdapter());
        WrapContentGridLayoutManger manager = new WrapContentGridLayoutManger(this, 5);
        manager.setDebugInfo("IconSelectView");
        inflate.apps.setLayoutManager(manager);
        loadApps();
    }

    @SuppressLint("CheckResult")
    private void loadApps() {
        List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        Disposable subscribe = Observable.fromIterable(packages)
                .observeOn(Schedulers.computation())
                .filter(appInfo -> appInfo.icon != 0)
                .map(AppItem::new)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(icon -> {
                    mAppList.add(icon);
                    inflate.apps.getAdapter().notifyItemInserted(mAppList.size() - 1);
                });
        compositeDisposable.add(subscribe);
    }

    private void selectApp(@NonNull AppItem appItem) {
        setResult(RESULT_OK, new Intent()
                .putExtra(EXTRA_PACKAGE_NAME, appItem.info.packageName));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shortcut_icon_select, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivityForResult(new Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*"), 11234);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private class AppItem {
        Drawable icon;
        ApplicationInfo info;

        public AppItem(@NonNull ApplicationInfo info) {
            this.info = info;
            icon = info.loadIcon(mPackageManager);
        }
    }

    private class AppIconViewHolder extends RecyclerView.ViewHolder {

        ImageView icon;

        public AppIconViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = (ImageView) itemView;
            icon.setOnClickListener(v -> selectApp(mAppList.get(getAdapterPosition())));
        }
    }


    private class AppsAdapter extends RecyclerView.Adapter<AppIconViewHolder> {

        @Override
        @NonNull
        public AppIconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new AppIconViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.app_icon_list_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull AppIconViewHolder holder, int position) {
            holder.icon.setImageDrawable(mAppList.get(position).icon);
        }

        @Override
        public int getItemCount() {
            return mAppList.size();
        }
    }


}
