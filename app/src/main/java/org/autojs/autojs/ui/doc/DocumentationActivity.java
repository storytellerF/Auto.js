package org.autojs.autojs.ui.doc;

import android.content.Context;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.Pref;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityDocumentationBinding;
import org.autojs.autojs.ui.BaseActivity;

/**
 * Created by Stardust on 2017/10/24.
 */
public class DocumentationActivity extends BaseActivity {

    public static final String EXTRA_URL = "url";

    WebView mWebView;
    private ActivityDocumentationBinding inflate;

    @NonNull
    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context mContext) {
        return new ActivityIntentBuilder<I>(mContext, DocumentationActivity.class) {
            @Nullable
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                context.startActivity(intent);
                return null;
            }
        };
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityDocumentationBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setUpViews();
    }

    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_tutorial));
        mWebView = inflate.ewebView.getWebView();
        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url == null) {
            url = Pref.getDocumentationUrl() + "index.html";
        }
        mWebView.loadUrl(url);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
