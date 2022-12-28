package org.autojs.autojs.model.explorer;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.pio.PFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class ExplorerDirPage extends ExplorerFileItem implements ExplorerPage {

    @NonNull
    private List<ExplorerItem> mChildren = Collections.emptyList();

    public ExplorerDirPage(PFile file, ExplorerPage parent) {
        super(file, parent);
    }

    public ExplorerDirPage(String path, ExplorerPage parent) {
        super(path, parent);
    }

    public ExplorerDirPage(@NonNull File file, ExplorerPage parent) {
        super(file, parent);
    }

    @Nullable
    public static ExplorerDirPage createRoot(String path) {
        return new ExplorerDirPage(path, null);
    }

    @Nullable
    public static ExplorerPage createRoot(@NonNull File directory) {
        return new ExplorerDirPage(directory, null);
    }

    public void copyChildren(@NonNull ExplorerPage g) {
        ensureChildListWritable();
        mChildren.clear();
        mChildren.addAll(((ExplorerDirPage) g).mChildren);
    }

    private void ensureChildListWritable() {
        if (mChildren == Collections.EMPTY_LIST) {
            mChildren = new ArrayList<>();
        }
    }

    @Override
    public ExplorerFileItem rename(String newName) {
        return new ExplorerDirPage(getFile().renameTo(newName), getParent());
    }

    protected int indexOf(@NonNull ExplorerItem child) {
        int i = 0;
        for (ExplorerItem item : mChildren) {
            if (item.getPath().equals(child.getPath())) {
                return i;
            }
            i++;
        }
        return -1;
    }

    public boolean updateChild(@NonNull ExplorerItem oldItem, ExplorerItem newItem) {
        int i = indexOf(oldItem);
        if (i < 0) {
            return false;
        }
        mChildren.set(i, newItem);
        return true;
    }

    public boolean removeChild(@NonNull ExplorerItem item) {
        int i = indexOf(item);
        if (i < 0) {
            return false;
        }
        mChildren.remove(i);
        return true;
    }

    public void addChild(ExplorerItem item) {
        ensureChildListWritable();
        mChildren.add(item);
    }

    @NonNull
    @Override
    public Iterator<ExplorerItem> iterator() {
        return mChildren.iterator();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void forEach(@NonNull Consumer<? super ExplorerItem> action) {
        mChildren.forEach(action);
    }

    @NonNull
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public Spliterator<ExplorerItem> spliterator() {
        return mChildren.spliterator();
    }
}
