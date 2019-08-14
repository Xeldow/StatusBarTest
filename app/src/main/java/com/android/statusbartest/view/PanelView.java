package com.android.statusbartest.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.statusbartest.R;
import com.android.statusbartest.utils.TouchAnimator;
import com.android.statusbartest.utils.BlurUtil;
import com.android.statusbartest.utils.ScreenShotUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * 所有SystemUI的BaseView
 * <p/>
 * Created by XiaoZhenLin on 2019/7/25.
 */
public class PanelView extends FrameLayout {
    private static final String TAG = "StatusBarView";
    private static final int MIN_EXPEND_HIGH = 250;

    private Context mContext;

    private GestureDetector mGestureDetector;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private PanelView thisView;

    /**
     * 状态栏
     */
    private RelativeLayout statusBar;
    private float statusBarHeight;
    /**
     * 展开部分
     */
    private RelativeLayout statusBarExpanded;
    /**
     * 整个下拉栏
     */
    private LinearLayout panelView;
    private DisplayMetrics metrics;
    /**
     * 高斯模糊相关
     */
    private ImageView blurView;
    private Bitmap finalBitmap;
    private Rect blurArea;
    private Bitmap overlay;
    private Canvas canvas;
    /**
     * 状态判断
     */
    private boolean isStatusBarShown;
    private boolean enablePullDownPanelView = false;
    private boolean isTouching = false;
    private boolean isStatusBarExpandedShown = false;
    /**
     * Animator
     * 可以一举实现多个View多个动画效果
     */
    private TouchAnimator statusBarAnimator;
    private TouchAnimator.Listener sbAnimatorListener;


    public PanelView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context);

        // TODO：设置背景方法
