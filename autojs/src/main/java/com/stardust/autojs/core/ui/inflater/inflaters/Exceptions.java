package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/11/4.
 */

public class Exceptions {

    public static final RuntimeException NO_EXCEPTION = new RuntimeException();
    private static ExceptionHandler sExceptionHandler;

    public static void unsupports(@NonNull View v, String name, String value) {
        UnsupportedOperationException e = new UnsupportedOperationException(String.format("Attr %s:%s=\"%s\" is not supported",
                v.getClass().getSimpleName(), name, value));
        if (sExceptionHandler == null || !sExceptionHandler.handleUnsupportedException(e, v, name, value)) {
            throw e;
        }

    }

    public static ExceptionHandler getExceptionHandler() {
        return sExceptionHandler;
    }

    public static void setExceptionHandler(ExceptionHandler exceptionHandler) {
        sExceptionHandler = exceptionHandler;
    }

    public interface ExceptionHandler {
        boolean handleUnsupportedException(UnsupportedOperationException e, View v, String attrName, String value);
    }
}
