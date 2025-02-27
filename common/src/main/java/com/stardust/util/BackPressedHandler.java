package com.stardust.util;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Stardust on 2017/2/3.
 */
public interface BackPressedHandler {

    boolean onBackPressed(Activity activity);

    interface HostActivity {

        @NonNull
        Observer getBackPressedObserver();

    }

    class Observer implements BackPressedHandler {

        private final CopyOnWriteArrayList<BackPressedHandler> mBackPressedHandlers = new CopyOnWriteArrayList<>();

        @Override
        public boolean onBackPressed(Activity activity) {
            for (BackPressedHandler handler : mBackPressedHandlers) {
                if (handler.onBackPressed(activity)) {
                    return true;
                }
            }
            return false;
        }

        public void registerHandler(BackPressedHandler handler) {
            mBackPressedHandlers.add(handler);
        }

        public void registerHandlerAtFront(BackPressedHandler handler) {
            mBackPressedHandlers.add(0, handler);
        }

        public void unregisterHandler(BackPressedHandler handler) {
            mBackPressedHandlers.remove(handler);
        }
    }


    class DoublePressExit implements BackPressedHandler {

        private final Activity mActivity;
        private final String mToast;
        private long mLastPressedMillis;
        private long mDoublePressInterval = 1000;

        public DoublePressExit(@NonNull Activity activity, int noticeResId) {
            this(activity, activity.getString(noticeResId));
        }

        public DoublePressExit(Activity activity, String toast) {
            mActivity = activity;
            mToast = toast;
        }

        @NonNull
        public DoublePressExit doublePressInterval(long doublePressInterval) {
            mDoublePressInterval = doublePressInterval;
            return this;
        }

        @Override
        public boolean onBackPressed(Activity activity) {
            if (System.currentTimeMillis() - mLastPressedMillis < mDoublePressInterval) {
                mActivity.finish();
            } else {
                mLastPressedMillis = System.currentTimeMillis();
                Toast.makeText(mActivity, mToast, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }


}
