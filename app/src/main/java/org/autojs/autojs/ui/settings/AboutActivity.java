package org.autojs.autojs.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.afollestad.materialdialogs.MaterialDialog;
import com.stardust.util.IntentUtil;
import com.storyteller_f.bandage.Bandage;
import com.storyteller_f.bandage.Click;

import org.autojs.autojs.BuildConfig;
import org.autojs.autojs.R;
import org.autojs.autojs.databinding.ActivityAboutBinding;
import org.autojs.autojs.theme.dialog.ThemeColorMaterialDialogBuilder;
import org.autojs.autojs.tool.IntentTool;
import org.autojs.autojs.ui.BaseActivity;

/**
 * Created by Stardust on 2017/2/2.
 */
public class AboutActivity extends BaseActivity {

    private int mLolClickCount = 0;
    private ActivityAboutBinding inflate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inflate = ActivityAboutBinding.inflate(getLayoutInflater());
        inflate.include.github.setTag("github");
        inflate.include.qq.setTag("qq");
        inflate.include.email.setTag("email");
        inflate.share.setTag("share");
        inflate.icon.setTag("icon");
        inflate.include.developer.setTag("developer");
        Bandage.bind(this,inflate.getRoot());
        setContentView(inflate.getRoot());
        setUpViews();
    }

    void setUpViews() {
        setVersionName();
        setToolbarAsBack(getString(R.string.text_about));
    }

    @SuppressLint("SetTextI18n")
    private void setVersionName() {
        inflate.version.setText("Version " + BuildConfig.VERSION_NAME);
    }

    @Click(tag = "github")
    void openGitHub() {
        IntentTool.browse(this, getString(R.string.my_github));
    }

    @Click(tag = "qq")
    void openQQToChatWithMe() {
        String qq = getString(R.string.qq);
        if (!IntentUtil.chatWithQQ(this, qq)) {
            Toast.makeText(this, R.string.text_mobile_qq_not_installed, Toast.LENGTH_SHORT).show();
        }
    }

    @Click(tag = "email")
    void openEmailToSendMe() {
        String email = getString(R.string.email);
        IntentUtil.sendMailTo(this, email);
    }


    @Click(tag = "share")
    void share() {
        IntentUtil.shareText(this, getString(R.string.share_app));
    }

    @Click(tag = "icon")
    void lol() {
        mLolClickCount++;
        //Toast.makeText(this, R.string.text_lll, Toast.LENGTH_LONG).show();
        if (mLolClickCount >= 5) {
            crashTest();
            //showEasterEgg();
        }
    }

    private void showEasterEgg() {
        new MaterialDialog.Builder(this)
                .customView(R.layout.paint_layout, false)
                .show();
    }

    private void crashTest() {
        new ThemeColorMaterialDialogBuilder(this)
                .title("Crash Test")
                .positiveText("Crash")
                .onPositive((dialog, which) -> {
//                    CrashReport.testJavaCrash();
                }).show();
    }

    @Click(tag = "developer")
    void hhh() {
        Toast.makeText(this, R.string.text_it_is_the_developer_of_app, Toast.LENGTH_LONG).show();
    }


}
