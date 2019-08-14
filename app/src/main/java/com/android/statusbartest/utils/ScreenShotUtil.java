package com.android.statusbartest.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.nio.ByteBuffer;

import static android.content.Context.MEDIA_PROJECTION_SERVICE;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/8/6
 */
public class ScreenShotUtil {
    public static Bitmap bitmap;
    public static View view;

    //使用反射调用截屏
    public static Bitmap getBitmap() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        float[] dims = {mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels};
        try {
            Class<?> demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getDeclaredMethod("screenshot", int.class, int.class);
            bitmap = (Bitmap) method.invoke(null, (int) dims[0], (int) dims[1]);
            view = (View) method.invoke(null, (int) dims[0], (int) dims[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //使用反射调用截屏
    public static View getView() {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        float[] dims = {mDisplayMetrics.widthPixels,
                mDisplayMetrics.heightPixels};
        try {
            Class<?> demo = Class.forName("android.view.SurfaceControl");
            Method method = demo.getDeclaredMethod("screenshot", int.class, int.class);
            view = (View) method.invoke(null, (int) dims[0], (int) dims[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

}
