package com.stardust.util;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/7/7.
 */

public interface Func1<T, R> {

    @NonNull
    R call(T t);

}
