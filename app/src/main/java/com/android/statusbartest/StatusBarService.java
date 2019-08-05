package com.android.statusbartest;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

public class StatusBarService extends Service {
    private static WindowManager windowManager;
    private static WindowManager.LayoutParams params;

    private StatusBarView view;

    @Override
    public void onDestroy() {
        windowManager.removeView(view);
        super.onDestroy();
    }

    public StatusBarService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();
        params.x = 0;
        params.y = 0;
        params.gravity = Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.height = 40;
        params.format = PixelFormat.RGBA_8888;
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        view = new StatusBarView(getApplicationContext(), windowManager, params);
        windowManager.addView(StatusBarService.this.view, params);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    class MyBinder extends Binder {
//        public void setView(View view) {
//            StatusBarService.this.view = (StatusBarView) view;
//            StatusBarService.this.view.setParams(windowManager, params);
//        }

        public void addView() {
        }
    }
}
