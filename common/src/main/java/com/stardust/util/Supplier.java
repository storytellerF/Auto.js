package com.stardust.util;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/5/1.
 */

public interface Supplier<T> {
    @NonNull
    T get();
}
