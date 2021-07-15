package org.autojs.autojs.ui.edit;

import android.content.Context;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.storyteller_f.bandage.OnCheckedChanged;
import com.storyteller_f.bandage.OnTextChanged;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.DialogFindOrReplaceBinding;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.ui.edit.editor.CodeEditor;

import java.util.Objects;

/**
 * Created by Stardust on 2017/9/28.
 */

public class FindOrReplaceDialogBuilder extends ThemeColorMaterialDialogBuilder {

    private static final String KEY_KEYWORDS = "...";

    private final EditorView mEditorView;
    private DialogFindOrReplaceBinding bind;

    public FindOrReplaceDialogBuilder(@NonNull Context context, EditorView editorView) {
        super(context);
        mEditorView = editorView;
        setupViews();
        restoreState();
        autoDismiss(false);
        onNegative((dialog, which) -> dialog.dismiss());
        onPositive((dialog, which) -> {
            storeState();
            findOrReplace(dialog);
        });
    }

    private void setupViews() {
        View view = View.inflate(context, R.layout.dialog_find_or_replace, null);
        bind = DialogFindOrReplaceBinding.bind(view);
        customView(view, true);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
        title(R.string.text_find_or_replace);
    }


    private void storeState() {
        PreferenceManager.getDefaultSharedPreferences(getContext()).edit()
                .putString(KEY_KEYWORDS, Objects.requireNonNull(bind.keywords.getText()).toString())
                .apply();
    }


    private void restoreState() {
        bind.keywords.setText(PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString(KEY_KEYWORDS, ""));
    }

    @OnCheckedChanged(tag = "checkbox_replace_all")
    void syncWithReplaceCheckBox() {
        if (bind.checkboxReplaceAll.isChecked() && !bind.checkboxReplace.isChecked()) {
            bind.checkboxReplace.setChecked(true);
        }
    }

    @OnTextChanged(tag = "replacement")
    void onTextChanged() {
        if (bind.replacement.getText().length() > 0) {
            bind.checkboxReplace.setChecked(true);
        }
    }

    private void findOrReplace(MaterialDialog dialog) {
        String keywords = Objects.requireNonNull(bind.keywords.getText()).toString();
        if (keywords.isEmpty()) {
            return;
        }
        try {
            boolean usingRegex = bind.checkboxRegex.isChecked();
            if (!bind.checkboxReplace.isChecked()) {
                mEditorView.find(keywords, usingRegex);
            } else {
                String replacement = Objects.requireNonNull(bind.replacement.getText()).toString();
                if (bind.checkboxReplaceAll.isChecked()) {
                    mEditorView.replaceAll(keywords, replacement, usingRegex);
                } else {
                    mEditorView.replace(keywords, replacement, usingRegex);
                }
            }
            dialog.dismiss();
        } catch (CodeEditor.CheckedPatternSyntaxException e) {
            e.printStackTrace();
            bind.keywords.setError(getContext().getString(R.string.error_pattern_syntax));
        }

    }

    public FindOrReplaceDialogBuilder setQueryIfNotEmpty(String s) {
        if (!TextUtils.isEmpty(s))
            bind.keywords.setText(s);
        return this;
    }
}
