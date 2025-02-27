package org.autojs.autojs.ui.floating.gesture;

import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;

import androidx.annotation.NonNull;

import com.stardust.enhancedfloaty.WindowBridge;

/**
 * Created by Stardust on 2017/9/26.
 */

public class BounceDragGesture extends DragGesture {

    private static final int MIN_DY_TO_SCREEN_BOTTOM = 100;
    private static final int MIN_DY_TO_SCREEN_TOP = 0;
    @NonNull
    private final BounceInterpolator mBounceInterpolator;
    private long mBounceDuration = 300;

    public BounceDragGesture(WindowBridge windowBridge, View view) {
        super(windowBridge, view);
        setAutoKeepToEdge(true);
        mBounceInterpolator = new BounceInterpolator();
    }

    public void setBounceDuration(long bounceDuration) {
        mBounceDuration = bounceDuration;
    }

    @Override
    public boolean onDown(@NonNull MotionEvent event) {
        return super.onDown(event);
    }

    @Override
    public void keepToEdge() {
        int y = Math.min(mWindowBridge.getScreenHeight() - mView.getHeight() - MIN_DY_TO_SCREEN_BOTTOM, Math.max(MIN_DY_TO_SCREEN_TOP, mWindowBridge.getY()));
        int x = mWindowBridge.getX();
        int hiddenWidth = (int) (getKeepToSideHiddenWidthRadio() * (float) mView.getWidth());
        if (x > mWindowBridge.getScreenWidth() / 2) {
            bounce(x, mWindowBridge.getScreenWidth() - mView.getWidth() + hiddenWidth, y);
        } else {
            bounce(x, -hiddenWidth, y);
        }
    }

    protected void bounce(final int fromX, final int toX, final int y) {
        ValueAnimator animator = ValueAnimator.ofFloat(fromX, toX);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                mWindowBridge.updatePosition((int) ((float) animation.getAnimatedValue()), y);
            }
        });
        animator.setDuration(mBounceDuration);
        animator.setInterpolator(mBounceInterpolator);
        animator.start();
    }
}
