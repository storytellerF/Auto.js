package org.autojs.autojs.ui.main.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;

import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.model.script.Scripts;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.download.DownloadManager;
import org.autojs.autojs.ui.common.OptionListView;
import org.autojs.autojs.ui.common.ScriptOperations;
import org.autojs.autojs.ui.filechooser.FileChooserDialogBuilder;
import org.autojs.autojs.ui.widget.EWebView;

import java.util.regex.Pattern;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by Stardust on 2017/10/19.
 */

public class CommunityWebView extends EWebView {

    @NonNull
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String mUrl;
    @Nullable
    private BottomSheetDialog mBottomSheetDialog;

    public CommunityWebView(Context context) {
        super(context);
        init();
    }

    public CommunityWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getWebView().setWebViewClient(new MyWebViewClient());
        getWebView().setWebChromeClient(new MyWebChromeClient());
    }

    private void shouldScriptOptionsDialog(@NonNull String url) {
        mUrl = url;
        String fileName = DownloadManager.parseFileNameLocally(url);
        mBottomSheetDialog = new BottomSheetDialog(getContext());
        OptionListView build = new OptionListView.Builder(getContext())
                .title(fileName)
                .item(R.id.save, R.drawable.ic_file_download_black_48dp, R.string.text_download)
                .item(R.id.run, R.drawable.ic_play_arrow_white_48dp, R.string.text_run)
                .bindItemClick(this)
                .build();
        mBottomSheetDialog.setContentView(build);
        mBottomSheetDialog.show();
    }

    @SuppressLint("CheckResult")
    public
        //@OnClick(R.id.save)
    void save() {
        dismissBottomSheetDialog();
        Disposable subscribe = new ScriptOperations(getContext(), CommunityWebView.this)
                .download(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file ->
                                Snackbar.make(CommunityWebView.this, getResources().getString(R.string.format_file_downloaded, file.getPath())
                                                , Snackbar.LENGTH_LONG)
                                        .setAction(R.string.text_open, v -> Scripts.INSTANCE.edit(getContext(), file))
                                        .show(),
                        error -> {
                            error.printStackTrace();
                            Snackbar.make(CommunityWebView.this, R.string.text_download_failed, Snackbar.LENGTH_SHORT).show();
                        });
        compositeDisposable.add(subscribe);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        compositeDisposable.dispose();
    }

    public void run() {
        dismissBottomSheetDialog();
        Disposable subscribe = new ScriptOperations(getContext(), CommunityWebView.this)
                .temporarilyDownload(mUrl)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(file -> {
                    Snackbar.make(CommunityWebView.this, R.string.text_start_running, Snackbar.LENGTH_SHORT).show();
                    Scripts.INSTANCE.run(file);
                }, error -> {
                    error.printStackTrace();
                    Snackbar.make(CommunityWebView.this, R.string.text_download_failed, Snackbar.LENGTH_SHORT).show();
                });
        compositeDisposable.add(subscribe);
    }

    private void dismissBottomSheetDialog() {
        mBottomSheetDialog.dismiss();
        mBottomSheetDialog = null;
    }


    private class MyWebViewClient extends EWebView.MyWebViewClient {

        private final Pattern UPLOAD_FILE_PATTERN = Pattern.compile(NodeBB.url("assets/uploads/files/.+(\\.js|\\.auto)"));

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, @NonNull String url) {
            if (UPLOAD_FILE_PATTERN.matcher(url).matches()) {
                shouldScriptOptionsDialog(url);
                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            evalJavaScript("$('#header').hide();$('#content').css({ top: '0', position: 'absolute' });");
        }
    }

    private class MyWebChromeClient extends EWebView.MyWebChromeClient {

        @Override
        public boolean openFileChooser(@NonNull ValueCallback<Uri> callback, String[] acceptType) {
            if (super.openFileChooser(callback, acceptType)) {
                return true;
            }
            new FileChooserDialogBuilder(getContext())
                    .title(R.string.text_select_file_to_upload)
                    .dir(Pref.getScriptDirPath())
                    .singleChoice(file -> callback.onReceiveValue(Uri.fromFile(file)))
                    .cancelListener(dialog -> callback.onReceiveValue(null))
                    .show();
            return true;
        }
    }
}
