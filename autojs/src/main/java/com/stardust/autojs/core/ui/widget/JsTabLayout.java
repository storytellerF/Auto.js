package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;

public class JsTabLayout extends TabLayout {

    public JsTabLayout(@NonNull Context context) {
        super(context);
    }

    public JsTabLayout(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public JsTabLayout(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
