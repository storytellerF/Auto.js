package android.widget;

import androidx.annotation.Nullable;

import java.lang.reflect.Field;

public class TextViewHelper {

    @Nullable
    private static final Field sSavedStateText;

    static {
        Field text = null;
        try {
            text = TextView.SavedState.class.getDeclaredField("text");
            text.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        sSavedStateText = text;
    }

    public static void setText(TextView.SavedState state, CharSequence text) {
        if (sSavedStateText == null) {
            return;
        }
        try {
            sSavedStateText.set(state, text);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
