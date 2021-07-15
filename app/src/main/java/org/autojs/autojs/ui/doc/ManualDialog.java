package org.autojs.autojs.ui.doc;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.FloatingManualDialogBinding;
import org.autojs.autojs.ui.widget.EWebView;

/**
 * Created by Stardust on 2017/10/24.
 */

public class ManualDialog {

    Dialog mDialog;
    private final Context mContext;
    private final FloatingManualDialogBinding bind;

    public ManualDialog(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.floating_manual_dialog, null);
        bind = FloatingManualDialogBinding.bind(view);
        bind.close.setOnClickListener(view1 -> close());
        bind.fullscreen.setOnClickListener(view1 -> viewInNewActivity());
        mDialog = new MaterialDialog.Builder(context)
                .customView(view, false)
                .build();
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public ManualDialog title(String title) {
        bind.title.setText(title);
        return this;
    }

    public ManualDialog url(String url) {
        bind.ewebView.getWebView().loadUrl(url);
        return this;
    }

    public ManualDialog pinToLeft(View.OnClickListener listener) {
        bind.pinToLeft.setOnClickListener(v -> {
            mDialog.dismiss();
            listener.onClick(v);
        });
        return this;
    }

    public ManualDialog show() {
        mDialog.show();
        return this;
    }

    void close() {
        mDialog.dismiss();
    }

    void viewInNewActivity() {
        mDialog.dismiss();
        DocumentationActivity.intent(mContext)
                .extra(DocumentationActivity.EXTRA_URL, bind.ewebView.getWebView().getUrl())
                .start();
    }

}
