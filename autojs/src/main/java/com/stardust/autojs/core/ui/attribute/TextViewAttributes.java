package com.stardust.autojs.core.ui.attribute;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.stardust.autojs.core.ui.inflater.ResourceParser;

public class TextViewAttributes extends ViewAttributes {

    public TextViewAttributes(@NonNull ResourceParser resourceParser, View view) {
        super(resourceParser, view);
    }

    @Override
    protected void onRegisterAttrs() {
        super.onRegisterAttrs();
    }

    @NonNull
    @Override
    public TextView getView() {
        return (TextView) super.getView();
    }
}