//        setBackgroundColor(Color.parseColor("#858585"));
        mContext = context;
        this.windowManager = windowManager;
        this.params = params;
        thisView = PanelView.this;

        initView();
        initListener();

        // TODO：设置margin方法
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(statusBarExpanded.getLayoutParams());
//        layoutParams.setMargins(0, statusBarHeight, 0, 0);
    }

    /**
     * 这个方法不能在View初始化的时候调用
     * 因为这时候子View还没测量好,无法获取状态栏的高度
     */
    private void initAnimator() {
        TouchAnimator.Builder statusBarBuilder = new TouchAnimator.Builder();
        statusBarBuilder.addFloat(statusBar, "translationY", -statusBarHeight, 0);
        statusBarBuilder.addFloat(statusBar, "alpha", (float) 0.3, (float) 0.7);
        statusBarAnimator = statusBarBuilder
                .setListener(sbAnimatorListener)
                .build();
    }

    private void initListener() {
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
        mGestureDetector.setOnDoubleTapListener(new MyGestureListener());
        sbAnimatorListener = new TouchAnimator.Listener() {
            @Override
            public void onAnimationAtStart() {
                Log.e("TouchAnimator: ", "onAnimationAtStart: ");
            }

            @Override
            public void onAnimationAtEnd() {
                isStatusBarShown = true;
                Log.e("TouchAnimator: ", "onAnimationAtEnd: ");
            }

            @Override
            public void onAnimationStarted() {
                Log.e("TouchAnimator: ", "onAnimationStarted: ");
            }
        };
    }

    private void initView() {
        inflate(mContext, R.layout.layout_base_systemui, this);

        metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        statusBar = (RelativeLayout) findViewById(R.id.status_bar_parent);
        statusBarHeight = getResources().getDimensionPixelSize(R.dimen.dimen_status_bar_height);
        Log.e("mLog ", "height" + statusBarHeight);
        statusBarExpanded = (RelativeLayout) findViewById(R.id.status_bar_expanded);
        panelView = (LinearLayout) findViewById(R.id.panel_view);
        //帧布局中视图显示是按照栈的方式这样就可以把高斯模糊置底了
        blurView = (ImageView) findViewById(R.id.blur_background);
    }


    @SuppressLint("NewApi")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            /*
            将按下和拖动交给Gesture处理
             */
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                if (canvas != null && !isStatusBarExpandedShown) {
//                    changeBitmap(ScreenShotUtil.getBitmap());
                }
                //初始化Animator
                // TODO：使用getResource就可以把这个初始化提前了
                if (statusBarAnimator == null) {
                    statusBar.setY(0 - statusBarHeight);
                    initAnimator();
                    Log.e("mLog ", "h2" + statusBar.getHeight());
                }
            case MotionEvent.ACTION_MOVE:
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                isTouching = false;
                /*
                处理自动展开和收回
                 */
                if (panelView.getY() + panelView.getHeight() < MIN_EXPEND_HIGH  //getY是指View的顶部位置,小于最小展开高度的话就自动回收
                        && enablePullDownPanelView
                        && isStatusBarShown) {
                    statusBar.setVisibility(VISIBLE);
                    /*
                    整个布局先滚出顶部边缘
                    这是为了重新只显示状态栏
                     */
                    panelView.setTranslationY(0 - panelView.getHeight());
                    statusBarExpanded.setVisibility(GONE);
                    isStatusBarExpandedShown = false;
                    blurView.setVisibility(GONE);
                    panelView.setTranslationY(0);
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    windowManager.updateViewLayout(thisView, params);
                } else if (isStatusBarExpandedShown) {//自动下拉到底部
                    // TODO：获取屏幕高度
                    //setTranslationY设置的是这个View顶部在的位置,要注意
                    panelView.setTranslationY(metrics.heightPixels - panelView.getHeight());
                    blurArea.set(0, 0, metrics.widthPixels, metrics.heightPixels);
                    blurView.setClipBounds(blurArea);
                }
                //在放手后无任何操作就隐藏状态栏
                statusBar.postDelayed(statusBarHideRunnable, 1000);
        }
        return true;
    }

    Runnable statusBarHideRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isTouching && !isStatusBarExpandedShown) {
                statusBar.setVisibility(INVISIBLE);
                enablePullDownPanelView = false;
                isStatusBarShown = false;
            }
        }
    };

    /**
     * 手势处理
     */
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

        @SuppressLint("NewApi")
        @Override
        public boolean onDown(MotionEvent e) {
            if (isStatusBarShown) {
                //只要状态栏显示了,就可以下拉
                enablePullDownPanelView = true;
                //重置状态栏是否要隐藏的时间
                statusBar.removeCallbacks(statusBarHideRunnable);
                //防止一按下状态栏就闪烁出下拉栏
                if (!isStatusBarExpandedShown) {
                    statusBarExpanded.setVisibility(INVISIBLE);
                    isStatusBarExpandedShown = true;
                }
                //覆盖整个屏幕,不然没有位置提供下拉
                params.height = WindowManager.LayoutParams.MATCH_PARENT;
                windowManager.updateViewLayout(thisView, params);
            }
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
        @SuppressLint("NewApi")
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isStatusBarShown) {//第一次下拉的时候只可以下拉状态栏
                statusBar.setVisibility(VISIBLE);
                //计算移动的百分比提供给Animator
                float position = (e2.getY() - statusBarHeight) / statusBarHeight;
                statusBarAnimator.setPosition(position);
                //
                applyBlur();
            } else if (enablePullDownPanelView) {

                statusBarExpanded.setVisibility(VISIBLE);
                blurView.setVisibility(VISIBLE);
                //下拉通知栏
                panelView.setTranslationY(e2.getY() - panelView.getHeight());
                blurArea.set(0, 0, metrics.widthPixels, (int) e2.getY());
                blurView.setClipBounds(blurArea);
            }
            Log.e(TAG, "onScroll");
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.e(TAG, "onLongPress");
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.e(TAG, "onFling");
            return true;
        }

    }

    public PanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public PanelView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 高斯模糊
     */

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void applyBlur() {
        overlay = Bitmap.createBitmap(metrics.widthPixels,
                metrics.heightPixels, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(overlay);
        blurArea = new Rect(0, 0, metrics.widthPixels, 10);


        canvas.drawBitmap(ScreenShotUtil.getBitmap(), 0, 0, null);
        finalBitmap = BlurUtil.with(mContext)
                .bitmap(overlay) //要模糊的图片
                .radius(7)//模糊半径
                .blur();

        blurView.setBackground(new BitmapDrawable(getResources(), finalBitmap));
    }

    @SuppressLint("NewApi")
    private void changeBitmap(Bitmap bitmap) {


        canvas.drawBitmap(bitmap, 0, 0, null);

        finalBitmap = BlurUtil.with(mContext)
                .bitmap(overlay) //要模糊的图片
                .radius(7)//模糊半径
                .blur();
        blurView.setVisibility(INVISIBLE);
        blurView.setBackground(new BitmapDrawable(getResources(), finalBitmap));

    }


    /**
     * 以下是可以实现自动动画的简单设想
     */
    Handler handler = new Handler();

    float f = 0f;
    Runnable runnable3 = new Runnable() {
        @Override
        public void run() {
            statusBarAnimator.setPosition(f);
            if (f <= 1f) {
                f += 0.2f;
                handler.postDelayed(runnable3, 100);
            }
        }
    };


}
