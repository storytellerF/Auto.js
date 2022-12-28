package org.autojs.autojs.ui.log;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.autojs.core.console.ConsoleImpl;

import org.autojs.autojs.IntentWrapper;
import org.autojs.autojs.R;
import org.autojs.autojs.autojs.AutoJs;
import org.autojs.autojs.databinding.ActivityLogBinding;
import org.autojs.autojs.ui.BaseActivity;

public class LogActivity extends BaseActivity {

    private ConsoleImpl mConsoleImpl;
    private ActivityLogBinding inflate;

    @NonNull
    public static IntentWrapper intent(Context mainActivity) {
        return new IntentWrapper(mainActivity, new Intent(mainActivity, LogActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityLogBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setupViews();
        inflate.fab.setOnClickListener(view -> clearConsole());
        applyDayNightMode();
    }

    void setupViews() {
        setToolbarAsBack(getString(R.string.text_log));
        mConsoleImpl = AutoJs.getInstance().getGlobalConsole();
        inflate.console.setConsole(mConsoleImpl);
        inflate.console.findViewById(R.id.input_container).setVisibility(View.GONE);
    }

    void clearConsole() {
        mConsoleImpl.clear();
    }
}
