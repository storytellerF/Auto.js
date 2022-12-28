package com.stardust.autojs.core.image;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.stardust.autojs.core.opencv.Mat;
import com.stardust.autojs.core.opencv.OpenCVHelper;
import com.stardust.pio.UncheckedIOException;

import org.opencv.android.Utils;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

/**
 * Created by Stardust on 2017/11/25.
 */
public class ImageWrapper {

    private final int mWidth;
    private final int mHeight;
    @Nullable
    private Mat mMat;
    @Nullable
    private Bitmap mBitmap;

    protected ImageWrapper(@NonNull Mat mat) {
        mMat = mat;
        mWidth = mat.cols();
        mHeight = mat.rows();
    }

    protected ImageWrapper(@NonNull Bitmap bitmap) {
        mBitmap = bitmap;
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
    }

    protected ImageWrapper(@NonNull Bitmap bitmap, Mat mat) {
        mBitmap = bitmap;
        mMat = mat;
        mWidth = bitmap.getWidth();
        mHeight = bitmap.getHeight();
    }

    public ImageWrapper(int width, int height) {
        this(Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888));
    }


    @Nullable
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static ImageWrapper ofImage(@Nullable Image image) {
        if (image == null) {
            return null;
        }
        return new ImageWrapper(toBitmap(image));
    }

    @Nullable
    public static ImageWrapper ofMat(@Nullable Mat mat) {
        if (mat == null) {
            return null;
        }
        return new ImageWrapper(mat);
    }


    @Nullable
    public static ImageWrapper ofBitmap(@Nullable Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        return new ImageWrapper(bitmap);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap toBitmap(@NonNull Image image) {
        Image.Plane plane = image.getPlanes()[0];
        ByteBuffer buffer = plane.getBuffer();
        buffer.position(0);
        int pixelStride = plane.getPixelStride();
        int rowPadding = plane.getRowStride() - pixelStride * image.getWidth();
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth() + rowPadding / pixelStride, image.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        if (rowPadding == 0) {
            return bitmap;
        }
        return Bitmap.createBitmap(bitmap, 0, 0, image.getWidth(), image.getHeight());
    }

    public int getWidth() {
        ensureNotRecycled();
        return mWidth;
    }

    public int getHeight() {
        ensureNotRecycled();
        return mHeight;
    }

    @Nullable
    public Mat getMat() {
        ensureNotRecycled();
        if (mMat == null && mBitmap != null) {
            mMat = new Mat();
            Utils.bitmapToMat(mBitmap, mMat);
        }
        return mMat;
    }

    public void saveTo(String path) {
        ensureNotRecycled();
        if (mBitmap != null) {
            try {
                mBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(path));
            } catch (FileNotFoundException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            Imgcodecs.imwrite(path, mMat);
        }
    }

    public int pixel(int x, int y) {
        ensureNotRecycled();
        if (mBitmap != null) {
            return mBitmap.getPixel(x, y);
        }
        double[] channels = mMat.get(x, y);
        return Color.argb((int) channels[3], (int) channels[0], (int) channels[1], (int) channels[2]);
    }

    @Nullable
    public Bitmap getBitmap() {
        ensureNotRecycled();
        if (mBitmap == null && mMat != null) {
            mBitmap = Bitmap.createBitmap(mMat.width(), mMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mMat, mBitmap);
        }
        return mBitmap;
    }

    public void recycle() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        if (mMat != null) {
            OpenCVHelper.release(mMat);
            mMat = null;
        }

    }

    public void ensureNotRecycled() {
        if (mBitmap == null && mMat == null)
            throw new IllegalStateException("image has been recycled");
    }

    @NonNull
    public ImageWrapper clone() {
        ensureNotRecycled();
        if (mBitmap == null) {
            return ImageWrapper.ofMat(mMat.clone());
        }
        if (mMat == null) {
            return ImageWrapper.ofBitmap(mBitmap.copy(mBitmap.getConfig(), true));
        }
        return new ImageWrapper(mBitmap.copy(mBitmap.getConfig(), true), mMat.clone());
    }
}
