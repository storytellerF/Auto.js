package com.stardust.autojs.runtime.api;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.stardust.autojs.R;
import com.stardust.autojs.annotation.ScriptInterface;
import com.stardust.autojs.annotation.ScriptVariable;
import com.stardust.autojs.core.ui.dialog.BlockedMaterialDialog;
import com.stardust.autojs.core.ui.dialog.JsDialogBuilder;
import com.stardust.autojs.runtime.ScriptRuntime;
import com.stardust.util.ArrayUtils;

/**
 * Created by Stardust on 2017/5/8.
 */

public class Dialogs {

    @ScriptVariable
    public final NonUiDialogs nonUiDialogs = new NonUiDialogs();
    private final ScriptRuntime mRuntime;
    private ContextThemeWrapper mThemeWrapper;

    public Dialogs(ScriptRuntime runtime) {
        mRuntime = runtime;
    }

    @Nullable
    @ScriptInterface
    public Object rawInput(@NonNull String title, String prefill, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .input(null, prefill, true)
                .title(title))
                .showAndGet();
    }

    @Nullable
    @ScriptInterface
    public Object alert(@NonNull String title, @NonNull String content, Object callback) {
        MaterialDialog.Builder builder = dialogBuilder(callback)
                .alert()
                .title(title)
                .positiveText(R.string.ok);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        return ((BlockedMaterialDialog.Builder) builder).showAndGet();
    }

    @Nullable
    @ScriptInterface
    public Object confirm(@NonNull String title, @NonNull String content, Object callback) {
        MaterialDialog.Builder builder = dialogBuilder(callback)
                .confirm()
                .title(title)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel);
        if (!TextUtils.isEmpty(content)) {
            builder.content(content);
        }
        return ((BlockedMaterialDialog.Builder) builder).showAndGet();
    }

    private Context getContext() {
        if (mThemeWrapper != null)
            return mThemeWrapper;
        mThemeWrapper = new ContextThemeWrapper(mRuntime.uiHandler.getContext().getApplicationContext(), R.style.Theme_AppCompat_Light);
        return mThemeWrapper;
    }

    @Nullable
    @ScriptInterface
    public Object select(@NonNull String title, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallback()
                .title(title)
                .items(items))
                .showAndGet();
    }

    @NonNull
    private String[] getItems(@NonNull Object[] args) {
        int len = 0;
        if (args.length > 1) {
            if (args[args.length - 1] instanceof CharSequence) {
                len = args.length;
            } else {
                len = args.length - 1;
            }
        }
        String[] items = new String[len];
        for (int i = 0; i < len; i++) {
            items[i] = args[i] == null ? null : args[i].toString();
        }
        return items;
    }

    @Nullable
    @ScriptInterface
    public Object singleChoice(@NonNull String title, int selectedIndex, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallbackSingleChoice(selectedIndex)
                .title(title)
                .positiveText(R.string.ok)
                .items(items))
                .showAndGet();
    }

    @Nullable
    @ScriptInterface
    public Object multiChoice(@NonNull String title, @NonNull int[] indices, String[] items, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .itemsCallbackMultiChoice(ArrayUtils.box(indices))
                .title(title)
                .positiveText(R.string.ok)
                .items(items))
                .showAndGet();
    }

    @Nullable
    @ScriptInterface
    public Object selectFile(@NonNull String title, String prefill, Object callback) {
        return ((BlockedMaterialDialog.Builder) dialogBuilder(callback)
                .input(null, prefill, true)
                .title(title))
                .showAndGet();
    }

    @NonNull
    private BlockedMaterialDialog.Builder dialogBuilder(Object callback) {
        Context context = mRuntime.app.getCurrentActivity();
        if (context == null || ((Activity) context).isFinishing()) {
            context = getContext();
        }
        return (BlockedMaterialDialog.Builder) new BlockedMaterialDialog.Builder(context, mRuntime, callback)
                .theme(Theme.LIGHT);
    }

    @ScriptInterface
    public MaterialDialog.Builder newBuilder() {
        Context context = mRuntime.app.getCurrentActivity();
        if (context == null || ((Activity) context).isFinishing()) {
            context = getContext();
        }
        return new JsDialogBuilder(context, mRuntime)
                .theme(Theme.LIGHT);
    }

    public class NonUiDialogs {

        @Nullable
        public String rawInput(@NonNull String title, String prefill, Object callback) {
            return (String) Dialogs.this.rawInput(title, prefill, callback);
        }

        @ScriptInterface
        public boolean confirm(@NonNull String title, @NonNull String content, Object callback) {
            return (boolean) Dialogs.this.confirm(title, content, callback);
        }

        @ScriptInterface
        public int select(@NonNull String title, String[] items, Object callback) {
            return (Integer) Dialogs.this.select(title, items, callback);
        }

        @ScriptInterface
        public int singleChoice(@NonNull String title, int selectedIndex, String[] items, Object callback) {
            return (int) Dialogs.this.singleChoice(title, selectedIndex, items, callback);
        }

        @Nullable
        @ScriptInterface
        public int[] multiChoice(@NonNull String title, @NonNull int[] indices, String[] items, Object callback) {
            return (int[]) Dialogs.this.multiChoice(title, indices, items, callback);
        }

        @Nullable
        @ScriptInterface
        public Object alert(@NonNull String title, @NonNull String content, Object callback) {
            return Dialogs.this.alert(title, content, callback);
        }

    }
}
