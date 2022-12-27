package org.autojs.autojs.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.app.OnActivityResultDelegate;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.databinding.ActivityWebBinding;
import org.autojs.autojs.ui.BaseActivity;

/**
 * Created by Stardust on 2017/10/26.
 */
public class WebActivity extends BaseActivity implements OnActivityResultDelegate.DelegateHost {

    public static final String EXTRA_URL = "url";

    private final OnActivityResultDelegate.Mediator mMediator = new OnActivityResultDelegate.Mediator();
    private ActivityWebBinding inflate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setupViews();
    }

    @NonNull
    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context context) {
        return new ActivityIntentBuilder<I>(context,WebActivity.class) {
            @Nullable
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                return null;
            }
        };
    }


    void setupViews() {
        setToolbarAsBack(getIntent().getStringExtra(Intent.EXTRA_TITLE));
        inflate.ewebView.getWebView().loadUrl(getIntent().getStringExtra(EXTRA_URL));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mMediator.onActivityResult(requestCode, resultCode, data);
    }

    @NonNull
    @Override
    public OnActivityResultDelegate.Mediator getOnActivityResultDelegateMediator() {
        return mMediator;
    }
}
