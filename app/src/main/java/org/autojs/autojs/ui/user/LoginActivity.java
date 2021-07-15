package org.autojs.autojs.ui.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.theme.ThemeColorManager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityLoginBinding;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.UserService;
import org.autojs.autojs.ui.BaseActivity;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/9/20.
 */
public class LoginActivity extends BaseActivity {

    private ActivityLoginBinding inflate;
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setUpViews();
        inflate.login.setOnClickListener(view -> login());
        inflate.forgotPassword.setOnClickListener(view -> forgotPassword());
    }

    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context context) {
        return new ActivityIntentBuilder<I>(context,LoginActivity.class) {
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                context.startActivity(intent);
                return null;
            }
        };
    }

    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_login));
        ThemeColorManager.addViewBackground(inflate.login);
    }

    void login() {
        String userName = Objects.requireNonNull(inflate.username.getText()).toString();
        String password = Objects.requireNonNull(inflate.password.getText()).toString();
        if (!checkNotEmpty(userName, password)) {
            return;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(R.string.text_logining)
                .cancelable(false)
                .show();
        Disposable subscribe = UserService.getInstance().login(userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), R.string.text_login_succeed, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        , error -> {
                            dialog.dismiss();
                            inflate.password.setError(NodeBB.getErrorMessage(error, LoginActivity.this, R.string.text_login_fail));
                        });
        compositeDisposable.add(subscribe);

    }

    void forgotPassword() {
        WebActivity.intent(this)
                .extra(WebActivity.EXTRA_URL, NodeBB.BASE_URL + "reset")
                .extra(Intent.EXTRA_TITLE, getString(R.string.text_reset_password))
                .start();
    }

    private boolean checkNotEmpty(String userName, String password) {
        if (userName.isEmpty()) {
            inflate.username.setError(getString(R.string.text_username_cannot_be_empty));
            return false;
        }
        if (password.isEmpty()) {
            inflate.username.setError(getString(R.string.text_password_cannot_be_empty));
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_register) {
            RegisterActivity.intent(this).start();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
