package org.autojs.autojs.tool;

import androidx.annotation.NonNull;

import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.model.explorer.ExplorerFileItem;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class Observers {

    private static final Consumer CONSUMER = ignored -> {

    };

    private static final Consumer<Throwable> TOAST_MESSAGE = e -> {
        e.printStackTrace();
        GlobalAppContext.toast(e.getMessage());
    };


    @NonNull
    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> emptyConsumer() {
        return CONSUMER;
    }

    @NonNull
    public static Consumer<Throwable> toastMessage() {
        return TOAST_MESSAGE;
    }

    @NonNull
    public  static <T> Observer<T> emptyObserver() {
        return new Observer<T>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(T t) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

    }
}
