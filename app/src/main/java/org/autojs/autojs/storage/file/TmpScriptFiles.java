package org.autojs.autojs.storage.file;

import android.content.Context;

import androidx.annotation.NonNull;

import com.stardust.pio.PFiles;

import java.io.File;
import java.io.IOException;

/**
 * Created by Stardust on 2017/10/21.
 */

public class TmpScriptFiles {

    @NonNull
    public static File create(@NonNull Context context) throws IOException {
        ensureTmpDir(context);
        File tmp = new File(getTmpDir(context), "tmp-" + System.currentTimeMillis() + ".js");
        tmp.createNewFile();
        return tmp;
    }

    public static void clearTmpDir(@NonNull Context context) {
        File dir = getTmpDir(context);
        PFiles.deleteRecursively(dir);
    }

    @NonNull
    public static File getTmpDir(@NonNull Context context) {
        return new File(context.getCacheDir(), "tmp_scripts/");
    }

    private static void ensureTmpDir(@NonNull Context context) {
        File dir = getTmpDir(context);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
