package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

/**
 * Created by Stardust on 2017/9/18.
 */

public class SwitchCompat extends androidx.appcompat.widget.SwitchCompat {

    private boolean mIgnoreCheckedChange = false;

    public SwitchCompat(@NonNull Context context) {
        super(context);
    }

    public SwitchCompat(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwitchCompat(@NonNull Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setOnCheckedChangeListener(@NonNull final OnCheckedChangeListener listener) {
        super.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (mIgnoreCheckedChange) {
                return;
            }
            listener.onCheckedChanged(buttonView, isChecked);
        });
    }

    public void setChecked(boolean checked, boolean notify) {
        mIgnoreCheckedChange = !notify;
        super.setChecked(checked);
        mIgnoreCheckedChange = false;
    }

    public void toggle(boolean notify) {
        setChecked(!isChecked(), notify);
    }
}
