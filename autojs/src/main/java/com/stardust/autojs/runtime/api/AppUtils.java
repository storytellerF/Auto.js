package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.util.IntentUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by Stardust on 2017/4/2.
 */

public class AppUtils {

    private final Context mContext;
    @Nullable
    private volatile WeakReference<Activity> mCurrentActivity = new WeakReference<>(null);
    @Nullable
    private final String mFileProviderAuthority;

    public AppUtils(Context context) {
        mContext = context;
        mFileProviderAuthority = null;
    }

    public AppUtils(Context context, String fileProviderAuthority) {
        mContext = context;
        mFileProviderAuthority = fileProviderAuthority;
    }

    @ScriptInterface
    public boolean launchPackage(String packageName) {
        try {
            PackageManager packageManager = mContext.getPackageManager();
            mContext.startActivity(packageManager.getLaunchIntentForPackage(packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    @ScriptInterface
    public void sendLocalBroadcastSync(@NonNull Intent intent) {
        LocalBroadcastManager.getInstance(mContext).sendBroadcastSync(intent);
    }

    @ScriptInterface
    public boolean launchApp(String appName) {
        String pkg = getPackageName(appName);
        if (pkg == null)
            return false;
        return launchPackage(pkg);
    }

    @Nullable
    @ScriptInterface
    public String getPackageName(String appName) {
        PackageManager packageManager = mContext.getPackageManager();
        List<ApplicationInfo> installedApplications = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo applicationInfo : installedApplications) {
            if (packageManager.getApplicationLabel(applicationInfo).toString().equals(appName)) {
                return applicationInfo.packageName;
            }
        }
        return null;
    }

    @Nullable
    @ScriptInterface
    public String getAppName(String packageName) {
        PackageManager packageManager = mContext.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            CharSequence appName = packageManager.getApplicationLabel(applicationInfo);
            return appName == null ? null : appName.toString();
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    @ScriptInterface
    public boolean openAppSetting(String packageName) {
        return IntentUtil.goToAppDetailSettings(mContext, packageName);
    }

    @Nullable
    @ScriptInterface
    public String getFileProviderAuthority() {
        return mFileProviderAuthority;
    }

    @Nullable
    public Activity getCurrentActivity() {
        Log.d("App", "getCurrentActivity: " + mCurrentActivity.get());
        return mCurrentActivity.get();
    }

    @ScriptInterface
    public void uninstall(String packageName) {
        mContext.startActivity(new Intent(Intent.ACTION_DELETE, Uri.parse("package:" + packageName))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @ScriptInterface
    public void viewFile(@Nullable String path) {
        if (path == null)
            throw new NullPointerException("path == null");
        IntentUtil.viewFile(mContext, path, mFileProviderAuthority);
    }

    @ScriptInterface
    public void editFile(@Nullable String path) {
        if (path == null)
            throw new NullPointerException("path == null");
        IntentUtil.editFile(mContext, path, mFileProviderAuthority);
    }

    @ScriptInterface
    public void openUrl(@NonNull String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        mContext.startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse(url))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    public void setCurrentActivity(Activity currentActivity) {
        mCurrentActivity = new WeakReference<>(currentActivity);
        Log.d("App", "setCurrentActivity: " + currentActivity);
    }
}
