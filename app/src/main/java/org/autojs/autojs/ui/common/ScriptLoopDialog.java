package org.autojs.autojs.ui.common;

import android.content.Context;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.app.DialogUtils;
import com.stardust.app.GlobalAppContext;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.DialogScriptLoopBinding;
import org.autojs.autojs.model.script.ScriptFile;
import org.autojs.autojs.model.script.Scripts;

import java.util.Objects;


/**
 * Created by Stardust on 2017/7/8.
 */

public class ScriptLoopDialog {

    private final ScriptFile mScriptFile;
    private final MaterialDialog mDialog;
    @NonNull
    private final DialogScriptLoopBinding bind;


    public ScriptLoopDialog(@NonNull Context context, ScriptFile file) {
        mScriptFile = file;
        View view = View.inflate(context, R.layout.dialog_script_loop, null);
        mDialog = new MaterialDialog.Builder(context)
                .title(R.string.text_run_repeatedly)
                .customView(view, true)
                .positiveText(R.string.ok)
                .onPositive((dialog, which) -> startScriptRunningLoop())
                .build();
        bind = DialogScriptLoopBinding.bind(view);
    }

    private void startScriptRunningLoop() {
        try {
            int loopTimes = Integer.parseInt(Objects.requireNonNull(bind.loopTimes.getText()).toString());
            float loopInterval = Float.parseFloat(Objects.requireNonNull(bind.loopInterval.getText()).toString());
            float loopDelay = Float.parseFloat(Objects.requireNonNull(bind.loopDelay.getText()).toString());
            Scripts.INSTANCE.runRepeatedly(mScriptFile, loopTimes, (long) (1000L * loopDelay), (long) (loopInterval * 1000L));
        } catch (NumberFormatException e) {
            GlobalAppContext.toast(R.string.text_number_format_error);
        }
    }

    @NonNull
    public ScriptLoopDialog windowType(int windowType) {
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setType(windowType);
        }
        return this;
    }

    public void show() {
        DialogUtils.showDialog(mDialog);
    }

}
