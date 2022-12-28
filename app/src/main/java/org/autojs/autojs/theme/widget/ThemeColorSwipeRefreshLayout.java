package org.autojs.autojs.theme.widget;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.stardust.theme.ThemeColor;
import com.stardust.theme.ThemeColorManager;
import com.stardust.theme.ThemeColorMutable;

/**
 * Created by Stardust on 2018/1/23.
 */

public class ThemeColorSwipeRefreshLayout extends SwipeRefreshLayout implements ThemeColorMutable {
    public ThemeColorSwipeRefreshLayout(@NonNull Context context) {
        super(context);
        init();
    }

    public ThemeColorSwipeRefreshLayout(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        ThemeColorManager.add(this);
    }

    @Override
    public void setThemeColor(@NonNull ThemeColor themeColor) {
        setColorSchemeColors(themeColor.colorPrimary);
    }
}
