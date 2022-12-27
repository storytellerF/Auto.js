package com.stardust.autojs.script;

import androidx.annotation.NonNull;

import com.stardust.pio.PFiles;

import java.io.File;

/**
 * Created by Stardust on 2017/8/2.
 */

public class AutoFileSource extends ScriptSource {

    public static final String ENGINE = AutoFileSource.class.getName() + ".Engine";
    @NonNull
    private final File mFile;

    public AutoFileSource(@NonNull File file) {
        super(PFiles.getNameWithoutExtension(file.getAbsolutePath()));
        mFile = file;
    }

    public AutoFileSource(@NonNull String path) {
        this(new File(path));
    }


    @NonNull
    @Override
    public String getEngineName() {
        return ENGINE;
    }

    @NonNull
    public File getFile() {
        return mFile;
    }

    @NonNull
    @Override
    public String toString() {
        return mFile.toString();
    }
}
