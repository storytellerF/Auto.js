package org.autojs.autojs.model.explorer;

import androidx.annotation.NonNull;

import com.stardust.pio.PFile;

import java.io.File;

public class ExplorerSampleItem extends ExplorerFileItem {
    public ExplorerSampleItem(PFile file, ExplorerPage parent) {
        super(file, parent);
    }

    public ExplorerSampleItem(@NonNull String path, ExplorerPage parent) {
        super(path, parent);
    }

    public ExplorerSampleItem(@NonNull File file, ExplorerPage parent) {
        super(file, parent);
    }

    @Override
    public boolean canDelete() {
        return false;
    }

    @Override
    public boolean canRename() {
        return false;
    }
}
