package org.autojs.autojs.ui.edit.debug;

import androidx.annotation.NonNull;

import com.stardust.autojs.rhino.debug.Debugger;

import org.autojs.autojs.autojs.AutoJs;
import org.mozilla.javascript.ContextFactory;

public class DebuggerSingleton {

    private static final Debugger sDebugger = new Debugger(AutoJs.getInstance().getScriptEngineService(), ContextFactory.getGlobal());

    @NonNull
    public static Debugger get() {
        return sDebugger;
    }
}
