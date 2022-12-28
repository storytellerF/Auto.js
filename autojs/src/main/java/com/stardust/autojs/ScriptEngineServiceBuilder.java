package com.stardust.autojs;

import androidx.annotation.NonNull;

import com.stardust.autojs.engine.ScriptEngineManager;
import com.stardust.autojs.runtime.api.Console;
import com.stardust.util.UiHandler;

/**
 * Created by Stardust on 2017/4/2.
 */

public class ScriptEngineServiceBuilder {

    ScriptEngineManager mScriptEngineManager;
    Console mGlobalConsole;
    UiHandler mUiHandler;

    public ScriptEngineServiceBuilder() {

    }

    @NonNull
    public ScriptEngineServiceBuilder uiHandler(UiHandler uiHandler) {
        mUiHandler = uiHandler;
        return this;
    }

    @NonNull
    public ScriptEngineServiceBuilder engineManger(ScriptEngineManager manager) {
        mScriptEngineManager = manager;
        return this;
    }

    @NonNull
    public ScriptEngineServiceBuilder globalConsole(Console console) {
        mGlobalConsole = console;
        return this;
    }

    @NonNull
    public ScriptEngineService build() {
        return new ScriptEngineService(this);
    }


}
