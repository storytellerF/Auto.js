package com.stardust.autojs.rhino;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

/**
 * Created by Stardust on 2018/4/4.
 */

public class NativeJavaObjectWithPrototype extends NativeJavaObject {


    public NativeJavaObjectWithPrototype(Scriptable scope, Object javaObject, Class<?> staticType) {
        super(scope, javaObject, staticType);
    }

    public NativeJavaObjectWithPrototype(Scriptable scope, Object javaObject, Class<?> staticType, boolean isAdapter) {
        super(scope, javaObject, staticType, isAdapter);
    }

    public NativeJavaObjectWithPrototype() {
    }

    @Override
    public boolean has(@NonNull String name, Scriptable start) {
        return super.has(name, start) || (prototype != null && prototype.has(name, start))
                || name.equals("__proto__");
    }

    @Nullable
    @Override
    public Object get(@NonNull String name, Scriptable start) {
        if (name.equals("__proto__")) {
            return prototype;
        }
        if (super.has(name, start)) {
            return super.get(name, start);
        }
        if (prototype == null) {
            return Scriptable.NOT_FOUND;
        }
        return prototype.get(name, start);
    }

    @Override
    public void put(@NonNull String name, Scriptable start, Object value) {
        if (name.equals("__proto__")) {
            prototype = (Scriptable) value;
            return;
        }
        super.put(name, start, value);
    }
}
