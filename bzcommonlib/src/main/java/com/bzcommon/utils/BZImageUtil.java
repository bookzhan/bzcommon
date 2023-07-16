package com.bzcommon.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Created by bookzhan on 2023−05-11 22:15.
 * description:
 */
public class BZImageUtil {

    public static Drawable flipDrawableHorizontal(Context context, Drawable drawable) {
        return flipDrawable(context, drawable, true, false);
    }

    public static Drawable flipDrawableVertical(Context context, Drawable drawable) {
        return flipDrawable(context, drawable, false, true);
    }

    public static Drawable flipDrawable(Context context, Drawable drawable, boolean flipHorizontal, boolean flipVertical) {
        if (null == context || null == drawable
                || drawable.getIntrinsicHeight() <= 0 || drawable.getIntrinsicWidth() <= 0) {
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        canvas.scale(flipHorizontal ? -1 : 1, flipVertical ? -1 : 1, drawable.getIntrinsicWidth() / 2f, drawable.getIntrinsicHeight() / 2f);
        drawable.draw(canvas);
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static Bitmap flipBitmapHorizontal(Context context, Bitmap bitmap) {
        return flipBitmap(context, bitmap, true, false);
    }

    public static Bitmap flipBitmapVertical(Context context, Bitmap bitmap) {
        return flipBitmap(context, bitmap, false, true);
    }

    public static Bitmap flipBitmap(Context context, Bitmap bitmap, boolean flipHorizontal, boolean flipVertical) {
        if (null == context || null == bitmap || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null;
        }
        Bitmap bitmapCanvas = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCanvas);
        canvas.scale(flipHorizontal ? -1 : 1, flipVertical ? -1 : 1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        return bitmapCanvas;
    }

    public static Bitmap processBitmap(Context context, Bitmap bitmap, int rotate, boolean flipHorizontal, boolean flipVertical) {
        if (null == context || null == bitmap || bitmap.getWidth() <= 0 || bitmap.getHeight() <= 0) {
            return null;
        }
        Bitmap bitmapCanvas = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCanvas);
        canvas.scale(flipHorizontal ? -1 : 1, flipVertical ? -1 : 1, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        if (rotate > 0) {
            canvas.rotate(rotate, bitmap.getWidth() / 2f, bitmap.getHeight() / 2f);
        }
        canvas.drawBitmap(bitmap, 0, 0, new Paint());
        return bitmapCanvas;
    }
}
