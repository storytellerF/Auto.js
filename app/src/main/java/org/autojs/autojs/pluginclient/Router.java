package org.autojs.autojs.pluginclient;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Stardust on 2017/5/11.
 */

public class Router implements Handler {

    private static final String LOG_TAG = "Router";
    @NonNull
    protected Map<String, Handler> mHandlerMap = new HashMap<>();
    private final String mKey;

    public Router(String key) {
        mKey = key;
    }

    public String getKey() {
        return mKey;
    }

    @NonNull
    public Router handler(String value, Handler handler) {
        mHandlerMap.put(value, handler);
        return this;
    }

    @Override
    public boolean handle(@NonNull JsonObject data) {
        Log.d(LOG_TAG, "handle: " + data);
        JsonElement key = data.get(getKey());
        if (key == null || !key.isJsonPrimitive()) {
            Log.w(LOG_TAG, "no such key: " + getKey());
            return false;
        }
        Handler handler = mHandlerMap.get(key.getAsString());
        return handleInternal(data, key.getAsString(), handler);
    }

    protected boolean handleInternal(JsonObject json, String key, @Nullable Handler handler) {
        return handler != null && handler.handle(json);
    }

    public static class RootRouter extends Router {

        public RootRouter(String key) {
            super(key);
        }

        @Override
        protected boolean handleInternal(@NonNull JsonObject json, String key, @Nullable Handler handler) {
            JsonElement data = json.get("data");
            if (data == null || !data.isJsonObject()) {
                Log.w(LOG_TAG, "json has no object data: " + json);
                return false;
            }
            return handler != null && handler.handle(data.getAsJsonObject());
        }
    }

}
