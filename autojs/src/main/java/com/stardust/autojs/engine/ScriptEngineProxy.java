package com.stardust.autojs.engine;

import androidx.annotation.Nullable;

import com.stardust.autojs.script.ScriptSource;

/**
 * Created by Stardust on 2018/3/17.
 */

public class ScriptEngineProxy<S extends ScriptSource> implements ScriptEngine<S> {

    private final ScriptEngine<S> mScriptEngine;

    public ScriptEngineProxy(ScriptEngine<S> scriptEngine) {
        mScriptEngine = scriptEngine;
    }

    public ScriptEngine<S> getInner() {
        return mScriptEngine;
    }

    @Override
    public void put(String name, Object value) {
        mScriptEngine.put(name, value);
    }

    @Override
    public Object execute(S scriptSource) {
        return mScriptEngine.execute(scriptSource);
    }

    @Override
    public void forceStop() {
        mScriptEngine.forceStop();
    }

    @Override
    public void destroy() {
        mScriptEngine.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return mScriptEngine.isDestroyed();
    }

    @Override
    public void setTag(String key, Object value) {
        mScriptEngine.setTag(key, value);
    }

    @Nullable
    @Override
    public Object getTag(String key) {
        return mScriptEngine.getTag(key);
    }

    @Nullable
    @Override
    public String cwd() {
        return mScriptEngine.cwd();
    }

    @Override
    public void uncaughtException(Throwable throwable) {
        mScriptEngine.uncaughtException(throwable);
    }

    @Override
    public Throwable getUncaughtException() {
        return mScriptEngine.getUncaughtException();
    }

    @Override
    public void setOnDestroyListener(OnDestroyListener listener) {
        mScriptEngine.setOnDestroyListener(listener);
    }

    @Override
    public void init() {
        mScriptEngine.init();
    }

    @Override
    public int getId() {
        return mScriptEngine.getId();
    }

    @Override
    public void setId(int id) {
        mScriptEngine.setId(id);
    }
}
