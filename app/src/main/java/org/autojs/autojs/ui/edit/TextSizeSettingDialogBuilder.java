package org.autojs.autojs.ui.edit;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.DialogTextSizeSettingBinding;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;

/**
 * Created by Stardust on 2018/2/24.
 */

public class TextSizeSettingDialogBuilder extends ThemeColorMaterialDialogBuilder implements SeekBar.OnSeekBarChangeListener {


    @NonNull
    private final DialogTextSizeSettingBinding bind;

    public interface PositiveCallback {

        void onPositive(int value);
    }

    private static final int MIN = 8;

    private int mTextSize;
    private MaterialDialog mMaterialDialog;

    public TextSizeSettingDialogBuilder(@NonNull Context context) {
        super(context);
        View view = View.inflate(context, R.layout.dialog_text_size_setting, null);
        bind = DialogTextSizeSettingBinding.bind(view);
        customView(view, false);
        title(R.string.text_text_size);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        bind.seekbar.setOnSeekBarChangeListener(this);
    }

    private void setTextSize(int textSize) {
        mTextSize = textSize;
        String title = getContext().getString(R.string.text_size_current_value, textSize);
        if (mMaterialDialog != null) {
            mMaterialDialog.setTitle(title);
        } else {
            title(title);
        }
        bind.previewText.setTextSize(textSize);
    }

    @NonNull
    public TextSizeSettingDialogBuilder initialValue(int value) {
        bind.seekbar.setProgress(value - MIN);
        return this;
    }

    @NonNull
    public TextSizeSettingDialogBuilder callback(@NonNull PositiveCallback callback) {
        onPositive((dialog, which) -> callback.onPositive(mTextSize));
        return this;
    }

    @Override
    public MaterialDialog build() {
        mMaterialDialog = super.build();
        return mMaterialDialog;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setTextSize(progress + MIN);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
