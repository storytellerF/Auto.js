package com.stardust.util;

import static com.stardust.pio.PFiles.getExtension;

import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Created by Stardust on 2018/2/12.
 */

public class MimeTypes {

    @Nullable
    public static String fromFile(@NonNull String path) {
        String ext = getExtension(path);
        return android.text.TextUtils.isEmpty(ext) ? "*/*" : MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    @NonNull
    public static String fromFileOr(@NonNull String path, @NonNull String defaultType) {
        String mimeType = fromFile(path);
        return mimeType == null ? defaultType : mimeType;
    }
}
