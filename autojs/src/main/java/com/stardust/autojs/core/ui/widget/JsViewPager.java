package com.stardust.autojs.core.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.stardust.autojs.core.ui.inflater.ShouldCallOnFinishInflate;

public class JsViewPager extends ViewPager implements ShouldCallOnFinishInflate {

    private String[] mTitles;

    public JsViewPager(@NonNull Context context) {
        super(context);
    }


    public JsViewPager(@NonNull Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void onFinishDynamicInflate() {
        setAdapter();
    }

    public void setTitles(String[] titles) {
        mTitles = titles;
    }

    private void setAdapter() {
        setOffscreenPageLimit(getChildCount());
        setAdapter(new PagerAdapter() {

            @NonNull
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                return getChildAt(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles != null && position < mTitles.length ?
                        mTitles[position] :
                        super.getPageTitle(position);
            }

            @Override
            public int getCount() {
                return getChildCount();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {

            }
        });
    }
}
