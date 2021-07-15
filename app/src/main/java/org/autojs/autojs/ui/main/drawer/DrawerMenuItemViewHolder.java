package org.autojs.autojs.ui.main.drawer;

import android.view.View;
import android.widget.Toast;

import org.autojs.autojs.R;
import org.autojs.autojs.databinding.DrawerMenuItemBinding;
import org.autojs.autojs.ui.widget.BindableViewHolder;
import org.autojs.autojs.ui.widget.SwitchCompat;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Stardust on 2017/12/10.
 */

public class DrawerMenuItemViewHolder extends BindableViewHolder<DrawerMenuItem> {

    private static final long CLICK_TIMEOUT = 1000;

    private boolean mAntiShake;
    private long mLastClickMillis;
    private DrawerMenuItem mDrawerMenuItem;
    private final DrawerMenuItemBinding bind;

    public DrawerMenuItemViewHolder(View itemView) {
        super(itemView);
        bind = DrawerMenuItemBinding.bind(itemView);
        bind.sw.setOnCheckedChangeListener((buttonView, isChecked) -> onClick());
        itemView.setOnClickListener(v -> {
            if (bind.sw.getVisibility() == VISIBLE) {
                bind.sw.toggle();
            } else {
                onClick();
            }
        });
    }

    @Override
    public void bind(DrawerMenuItem item, int position) {
        mDrawerMenuItem = item;
        bind.icon.setImageResource(item.getIcon());
        bind.title.setText(item.getTitle());
        mAntiShake = item.antiShake();
        setSwitch(item);
        setProgress(item.isProgress());
        setNotifications(item.getNotificationCount());
    }

    private void setNotifications(int notificationCount) {
        if (notificationCount == 0) {
            bind.notifications.setVisibility(View.GONE);
        } else {
            bind.notifications.setVisibility(View.VISIBLE);
            bind.notifications.setText(String.valueOf(notificationCount));
        }
    }

    private void setSwitch(DrawerMenuItem item) {
        if (!item.isSwitchEnabled()) {
            bind.sw.setVisibility(GONE);
            return;
        }
        bind.sw.setVisibility(VISIBLE);
        int prefKey = item.getPrefKey();
        if (prefKey == 0) {
            bind.sw.setChecked(item.isChecked(), false);
            bind.sw.setPrefKey(null);
        } else {
            bind.sw.setPrefKey(itemView.getResources().getString(prefKey));
        }
    }

    private void onClick() {
        mDrawerMenuItem.setChecked(bind.sw.isChecked());
        if (mAntiShake && (System.currentTimeMillis() - mLastClickMillis < CLICK_TIMEOUT)) {
            Toast.makeText(itemView.getContext(), R.string.text_click_too_frequently, Toast.LENGTH_SHORT).show();
            bind.sw.setChecked(!bind.sw.isChecked(), false);
            return;
        }
        mLastClickMillis = System.currentTimeMillis();
        if (mDrawerMenuItem != null) {
            mDrawerMenuItem.performAction(this);
        }
    }

    private void setProgress(boolean onProgress) {
        bind.progressBar.setVisibility(onProgress ? VISIBLE : GONE);
        bind.icon.setVisibility(onProgress ? GONE : VISIBLE);
        bind.sw.setEnabled(!onProgress);
        itemView.setEnabled(!onProgress);
    }

    public SwitchCompat getSwitchCompat() {
        return bind.sw;
    }

}
