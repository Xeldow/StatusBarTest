package com.android.statusbartest;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
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

/**
 * @description:
 * @author: Xeldow
 * @date: 2019/7/25
 */
public class StatusBarView extends FrameLayout {

    private Context mContext;
    private GestureDetector mGestureDetector;
    private static final String TAG = "MyView";
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private Animation translateAnimation;
    private StatusBarView thisView;

    private LinearLayout statusBar;
    private LinearLayout statusBarExpanded;


    public StatusBarView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context);
        inflate(context, R.layout.layout_base_systemui, this);
//        setBackgroundColor(Color.parseColor("#858585"));
//        StatusBarView.this.getBackground().setAlpha(0);
        mContext = context;
        this.windowManager = windowManager;
        this.params = params;
        thisView = StatusBarView.this;
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
        mGestureDetector.setOnDoubleTapListener(new MyGestureListener());

        statusBar = (LinearLayout) findViewById(R.id.status_bar_parent);
        statusBarExpanded = (LinearLayout) findViewById(R.id.status_bar_expanded);
    }


    public void setParams(WindowManager windowManager, WindowManager.LayoutParams params) {
        this.windowManager = windowManager;
        this.params = params;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:

                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                if (statusBarExpanded.getY() + statusBarExpanded.getHeight() < 250 && statusBarExpanded.getVisibility() == VISIBLE) {
                    statusBarExpanded.setVisibility(GONE);
                    statusBarExpanded.setTranslationY(0 - statusBarExpanded.getHeight());
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    windowManager.updateViewLayout(StatusBarView.this, params);
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
                    statusBarExpanded.setTranslationY(metrics.heightPixels - statusBarExpanded.getHeight());
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
//            handler.post(runnable);

            if (statusBar.getVisibility() == VISIBLE) {
                statusBarExpanded.setVisibility(VISIBLE);
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                windowManager.updateViewLayout(StatusBarView.this, params);
//                params.height = WindowManager.LayoutParams.MATCH_PARENT;
//                windowManager.updateViewLayout(StatusBarView.this, params);
            } else {
                statusBar.setVisibility(VISIBLE);
                statusBarExpanded.setVisibility(INVISIBLE);
//                statusBarExpanded.setY(0);
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                statusBarExpanded.setTranslationX(0 - statusBarExpanded.getHeight());
                windowManager.updateViewLayout(StatusBarView.this, params);
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
            //要对应下拉栏底部
            //拉到一定程度不能拉（不急）

            statusBarExpanded.setTranslationY(e2.getY() - statusBarExpanded.getHeight());
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
//            statusBar.setVisibility(GONE);
            windowManager.updateViewLayout(StatusBarView.this, params);

            Log.e(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "onFling");
//            Toast.makeText(mContext, "下拉了状态栏", Toast.LENGTH_LONG).show();
//            statusBarExpanded.setVisibility(VISIBLE);
//            params.height = WindowManager.LayoutParams.MATCH_PARENT;
//            windowManager.updateViewLayout(StatusBarView.this, params);
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
