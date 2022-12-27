package com.stardust.util;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;

import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Stardust on 2017/1/24.
 */

public class ViewUtil {

    @SuppressWarnings("unchecked")
    public static <V extends View> V $(@NonNull View view, @IdRes int resId) {
        return view.findViewById(resId);
    }

    // FIXME: 2018/1/23 not working in some devices (https://github.com/hyb1996/Auto.js/issues/268)
    public static int getStatusBarHeight(@NonNull Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getScreenHeight(@NonNull Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    public static int getScreenWidth(@NonNull Activity activity) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.widthPixels;
    }


    public static void setViewMeasure(@NonNull View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }
}
