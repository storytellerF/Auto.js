package com.stardust.autojs.runtime;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/7/21.
 */

public class ScriptBridges {


    private Bridges mBridges;

    public Bridges getBridges() {
        return mBridges;
    }

    public void setBridges(Bridges bridges) {
        mBridges = bridges;
    }

    @NonNull
    public Object callFunction(Object func, Object target, Object args) {
        checkBridges();
        return mBridges.call(func, target, args);
    }

    private void checkBridges() {
        if (mBridges == null)
            throw new IllegalStateException("no bridges set");
    }

    @NonNull
    public Object toArray(Iterable c) {
        checkBridges();
        return mBridges.toArray(c);
    }

    @NonNull
    public Object toString(Object obj) {
        checkBridges();
        return mBridges.toString(obj);
    }

    @NonNull
    public Object asArray(Object obj) {
        checkBridges();
        return mBridges.asArray(obj);
    }

    public interface Bridges {

        Object[] NO_ARGUMENTS = new Object[0];

        @NonNull
        Object call(Object func, Object target, Object arg);

        @NonNull
        Object toArray(Iterable o);

        @NonNull
        Object toString(Object obj);

        @NonNull
        Object asArray(Object obj);
    }
}
