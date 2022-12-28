package org.autojs.autojs.external.shortcut;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.autojs.autojs.tool.BitmapTool;

/**
 * Created by Stardust on 2017/1/20.
 */

public class Shortcut {

    private final Context mContext;
    private final Intent mLaunchIntent = new Intent();
    private String mName;
    private String mTargetClass;
    private String mTargetPackage;
    private Intent.ShortcutIconResource mIconRes;
    private boolean mDuplicate = false;
    @Nullable
    private Bitmap mIcon;

    public Shortcut(Context context) {
        mContext = context;
        mTargetPackage = mContext.getPackageName();
    }

    public Shortcut(String name, Context context) {
        this(context);
        mName = name;
    }

    @NonNull
    public Shortcut name(String name) {
        mName = name;
        return this;
    }

    @NonNull
    public Shortcut targetPackage(String targetPackage) {
        mTargetPackage = targetPackage;
        return this;
    }

    @NonNull
    public Shortcut targetClass(String targetClass) {
        mTargetClass = targetClass;
        return this;
    }

    @NonNull
    public Shortcut targetClass(@NonNull Class<?> targetClass) {
        mTargetClass = targetClass.getName();
        return this;
    }


    @NonNull
    public Shortcut iconRes(Intent.ShortcutIconResource icon) {
        if (mIcon != null) {
            throw new IllegalStateException("Cannot set both iconRes and icon");
        }
        mIconRes = icon;
        return this;
    }

    @NonNull
    public Shortcut iconRes(int resId) {
        return iconRes(Intent.ShortcutIconResource.fromContext(mContext, resId));
    }


    @NonNull
    public Shortcut icon(@NonNull Bitmap icon) {
        if (mIconRes != null) {
            throw new IllegalStateException("Cannot set both iconRes and icon");
        }
        if (icon.getByteCount() > 1024 * 500) {
            mIcon = BitmapTool.scaleBitmap(icon, 200, 200);
        } else {
            mIcon = icon;
        }
        return this;
    }

    public Intent.ShortcutIconResource getIconRes() {
        return mIconRes;
    }


    @NonNull
    public Shortcut duplicate(boolean duplicate) {
        mDuplicate = duplicate;
        return this;
    }

    public String getName() {
        return mName;
    }

    @Nullable
    public Bitmap getIcon() {
        return mIcon;
    }

    public boolean isDuplicate() {
        return mDuplicate;
    }

    private String getClassName() {
        return mTargetClass;
    }

    private String getPackageName() {
        return mTargetPackage;
    }

    public Intent getCreateIntent() {
        Intent intent = new Intent(Intent.ACTION_CREATE_SHORTCUT)
                .putExtra(Intent.EXTRA_SHORTCUT_NAME, getName())
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, getLaunchIntent())
                .putExtra("duplicate", isDuplicate())
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        Bitmap icon = getIcon();
        if (icon == null) {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, getIconRes());
        } else {
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, icon);
        }
        return intent;
    }

    @NonNull
    public Intent getLaunchIntent() {
        mLaunchIntent.setClassName(getPackageName(), getClassName());
        return mLaunchIntent;
    }

    public void send() {
        mContext.sendBroadcast(getCreateIntent());
    }

    @NonNull
    public Shortcut extras(Bundle bundle) {
        mLaunchIntent.putExtras(bundle);
        return this;
    }

    @NonNull
    public Shortcut extras(Intent intent) {
        mLaunchIntent.putExtras(intent);
        return this;
    }
}
