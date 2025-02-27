package org.autojs.autojs.tool;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.stardust.app.OnActivityResultDelegate;

import org.autojs.autojs.R;

/**
 * Created by Stardust on 2017/3/5.
 */

public class ImageSelector implements OnActivityResultDelegate {

    private static final String TAG = ImageSelector.class.getSimpleName();
    private static final int REQUEST_CODE = "LOVE HONMUA".hashCode() >> 16;
    private final Activity mActivity;
    private final ImageSelectorCallback mCallback;
    @NonNull
    private final Mediator mMediator;
    private boolean mDisposable;
    public ImageSelector(Activity activity, @NonNull OnActivityResultDelegate.Mediator mediator, ImageSelectorCallback callback) {
        mediator.addDelegate(REQUEST_CODE, this);
        mActivity = activity;
        mCallback = callback;
        mMediator = mediator;
    }

    public void select() {
        mActivity.startActivityForResult(Intent.createChooser(
                        new Intent(Intent.ACTION_GET_CONTENT).setType("image/*"), mActivity.getString(R.string.text_select_image)),
                REQUEST_CODE);
    }

    @NonNull
    public ImageSelector disposable() {
        mDisposable = true;
        return this;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (mDisposable) {
            mMediator.removeDelegate(this);
        }
        if (data == null) {
            mCallback.onImageSelected(this, null);
            return;
        }
        mCallback.onImageSelected(this, data.getData());

    }

    public interface ImageSelectorCallback {
        void onImageSelected(ImageSelector selector, Uri uri);
    }


}
