package com.android.statusbartest;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManagerNative;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/7/25
 */
public class StatusBarView extends FrameLayout {
    private static final String TAG = "StatusBarView";

    private Context mContext;

    private GestureDetector mGestureDetector;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private StatusBarView thisView;

    /**
     * 状态栏
     */
    private LinearLayout statusBar;
    /**
     * 展开部分
     */
    private LinearLayout statusBarExpanded;
    /**
     * 整个下拉栏
     */
    private LinearLayout panelView;


    public StatusBarView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context);
        inflate(context, R.layout.layout_base_systemui, this);
        // TODO：设置背景方法
//        setBackgroundColor(Color.parseColor("#858585"));
        mContext = context;
        this.windowManager = windowManager;
        this.params = params;
        thisView = StatusBarView.this;
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
        mGestureDetector.setOnDoubleTapListener(new MyGestureListener());

        statusBar = (LinearLayout) findViewById(R.id.status_bar_parent);
        statusBarExpanded = (LinearLayout) findViewById(R.id.status_bar_expanded);
        panelView = (LinearLayout) findViewById(R.id.panel_view);
        // TODO：设置margin方法
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(statusBarExpanded.getLayoutParams());
//        layoutParams.setMargins(0, statusBar.getHeight(), 0, 0);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            /*
            将按下和拖动交给Gesture处理
             */
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                /*
                处理自动展开和收回
                这里将整个布局都收回去了,所以显示不出状态栏
                 */
                if (panelView.getY() + panelView.getHeight() < 250 && panelView.getVisibility() == VISIBLE) {
                    statusBarExpanded.setVisibility(GONE);
                    statusBar.setVisibility(VISIBLE);
                    panelView.setTranslationY(0 - statusBarExpanded.getHeight());
                    // TODO：状态栏下滑动画
                    panelView.setTranslationY(0);

                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    windowManager.updateViewLayout(thisView, params);
                    statusBar.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (statusBarExpanded.getVisibility() == GONE) {
                                statusBar.setVisibility(INVISIBLE);
                            }
                        }
                    }, 1500);
                } else if (statusBarExpanded.getVisibility() == VISIBLE) {
                    // TODO：获取屏幕高度
                    DisplayMetrics metrics = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(metrics);
                    panelView.setTranslationY(metrics.heightPixels - panelView.getHeight());

                }
        }
        return true;
    }


    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        public MyGestureListener() {
            super();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.e(TAG, "onDoubleTap");
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.e(TAG, "onDoubleTapEvent");
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.e(TAG, "onSingleTapConfirmed");
            return true;
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            Log.e(TAG, "onContextClick");
            return true;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (statusBar.getVisibility() != GONE) {//状态栏显示的时候再次点击就可以下拉
                statusBar.setVisibility(VISIBLE);
                //处理屏幕闪动
//                panelView.setY(e.getY() - panelView.getHeight());
//                panelView.setTranslationY(e.getY() - panelView.getHeight());

//                panelView.setVisibility(VISIBLE);
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                windowManager.updateViewLayout(thisView, params);
            } else {//第一次点击（下拉）就展示状态栏,添加动画
                statusBar.setVisibility(VISIBLE);
                statusBarExpanded.setVisibility(INVISIBLE);
                panelView.setTranslationY(0);
//                panelView.setVisibility(INVISIBLE);

            }
//            Toast.makeText(mContext, "点击了状态栏", Toast.LENGTH_LONG).show();
            Log.e(TAG, "onDown");
            return true;
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.e(TAG, "onShowPress");
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.e(TAG, "onSingleTapUp");
            return true;
        }

        /**
         * @param e1        之前的DOWN
         * @param e2        现在的MOVE
         * @param distanceX 当前MOVE和上一个MOVE的位移量
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.e(TAG, "onScroll");
//            statusBar.setTranslationY(e2.getY() - statusBarExpanded.getHeight() - statusBar.getHeight());
//            statusBarExpanded.setTranslationY(e2.getY() - statusBarExpanded.getHeight());
            statusBarExpanded.setVisibility(VISIBLE);
            //下拉通知栏
            panelView.setTranslationY(e2.getY() - panelView.getHeight());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.e(TAG, "onLongPress");
        }

        /**
         * 快速下拉 可以开启动画直达底部
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "onFling");
//            Toast.makeText(mContext, "下拉了状态栏", Toast.LENGTH_LONG).show();
            return true;
        }

    }

    public StatusBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatusBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    private int realWidth, realHeiht;

    Handler handler = new Handler();

    boolean flag = false;

    Runnable runnable = new
            Runnable() {
                @Override
                public void run() {
                    if (params.y < 10) {
                        params.y += 1;
                        windowManager.updateViewLayout(StatusBarView.this, params);
                        handler.postDelayed(runnable, 10);
                    }
                    if (params.y == 10) {
                        handler.postDelayed(runnable2, 2000);
                    }
                }
            };

    Runnable runnable2 = new Runnable() {
        @Override
        public void run() {
            if (params.y > 0) {
                params.y -= 1;
                windowManager.updateViewLayout(StatusBarView.this, params);
                handler.postDelayed(runnable2, 10);
            }
        }
    };


}
