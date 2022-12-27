package com.stardust.util;

import android.content.Intent;
import android.util.SparseArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Stardust on 2017/7/11.
 */

public class IntentExtras implements Serializable {

    public static final String EXTRA_ID = "com.stardust.util.IntentExtras.id";

    private static final AtomicInteger mMaxId = new AtomicInteger(-1);
    private static final SparseArray<Map<String, Object>> extraStore = new SparseArray<>();

    private final Map<String, Object> mMap;
    private int mId;

    private IntentExtras() {
        mMap = new HashMap<>();
        mId = mMaxId.incrementAndGet();
        extraStore.put(mId, mMap);
    }


    private IntentExtras(int id, Map<String, Object> map) {
        mId = id;
        mMap = map;
    }


    @NonNull
    public static IntentExtras newExtras() {
        return new IntentExtras();
    }

    @Nullable
    public static IntentExtras fromIntentAndRelease(@NonNull Intent intent) {
        int id = intent.getIntExtra(EXTRA_ID, -1);
        if (id < 0) {
            return null;
        }
        return fromIdAndRelease(id);
    }

    @Nullable
    public static IntentExtras fromIdAndRelease(int id) {
        Map<String, Object> map = extraStore.get(id);
        if (map == null) {
            return null;
        }
        extraStore.remove(id);
        return new IntentExtras(id, map);
    }

    @Nullable
    public static IntentExtras fromId(int id) {
        Map<String, Object> map = extraStore.get(id);
        if (map == null) {
            return null;
        }
        return new IntentExtras(id, map);
    }


    @Nullable
    public static IntentExtras fromIntent(@NonNull Intent intent) {
        int id = intent.getIntExtra(EXTRA_ID, -1);
        if (id < 0) {
            return null;
        }
        return fromId(id);
    }


    public int getId() {
        return mId;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) mMap.get(key);
    }

    @NonNull
    public IntentExtras put(String key, Object value) {
        mMap.put(key, value);
        return this;
    }

    @NonNull
    public IntentExtras putAll(@NonNull IntentExtras extras) {
        mMap.putAll(extras.mMap);
        return this;
    }

    @NonNull
    public Intent putInIntent(@NonNull Intent intent) {
        intent.putExtra(EXTRA_ID, mId);
        return intent;
    }

    public void release() {
        extraStore.remove(mId);
        mId = -1;
    }


}
