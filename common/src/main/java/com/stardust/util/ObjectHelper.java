package com.stardust.util;

import androidx.annotation.Nullable;

public class ObjectHelper {

    public static void requireNonNull(@Nullable Object obj, String name){
        if(obj == null){
            throw new NullPointerException(name + " should not be null");
        }
    }

    public static void requireNonNull(@Nullable Object obj){
        if(obj == null){
            throw new NullPointerException();
        }
    }


}
