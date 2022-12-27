package com.stardust.autojs.rhino;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Stardust on 2018/4/4.
 */

public class NativeJavaClassWithPrototype extends NativeJavaClass {

    private static final Object NULL = new Object();
    private final ConcurrentHashMap<String, Object> mProperties = new ConcurrentHashMap<>();

    public NativeJavaClassWithPrototype(Scriptable scope, Class<?> javaClass) {
        super(scope, javaClass);
    }

    @Override
    public boolean has(@NonNull String name, Scriptable start) {
        return mProperties.containsKey(name) || super.has(name, start) || (prototype != null && prototype.has(name, start))
                || name.equals("prototype");
    }

    @Nullable
    @Override
    public Object get(@NonNull String name, Scriptable start) {
        if (name.equals("prototype")) {
            return prototype;
        }
        Object value = mProperties.get(name);
        if (value != null) {
            return unwrapValue(value);
        }
        try {
            value = super.get(name, start);
        } catch (EvaluatorException e) {
            if (!memberNotFound(e)) {
                throw e;
            }
        }
        if (value != Scriptable.NOT_FOUND) {
            return value;
        }
        if (prototype == null) {
            return Scriptable.NOT_FOUND;
        }
        return prototype.get(name, start);
    }

    @Nullable
    private Object unwrapValue(Object value) {
        if (value == NULL)
            return null;
        return value;
    }

    @Override
    public void put(@NonNull String name, Scriptable start, Object value) {
        if (name.equals("prototype")) {
            prototype = (Scriptable) value;
            return;
        }
        if (mProperties.containsKey(name)) {
            mProperties.put(name, wrapValue(value));
        }
        try {
            super.put(name, start, value);
        } catch (EvaluatorException e) {
            if (memberNotFound(e)) {
                mProperties.put(name, wrapValue(value));
            } else {
                throw e;
            }
        }
    }

    @Nullable
    private Object wrapValue(@Nullable Object value) {
        if (value == null)
            return NULL;
        return value;
    }

    private static boolean memberNotFound(@NonNull EvaluatorException e) {
        return e.getMessage() != null && e.getMessage().startsWith("Java class \"com.stardust.autojs.rhino.NativeJavaObjectWithPrototype\"");
    }

}
