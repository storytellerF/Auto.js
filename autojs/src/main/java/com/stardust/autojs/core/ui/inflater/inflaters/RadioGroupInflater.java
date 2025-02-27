package com.stardust.autojs.core.ui.inflater.inflaters;

import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.core.ui.inflater.ResourceParser;
import com.stardust.autojs.core.ui.inflater.util.Ids;

import java.util.Map;

/**
 * Created by Stardust on 2017/11/29.
 */

public class RadioGroupInflater<V extends RadioGroup> extends LinearLayoutInflater<V> {

    @Nullable
    private Integer mCheckedButton;

    public RadioGroupInflater(ResourceParser resourceParser) {
        super(resourceParser);
    }

    @Override
    public boolean setAttr(V view, @NonNull String attr, String value, ViewGroup parent, Map<String, String> attrs) {
        if (attr.equals("checkedButton")) {
            mCheckedButton = Ids.parse(value);
            return true;
        } else {
            return super.setAttr(view, attr, value, parent, attrs);
        }
    }

    @Override
    public void applyPendingAttributesOfChildren(@NonNull V view) {
        if (mCheckedButton != null) {
            view.check(mCheckedButton);
            mCheckedButton = null;
        }
    }
}
