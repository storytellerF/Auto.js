package com.storyteller_f.bandage;

import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author storyteller_f
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Click {
    @NonNull String tag();
}
