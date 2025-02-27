package com.stardust.util;

import android.app.Activity;
import android.content.res.AssetManager;

import androidx.annotation.NonNull;

import com.stardust.pio.PFiles;

/**
 * Created by Stardust on 2017/3/14.
 */

public class AssetsCache {

    private static final long PERSIST_TIME = 5 * 60 * 1000;

    private static final SimpleCache<String> cache = new SimpleCache<>(PERSIST_TIME, 5, 30 * 1000);

    public static String get(@NonNull final AssetManager assetManager, final String path) {
        return cache.get(path, new SimpleCache.Supplier<String>() {
            @NonNull
            @Override
            public String get(String key) {
                return PFiles.readAsset(assetManager, path);
            }
        });
    }

    public static String get(@NonNull final Activity activity, final String path) {
        return get(activity.getAssets(), path);
    }
}
