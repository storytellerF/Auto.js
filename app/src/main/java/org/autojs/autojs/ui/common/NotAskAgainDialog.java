package org.autojs.autojs.ui.common;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.util.HashUtils;

import org.autojs.autojs.R;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;


/**
 * Created by Stardust on 2017/1/30.
 */

public class NotAskAgainDialog extends MaterialDialog {

    protected NotAskAgainDialog(@NonNull Builder builder) {
        super(builder);
    }


    public static class Builder extends ThemeColorMaterialDialogBuilder {

        private String mKeyRemind;
        private boolean mRemind;

        public Builder(@NonNull Context context) {
            this(context, null);
        }

        public Builder(@NonNull Context context, String key) {
            super(context);
            mKeyRemind = key;
            readRemindStatus();
            checkBoxPrompt(context.getString(R.string.text_do_not_remind_again), false, (buttonView, isChecked) -> setRemindState(!isChecked));
        }

        @Nullable
        public MaterialDialog show() {
            if (mRemind) {
                return super.show();
            }
            return null;
        }

        private void setRemindState(boolean remind) {
            mRemind = remind;
            PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                    .putBoolean(mKeyRemind, remind).apply();
        }

        private void readRemindStatus() {
            generatePreferenceKeyIfNeeded();
            mRemind = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(mKeyRemind, true);
        }

        private void generatePreferenceKeyIfNeeded() {
            if (mKeyRemind == null)
                mKeyRemind = HashUtils.md5(TextUtils.join("", Thread.currentThread().getStackTrace()));
        }
    }
}
