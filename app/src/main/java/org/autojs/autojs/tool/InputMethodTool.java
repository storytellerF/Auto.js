package org.autojs.autojs.tool;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/12/9.
 */

public class InputMethodTool {

    public static void dismissInputMethod(@NonNull Context context, @NonNull View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null)
            return;
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
