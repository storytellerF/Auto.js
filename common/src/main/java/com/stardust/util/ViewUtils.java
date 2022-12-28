package com.stardust.util;

import android.content.Context;
import android.view.View;
import android.view.ViewParent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Stardust on 2017/7/2.
 */

public class ViewUtils {

    @Nullable
    public static View findParentById(@NonNull View view, int id) {
        ViewParent parent = view.getParent();
        if (parent == null || !(parent instanceof View))
            return null;
        View viewParent = (View) parent;
        if (viewParent.getId() == id) {
            return viewParent;
        }
        return findParentById(viewParent, id);
    }

    public static float pxToSp(@NonNull Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }
}
