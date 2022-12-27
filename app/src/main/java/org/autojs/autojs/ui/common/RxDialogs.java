package org.autojs.autojs.ui.common;

import android.content.Context;

import androidx.annotation.NonNull;

import org.autojs.autojs.R;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by Stardust on 2017/10/21.
 */

public class RxDialogs {


    @NonNull
    public static Observable<Boolean> confirm(@NonNull Context context, @NonNull String text) {
        PublishSubject<Boolean> subject = PublishSubject.create();
        new ThemeColorMaterialDialogBuilder(context)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onPositive((dialog, which) -> subject.onNext(true))
                .onNegative((dialog, which) -> subject.onNext(false))
                .content(text)
                .show();
        return subject;
    }

    @NonNull
    public static Observable<Boolean> confirm(@NonNull Context context, int res) {
        return confirm(context, context.getString(res));
    }

}
