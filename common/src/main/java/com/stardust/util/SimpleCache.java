package com.stardust.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Iterator;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Stardust on 2017/4/5.
 */

public class SimpleCache<T> {

    public interface Supplier<T> {
        @Nullable
        T get(String key);
    }

    private final long mPersistTime;
    @NonNull
    private final LimitedHashMap<String, Item<T>> mCache;
    @NonNull
    private final Timer mCacheCheckTimer;
    @NonNull
    private final Supplier<T> mSupplier;

    public SimpleCache(long persistTime, int cacheSize, long checkInterval, @Nullable Supplier<T> supplier) {
        mPersistTime = persistTime;
        mCache = new LimitedHashMap<>(cacheSize);
        mSupplier = supplier == null ? new NullSupplier<T>() : supplier;
        mCacheCheckTimer = new Timer();
        startCacheCheck(checkInterval);
    }

    public SimpleCache(long persistTime, int cacheSize, long checkInterval) {
        this(persistTime, cacheSize, checkInterval, null);
    }

    public synchronized void put(String key, T value) {
        mCache.put(key, new Item<>(value));
    }

    @Nullable
    public synchronized T get(String key) {
        Item<T> item = mCache.get(key);
        if (item == null) {
            T value = mSupplier.get(key);
            if (value != null) {
                put(key, value);
            }
            return value;
        }
        return item.value;
    }

    @Nullable
    public T get(String key, T defaultValue) {
        T value = get(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    @Nullable
    public T get(String key, @NonNull Supplier<T> supplier) {
        T value = get(key);
        if (value == null) {
            value = supplier.get(key);
            put(key, value);
        }
        return value;
    }

    public synchronized void destroy() {
        mCacheCheckTimer.cancel();
        mCache.clear();
    }

    private void startCacheCheck(long checkInterval) {
        mCacheCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkCache();
            }
        }, 0, checkInterval);
    }


    private synchronized void checkCache() {
        Iterator<Map.Entry<String, Item<T>>> iterator = mCache.entrySet().iterator();
        while (iterator.hasNext()) {
            if (!iterator.next().getValue().isValid()) {
                iterator.remove();
            }
        }
    }

    private class Item<T> {

        T value;
        private final long mSaveMillis;


        Item(T value) {
            mSaveMillis = System.currentTimeMillis();
            this.value = value;
        }

        boolean isValid() {
            return System.currentTimeMillis() - mSaveMillis <= mPersistTime;
        }

    }

    private static class NullSupplier<T> implements Supplier<T> {

        @Nullable
        @Override
        public T get(String key) {
            return null;
        }
    }


}
