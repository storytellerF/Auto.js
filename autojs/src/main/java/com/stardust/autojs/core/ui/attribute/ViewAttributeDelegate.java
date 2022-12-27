package com.stardust.autojs.core.ui.attribute;

import android.view.View;

import androidx.annotation.NonNull;

public interface ViewAttributeDelegate {

    interface ViewAttributeGetter {
        @NonNull
        String get(String name);
    }

    interface ViewAttributeSetter {
        void set(String name, String value);
    }

    boolean has(String name);

    @NonNull
    String get(View view, String name, ViewAttributeGetter defaultGetter);

    void set(View view, String name, String value, ViewAttributeSetter defaultSetter);

}
