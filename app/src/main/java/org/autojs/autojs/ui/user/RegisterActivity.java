package org.autojs.autojs.ui.user;

import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.theme.ThemeColorManager;

import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.builder.PostActivityStarter;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityRegisterBinding;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.UserService;
import org.autojs.autojs.ui.BaseActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Stardust on 2017/10/26.
 */
public class RegisterActivity extends BaseActivity {

    @NonNull
    CompositeDisposable compositeDisposable=new CompositeDisposable();
    private ActivityRegisterBinding inflate;

    @NonNull
    public static <I extends ActivityIntentBuilder<I>> ActivityIntentBuilder<I> intent(Context context) {
        return new ActivityIntentBuilder<I>(context,RegisterActivity.class) {
            @Nullable
            @Override
            public PostActivityStarter startForResult(int requestCode) {
                context.startActivity(intent);
                return null;
            }
        };
    }

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(inflate.getRoot());
        setUpViews();
        inflate.register.setOnClickListener(view -> login());
    }

    void setUpViews() {
        setToolbarAsBack(getString(R.string.text_register));
        ThemeColorManager.addViewBackground(inflate.register);
    }

    void login() {
        String email = inflate.email.getText().toString();
        String userName = inflate.username.getText().toString();
        String password = inflate.password.getText().toString();
        if (!validateInput(email, userName, password)) {
            return;
        }
        MaterialDialog dialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .content(R.string.text_registering)
                .cancelable(false)
                .show();
        Disposable subscribe = UserService.getInstance().register(email, userName, password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                            dialog.dismiss();
                            onRegisterResponse(response.string());
                        }
                        , error -> {
                            dialog.dismiss();
                            inflate.password.setError(NodeBB.getErrorMessage(error, RegisterActivity.this, R.string.text_register_fail));
                        });
        compositeDisposable.add(subscribe);

    }

    private void onRegisterResponse(String res) {
        Toast.makeText(this, R.string.text_register_succeed, Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInput(@NonNull String email, @NonNull String userName, @NonNull String password) {
        if (email.isEmpty()) {
            inflate.email.setError(getString(R.string.text_email_cannot_be_empty));
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inflate.email.setError(getString(R.string.text_email_format_error));
            return false;
        }
        if (userName.isEmpty()) {
            inflate.username.setError(getString(R.string.text_username_cannot_be_empty));
            return false;
        }
        if (password.isEmpty()) {
            inflate.username.setError(getString(R.string.text_password_cannot_be_empty));
            return false;
        }
        if (password.length() < 6) {
            inflate.password.setError(getString(R.string.nodebb_error_change_password_error_length));
            return false;
        }
        return true;
    }
}
