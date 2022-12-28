package org.autojs.autojs.model.script;

import androidx.annotation.NonNull;

import com.stardust.autojs.script.AutoFileSource;
import com.stardust.autojs.script.JavaScriptFileSource;
import com.stardust.autojs.script.ScriptSource;
import com.stardust.pio.PFile;

import java.io.File;

/**
 * Created by Stardust on 2017/1/23.
 */

public class ScriptFile extends PFile {

    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_AUTO = 1;
    public static final int TYPE_JAVA_SCRIPT = 2;

    private int mType = -1;

    public ScriptFile(@NonNull String path) {
        super(path);
    }

    public ScriptFile(String parent, @NonNull String name) {
        super(parent, name);
    }

    public ScriptFile(File parent, @NonNull String child) {
        super(parent, child);
    }

    public ScriptFile(@NonNull File file) {
        super(file.getPath());
    }

    public int getType() {
        if (mType == -1) {
            mType = getName().endsWith(".js") ? TYPE_JAVA_SCRIPT :
                    getName().endsWith(".auto") ? TYPE_AUTO :
                            TYPE_UNKNOWN;
        }
        return mType;
    }

    @Override
    public ScriptFile getParentFile() {
        String p = this.getParent();
        if (p == null)
            return null;
        return new ScriptFile(p);
    }

    @NonNull
    public ScriptSource toSource() {
        if (getType() == TYPE_JAVA_SCRIPT) {
            return new JavaScriptFileSource(this);
        } else {
            return new AutoFileSource(this);
        }
    }
}
