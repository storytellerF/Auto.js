package com.stardust.autojs.core.ui.inflater;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class InflateContext {

    private HashMap<String, Object> mProperties;

    public void put(String key, Object value) {
        if (mProperties == null) {
            mProperties = new HashMap<>();
        }
        mProperties.put(key, value);
    }

    @Nullable
    public Object get(String key) {
        if(mProperties == null)
            return null;
        return mProperties.get(key);
    }


    @Nullable
    public Object remove(String key){
        if(mProperties == null)
            return null;
        return mProperties.remove(key);
    }

    public boolean has(String key) {
        return mProperties.containsKey(key);
    }
}
