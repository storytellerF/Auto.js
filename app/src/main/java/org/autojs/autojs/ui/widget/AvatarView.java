package org.autojs.autojs.ui.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import org.autojs.autojs.databinding.AvatarViewBinding;
import org.autojs.autojs.network.NodeBB;
import org.autojs.autojs.network.entity.user.User;

/**
 * Created by 婷 on 2017/9/29.
 */

public class AvatarView extends FrameLayout {

    private GradientDrawable mIconTextBackground;
    private AvatarViewBinding inflate;


    public AvatarView(@NonNull Context context) {
        super(context);
        init();
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvatarView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate = AvatarViewBinding.inflate(LayoutInflater.from(getContext()), this, true);
        mIconTextBackground = (GradientDrawable) inflate.iconText.getBackground();
    }

    public void setIcon(int resId) {
        inflate.icon.setVisibility(View.VISIBLE);
        inflate.iconText.setVisibility(View.GONE);
        inflate.icon.setImageResource(resId);
    }

    public void setUser(@NonNull final User user) {
        if (TextUtils.isEmpty(user.getPicture())) {
            inflate.icon.setVisibility(View.GONE);
            inflate.iconText.setVisibility(View.VISIBLE);
            mIconTextBackground.setColor(Color.parseColor(user.getIconBgColor()));
            mIconTextBackground.setCornerRadius((float) (getWidth() * 1.0 / 2));
            inflate.iconText.setText(user.getIconText());
        } else {
            inflate.icon.setVisibility(View.VISIBLE);
            inflate.iconText.setVisibility(View.GONE);
            inflate.icon.setCornerRadius((float) (getWidth() * 1.0 / 2));
            Glide.with(getContext())
                    .load(NodeBB.BASE_URL + user.getPicture())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                    )
                    .into(inflate.icon);
        }
    }
}

