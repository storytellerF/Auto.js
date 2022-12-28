package com.stardust.autojs.core.ui.inflater.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/11/3.
 */

public class Colors {

    public static int parse(@NonNull Context context, @NonNull String color) {
        Resources resources = context.getResources();
        if (color.startsWith("@color/")) {
            return resources.getColor(resources.getIdentifier(color.substring("@color/".length()), "color", context.getPackageName()));
        }
        if (color.startsWith("@android:color/")) {
            return Color.parseColor(color.substring(15));
        }
        return Color.parseColor(color);
    }

    public static int parse(@NonNull View view, @NonNull String color) {
        return parse(view.getContext(), color);
    }
}
