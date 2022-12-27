package org.autojs.autojs.storage.file;

import androidx.annotation.NonNull;

import com.stardust.pio.PFiles;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.Observer;

public class FileObservable {

    @NonNull
    public static Observable<File> copy(@NonNull String fromPath, @NonNull String toPath) {
        return copy(fromPath, toPath, false);
    }

    @NonNull
    public static Observable<File> move(@NonNull String fromPath, @NonNull String toPath) {
        return copy(fromPath, toPath, true);
    }

    @NonNull
    private static Observable<File> copy(@NonNull String fromPath, @NonNull String toPath, boolean deleteOld) {
        return new Observable<File>() {
            @Override
            protected void subscribeActual(@NonNull Observer<? super File> observer) {
                try {
                    copy(new File(fromPath), new File(toPath), deleteOld, observer);
                    observer.onComplete();
                } catch (IOException e) {
                    observer.onError(e);
                }
            }
        };
    }

    private static void copyDir(@NonNull File fromDir, File toDir, boolean deleteOld, @NonNull Observer<? super File> progress) throws IOException {
        if (!fromDir.isDirectory()) {
            return;
        }
        File[] files = fromDir.listFiles();
        if (files == null || files.length == 0) {
            return;
        }
        for (File file : files) {
            copy(file, new File(toDir, file.getName()), deleteOld, progress);
        }
    }

    private static void copy(@NonNull File fromFile, @NonNull File toFile, boolean deleteOld, @NonNull Observer<? super File> progress) throws IOException {
        progress.onNext(fromFile);
        if (fromFile.isDirectory()) {
            copyDir(fromFile, toFile, deleteOld, progress);
        } else {
            PFiles.ensureDir(toFile.getPath());
            FileUtils.copyFile(fromFile, toFile);
        }
        if (deleteOld) {
            fromFile.delete();
        }
    }


}
