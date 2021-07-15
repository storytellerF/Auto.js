package com.storyteller_f.bandage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author storyteller_f
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface OnTextChanged {
    String tag();
}
