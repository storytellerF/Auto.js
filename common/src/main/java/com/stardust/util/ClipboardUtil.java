package com.stardust.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Created by Stardust on 2017/3/10.
 */

public class ClipboardUtil {


    public static void setClip(@NonNull Context context, CharSequence text) {
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("", text));
    }

    @Nullable
    public static CharSequence getClip(@NonNull Context context) {
        ClipData clip = ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        return (clip == null || clip.getItemCount() == 0) ? null : clip.getItemAt(0).getText();
    }

    @NonNull
    public static CharSequence getClipOrEmpty(@NonNull Context context) {
        CharSequence clip = getClip(context);
        if (clip == null) {
            return "";
        }
        return clip;
    }
}
