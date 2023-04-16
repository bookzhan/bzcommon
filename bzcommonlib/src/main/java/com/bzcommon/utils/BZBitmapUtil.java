package com.bzcommon.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 图片处理的工具类，主要针对bitmap进行处理
 */
public class BZBitmapUtil {

    private static final String TAG = "BZBitmapUtil";


    /**
     * 对比度
     *
     * @author Medivh
     * @date 2014-7-17 上午9:40:33
     */
    public static Bitmap setContrast(Bitmap bitmap, int contrastValue) {

        Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        // int brightness = progress - 127;
        float contrast = (float) ((contrastValue + 64) / 128.0);
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0,// �ı�Աȶ�
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0});

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        Canvas canvas = new Canvas(bmp);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return bmp;
    }

    /**
     * 设置图片亮度
     *
     * @author Medivh
     * @date 2014-7-17 上午9:53:06
     */
    public static void setBrightness(Bitmap srcBitmap, int brightness) {
        ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
        Canvas canvas = new Canvas(srcBitmap);
        canvas.drawBitmap(srcBitmap, 0, 0, paint);
    }


    public static Bitmap scaleBitmapFromWidth(Bitmap srcBitmap, float newWidth) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        srcBitmap.compress(CompressFormat.JPEG, 100, baos);
        // 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
        if (baos.toByteArray().length / 1024 > 1024) {
            // 重置baos即清空baos
            baos.reset();
            // 这里压缩50%，把压缩后的数据存放到baos中
            srcBitmap.compress(CompressFormat.JPEG, 100, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;

        // 缩放比。由于是固定比例缩放，用宽进行计算
        int ration = 1;
        if (newOpts.outWidth > newWidth) {
            ration = Math.round(newOpts.outWidth / newWidth);
        } else {
            ration = Math.round(newWidth / newOpts.outWidth);
        }
        if (ration <= 0)
            ration = 1;

        // 设置缩放比例
        newOpts.inSampleSize = ration;

        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(CompressFormat.JPEG, 100, baos);
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > 100) {
            // 重置baos即清空baos
            baos.reset();
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(CompressFormat.JPEG, options, baos);
            // 每次都减少10
            options -= 10;
        }
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        return BitmapFactory.decodeStream(isBm, null, null);
    }

    /**
     * @param bitmap 要压缩图片
     * @return 缩放后的图片
     * @author YHC 通过压缩图片的尺寸来压缩图片大小
     */
    public static Bitmap compressBySize(Context context, Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;

        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        int widthRatio = display.getWidth() / imgWidth;
        BZLogUtil.d("imgWidth:" + imgWidth + " Constants.screenWidth:" + display.getWidth() + "-------widthRatio:" + widthRatio);

        if (widthRatio > 1) {
            opts.inSampleSize = widthRatio;
        }
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
        return bitmap;
    }

    public static Bitmap compressBySize(Bitmap bitmap, int targetWidth, int targetHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 100, baos);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
        // 得到图片的宽度、高度；
        int imgWidth = opts.outWidth;
        int imgHeight = opts.outHeight;
        // 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
        int widthRatio = (int) Math.ceil(imgWidth / (float) targetWidth);
        int heightRatio = (int) Math.ceil(imgHeight / (float) targetHeight);
        if (widthRatio > 1 && widthRatio > 1) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        // 设置好缩放比例后，加载图片进内存；
        opts.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, opts);
        return bitmap;
    }


    /**
     * 按比例缩放到高度为scaleValue
     *
     * @param scaleValue 长和宽放大缩小的比例
     * @author Medivh
     * @date 2014-7-17 上午9:41:53
     */
    public static Bitmap scaleBitmap(Bitmap srcBitmap, float scaleValue) {
        Matrix matrix = new Matrix();
        matrix.postScale(scaleValue, scaleValue); //
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    /**
     * 按比例缩放到高度为scaleValue
     *
     * @param targetWidth  期望的宽
     * @param targetHeight 期望的高
     */
    public static Bitmap scaleBitmap(Bitmap srcBitmap, float targetWidth, float targetHeight) {
        float scaleX = targetWidth / srcBitmap.getWidth();
        float scaleY = targetHeight / srcBitmap.getHeight();
        float targetScale = scaleY > scaleX ? scaleY : scaleX;

        Matrix matrix = new Matrix();
        matrix.postScale(targetScale, targetScale);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    /**
     * 缩放到指定的宽高
     *
     * @param targetWidth  期望的宽
     * @param targetHeight 期望的高
     */
    public static Bitmap scaleBitmap4Fix(Bitmap srcBitmap, float targetWidth, float targetHeight) {
        float scaleX = targetWidth / srcBitmap.getWidth();
        float scaleY = targetHeight / srcBitmap.getHeight();
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
    }

    /**
     * 旋转图片
     *
     * @param angle 旋转角度
     * @author Medivh
     * @date 2014-7-17 上午9:52:35
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /**
     * 从原始图片正中央裁剪一张新图
     *
     * @author Medivh
     * @date 2014-7-17 上午9:53:06
     */
    public static Bitmap centerCutBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        try {

            Matrix matrix = new Matrix();
            float scaleWidth = (float) newWidth / bitmap.getWidth();
            float scaleHeight = (float) newHeight / bitmap.getHeight();

            if ((float) bitmap.getWidth() / (float) bitmap.getHeight() > (float) newWidth / (float) newHeight) {
                matrix.setScale(scaleHeight, scaleHeight);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                int offset = (bitmap.getWidth() - newWidth) / 2;
                bitmap = Bitmap.createBitmap(bitmap, offset, 0, newWidth, newHeight);
            } else {
                matrix.setScale(scaleWidth, scaleWidth);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                int offset = (bitmap.getHeight() - newHeight) / 2;
                bitmap = Bitmap.createBitmap(bitmap, 0, offset, newWidth, newHeight);
            }

        } catch (Exception e) {

            BZLogUtil.e(TAG, e);
            return null;
        }

        return bitmap;
    }


    /**
     * 设置图片的透明度
     *
     * @author Medivh
     * @date 2014-7-17 上午9:58:05
     */
    public static Bitmap makeReflectionBitmap(Bitmap srcBitmap) {
        int bmpWidth = srcBitmap.getWidth();
        int bmpHeight = srcBitmap.getHeight();
        int[] pixels = new int[bmpWidth * bmpHeight * 4];
        srcBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);

        // get4NoToast reflection bitmap based on the reversed one
        srcBitmap.getPixels(pixels, 0, bmpWidth, 0, 0, bmpWidth, bmpHeight);
        Bitmap reflectionBitmap = Bitmap.createBitmap(bmpWidth, bmpHeight, Config.ARGB_8888);
        int alpha = 0x00000000;

        int i = 0x00000000;
        for (int y = bmpHeight - 1; y >= 0; y--) {
            for (int x = bmpWidth - 1; x >= 0; x--) {
                int index = y * bmpWidth + x;
                int r = (pixels[index] >> 16) & 0xff;
                int g = (pixels[index] >> 8) & 0xff;
                int b = pixels[index] & 0xff;

                pixels[index] = alpha | (r << 16) | (g << 8) | b;

                reflectionBitmap.setPixel(x, y, pixels[index]);
            }

            if (i < 5) {
                alpha = alpha + 0x03000000;
            } else if (i < 10) {
                alpha = alpha + 0x06000000;
            } else if (i < 20) {
                alpha = alpha + 0x09000000;
            } else if (i < 30) {
                alpha = alpha + 0x0c000000;
            }

            i++;
        }

        return reflectionBitmap;
    }

    /**
     * 合并两张bitmap为一张
     *
     * @param background
     * @param foreground
     * @return Bitmap
     */
    public static Bitmap combineBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }
        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();

        final float rate = (float) fgWidth / fgHeight;
        int h = (int) (Math.sqrt(bgWidth * bgWidth / (rate * rate + 1)));
        int w = (int) (h * rate);

        foreground = scaleBitmap(foreground, w, h);
        fgWidth = foreground.getWidth();
        fgHeight = foreground.getHeight();

        Bitmap newmap = Bitmap.createBitmap(bgWidth, bgHeight, Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawBitmap(foreground, (bgWidth - fgWidth) / 2, (bgHeight - fgHeight) / 2, null);
        return newmap;
    }

    public static Bitmap readBitmap(Context context, int resId) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Context context, int resId, Config config) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = config;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    public static Bitmap readBitmap(Context context, String path) {

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        try {
            InputStream is = new FileInputStream(new File(path));
            return BitmapFactory.decodeStream(is, null, opt);
        } catch (Exception e) {

            BZLogUtil.e(TAG, e);
            return null;
        }
    }

    public static Bitmap convertViewToBitmap(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    /**
     * 添加阴影
     */
    public static Bitmap addShadow(Bitmap bitmap) {
        try {
            if (bitmap == null)
                return null;

            BlurMaskFilter blurFilter = new BlurMaskFilter(4, BlurMaskFilter.Blur.OUTER);
            Paint shadowPaint = new Paint();
            shadowPaint.setMaskFilter(blurFilter);

            int[] offsetXY = new int[2];
            Bitmap shadowImage = bitmap.extractAlpha(shadowPaint, offsetXY);
            shadowImage = shadowImage.copy(Config.ARGB_8888, true);
            Canvas c = new Canvas(shadowImage);
            c.drawBitmap(bitmap, -offsetXY[0], -offsetXY[1], null);
            return shadowImage;
        } catch (Exception e) {

            BZLogUtil.e(TAG, e);
        }
        return bitmap; // if error return the original bitmap
    }

    public static Bitmap getARGB_8888Bitmap(Bitmap bitmap) {
        if (null == bitmap) return null;
        Bitmap newmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newmap);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return newmap;
    }


    /**
     * 保存图片到指定的目录中
     *
     * @param bitmap 默认是jpg格式, 不保存alpha值
     */
    public static boolean saveBitmapToSDcard(Bitmap bitmap, String path) {
        try {
            BZFileUtils.createNewFile(path);
            FileOutputStream fileout = new FileOutputStream(path);
            BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileout);
            bitmap.compress(CompressFormat.JPEG, 100, bufferOutStream);
            bufferOutStream.flush();
            bufferOutStream.close();
            return true;
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        return false;
    }

    /**
     * 保存图片到指定的目录中
     *
     * @param bitmap 默认是png格式, 保存alpha值
     */
    public static void saveBitmapToFile(Bitmap bitmap, String path) {
        try {
            BZFileUtils.createNewFile(path);
            FileOutputStream fileout = new FileOutputStream(path);
            BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileout);
            bitmap.compress(CompressFormat.PNG, 100, bufferOutStream);
            bufferOutStream.flush();
            bufferOutStream.close();
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
    }

    /**
     * 根据资源id来读取一张图片
     *
     * @param resources
     * @param resId
     * @param width     希望的宽
     * @param height    希望的高
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources resources, int resId, int width, int height) {
        // 给定的BitmapFactory设置解码的参数
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 从解码器中获得原始图片的宽高，而避免申请内存空间
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, resId, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(resources, resId, options);
    }

    /**
     * 计算缩放比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image load both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

    public static Bitmap readResourceBitmap(Context context, int resId) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * 防止加载本地图片OOM
     *
     * @param path 本地地址,可以是absolutePath,也可以是Uri Content path
     */
    public static Bitmap loadBitmap(Context context, String path) {
        if (null == context || null == path) {
            return null;
        }
        Bitmap bm = null;
        InputStream inputStream1 = null;
        InputStream inputStream2 = null;
        try {
            Uri uri = Uri.parse(path);
            if (path.startsWith("/")) {
                uri = Uri.fromFile(new File(path));
            }
            inputStream1 = context.getContentResolver().openInputStream(uri);
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream1, null, opt);
            int picWidth = opt.outWidth;
            int picHeight = opt.outHeight;

            opt.inSampleSize = 1;
            // 根据屏的大小和图片大小计算出缩放比例
            Point screenSize = BZScreenUtils.getScreenSize(context);
            if (picWidth > picHeight) {
                if (picWidth > screenSize.x)
                    opt.inSampleSize = picWidth / screenSize.x;
            } else {
                if (picHeight > screenSize.y)
                    opt.inSampleSize = picHeight / screenSize.y;
            }
            // 这次再真正地生成一个有像素的，经过缩放了的bitmap
            opt.inJustDecodeBounds = false;
            inputStream2 = context.getContentResolver().openInputStream(uri);
            bm = BitmapFactory.decodeStream(inputStream2, null, opt);
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        BZFileUtils.closeStream(inputStream1);
        BZFileUtils.closeStream(inputStream2);
        int pictureDegree = readPictureRotateDegree(context, path);
        if (null != bm && pictureDegree != 0) {
            bm = rotateBitmap(bm, pictureDegree);
        }
        return bm;
    }

    /**
     * 读取图片属性：旋转的角度
     * ExifInterface可以设置旋转角度,用于测试 详见writePictureRotateDegree
     *
     * @param path 图片路径 可以是absolutePath,也可以是Uri Content path
     * @return degree旋转的角度
     */
    public static int readPictureRotateDegree(Context context, String path) {
        int degree = 0;
        try {
            Uri uri = Uri.parse(path);
            if (path.startsWith("/")) {
                uri = Uri.fromFile(new File(path));
            }
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            ExifInterface exifInterface = new ExifInterface(inputStream);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
            inputStream.close();
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
        return degree;
    }

    /**
     * @param path 绝对路径,saveAttributes不支持Uri
     */
    public static void writePictureRotateDegree(String path, int rotate) {
        try {
            rotate = rotate % 360;
            ExifInterface exifInterface = new ExifInterface(path);
            switch (rotate) {
                case 90:
                    exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(ExifInterface.ORIENTATION_ROTATE_90));
                    break;
                case 180:
                    exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(ExifInterface.ORIENTATION_ROTATE_180));
                    break;
                case 270:
                    exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, Integer.toString(ExifInterface.ORIENTATION_ROTATE_270));
                    break;
            }
            exifInterface.saveAttributes();
        } catch (Throwable e) {
            BZLogUtil.e(TAG, e);
        }
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (null == drawable) return null;
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                        : Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static synchronized Bitmap getFitBitmap(Context context, String fileName, int targetSize) {
        Bitmap targetBitmap = null;
        try {
            InputStream inputStream;
            if (fileName.startsWith("/")) {
                inputStream = new FileInputStream(fileName);
            } else {
                inputStream = context.getAssets().open(fileName);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, options);

            int outWidth = options.outWidth;
            int outHeight = options.outHeight;
            options.inSampleSize = 1;

            //由于是视频, 那么最大图片超过了屏幕大小就没有什么意义了,所以缩放处理
            Point maxSizePoint = new Point(targetSize, targetSize);
            if (outWidth > outHeight) {
                if (outWidth > maxSizePoint.x && maxSizePoint.x > 0) {
                    options.inSampleSize = outWidth / maxSizePoint.x;
                }
            } else if (outHeight > maxSizePoint.y && maxSizePoint.y > 0) {
                options.inSampleSize = outHeight / maxSizePoint.y;
            }
            options.inJustDecodeBounds = false;

            inputStream.close();

            int rotation = 0;
            //重新开启
            if (fileName.startsWith("/")) {
                inputStream = new FileInputStream(fileName);
                ExifInterface exifInterface = new ExifInterface(fileName);
                rotation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                try {
                    switch (rotation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                    }
                } catch (Throwable e) {
                    BZLogUtil.e(e);
                }
                Log.d(TAG, "Image rotation=" + rotation);
            } else {
                inputStream = context.getAssets().open(fileName);
            }
//            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
            if (null == bitmap) {
                inputStream.close();
                return null;
            }
            targetBitmap = bitmap;
            Bitmap scaleBitmap = null;
            if (bitmap.getWidth() > targetSize) {
                long startTime = System.currentTimeMillis();
                scaleBitmap = BZBitmapUtil.scaleBitmap(bitmap, targetSize, targetSize);
                targetBitmap = scaleBitmap;
                BZLogUtil.d(TAG, "scaleBitmap耗时=" + (System.currentTimeMillis() - startTime) + " srcWidth=" + bitmap.getWidth() + " srcHeight=" + bitmap.getHeight() + " targetWidth=" + targetBitmap.getWidth() + " targetHeight=" + targetBitmap.getHeight());
            }
            Bitmap rotationBitmap = null;
            if (rotation > 10) {
                long startTime = System.currentTimeMillis();
                rotationBitmap = BZBitmapUtil.rotateBitmap(targetBitmap, rotation);
                BZLogUtil.d(TAG, "rotateBitmap耗时=" + (System.currentTimeMillis() - startTime) + " srcWidth=" + targetBitmap.getWidth() + " srcHeight=" + targetBitmap.getHeight() + " rotationWidth=" + rotationBitmap.getWidth() + " rotationHeight=" + rotationBitmap.getHeight());
                targetBitmap = rotationBitmap;
            }
            //转化成RGBA
            Bitmap tempBitmap = Bitmap.createBitmap(targetBitmap.getWidth(), targetBitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(tempBitmap);
            canvas.drawBitmap(targetBitmap, 0, 0, new Paint());

            targetBitmap = tempBitmap;
            inputStream.close();
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
            if (null != scaleBitmap && !scaleBitmap.isRecycled()) {
                scaleBitmap.recycle();
            }
            if (null != rotationBitmap && !rotationBitmap.isRecycled()) {
                rotationBitmap.recycle();
            }
        } catch (Throwable e) {
            Log.d(TAG, "getTexture fileName=" + fileName);
        }
        return targetBitmap;
    }


    private static boolean saveBitmap(Bitmap bitmap, String path) {
        try {
            BZFileUtils.ensureParentPathExist(path);
            FileOutputStream fileOut = new FileOutputStream(path);
            BufferedOutputStream bufferOutStream = new BufferedOutputStream(fileOut);
            bitmap.compress(CompressFormat.JPEG, 100, bufferOutStream);
            bufferOutStream.flush();
            bufferOutStream.close();
            return true;
        } catch (Exception e) {
            BZLogUtil.e(TAG, e);
        }
        return false;
    }

}
