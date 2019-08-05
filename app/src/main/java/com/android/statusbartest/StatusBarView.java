package com.android.statusbartest;


import android.content.Context;
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
import android.widget.LinearLayout;

import com.android.statusbartest.animator.TouchAnimator;

/**
 * 所有SystemUI的BaseView
 * <p/>
 * Created by XiaoZhenLin on 2019/7/25.
 */
public class StatusBarView extends FrameLayout {
    private static final String TAG = "StatusBarView";
    private static final int MINEXPENDHIGH = 250;

    private Context mContext;

    private GestureDetector mGestureDetector;

    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private StatusBarView thisView;

    /**
     * 状态栏
     */
    private LinearLayout statusBar;
    private boolean isStatusBarShown;
    /**
     * 展开部分
     */
    private LinearLayout statusBarExpanded;
    /**
     * 整个下拉栏
     */
    private LinearLayout panelView;
    /**
     * flag
     */
    private boolean enablePullDownPanelView = false;
    private boolean isTouching = false;
    private boolean isStatusBarExpandedShown = false;
    /**
     * Animator
     * 可以一举实现多个View多个动画效果
     */
    private TouchAnimator statusBarAnimator;
    private TouchAnimator.Listener sbAnimatorListener;


    public StatusBarView(Context context, WindowManager windowManager, WindowManager.LayoutParams params) {
        super(context);

        // TODO：设置背景方法
//        setBackgroundColor(Color.parseColor("#858585"));
        mContext = context;
        this.windowManager = windowManager;
        this.params = params;
        thisView = StatusBarView.this;

        initView();
        initListener();

        // TODO：设置margin方法
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(statusBarExpanded.getLayoutParams());
//        layoutParams.setMargins(0, statusBar.getHeight(), 0, 0);
    }

    /**
     * 这个方法不能在View初始化的时候调用
     * 因为这时候子View还没测量好,无法获取状态栏的高度
     */
    private void initAnimator() {
        TouchAnimator.Builder statusBarBuilder = new TouchAnimator.Builder();
        statusBarBuilder.addFloat(statusBar, "translationY", -statusBar.getHeight(), 0);
//        statusBarBuilder.addFloat(statusBar, "alpha", 0.5, 1);
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

        statusBar = (LinearLayout) findViewById(R.id.status_bar_parent);
        statusBarExpanded = (LinearLayout) findViewById(R.id.status_bar_expanded);
        panelView = (LinearLayout) findViewById(R.id.panel_view);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            /*
            将按下和拖动交给Gesture处理
             */
            case MotionEvent.ACTION_DOWN:
                isTouching = true;
                //初始化Animator
                if (statusBarAnimator == null) {
                    statusBar.setY(0 - statusBar.getHeight());
                    initAnimator();
                }
            case MotionEvent.ACTION_MOVE:
                return mGestureDetector.onTouchEvent(event);
            case MotionEvent.ACTION_UP:
                isTouching = false;
                /*
                处理自动展开和收回
                这里将整个布局都收回去了,所以显示不出状态栏
                 */
                if (panelView.getY() + panelView.getHeight() < MINEXPENDHIGH  //getY是指View的顶部位置,小于最小展开高度的话就自动回收
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
                    panelView.setTranslationY(0);
                    params.height = WindowManager.LayoutParams.WRAP_CONTENT;
                    windowManager.updateViewLayout(thisView, params);
                    //在放手后无任何操作就隐藏状态栏
                    statusBar.postDelayed(statusBarHideRunnable, 2500);
                } else if (isStatusBarExpandedShown) {//自动下拉到底部
                    // TODO：获取屏幕高度
                    DisplayMetrics metrics = new DisplayMetrics();
                    windowManager.getDefaultDisplay().getMetrics(metrics);
                    //setTranslationY设置的是这个View顶部在的位置,要注意
                    panelView.setTranslationY(metrics.heightPixels - panelView.getHeight());
                }
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
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (!isStatusBarShown) {//第一次下拉的时候只可以下拉状态栏
                statusBar.setVisibility(VISIBLE);
                //计算移动的百分比提供给Animator
                float position = (e2.getY() - statusBar.getHeight()) / statusBar.getHeight();
                statusBarAnimator.setPosition(position);
            } else if (enablePullDownPanelView) {
                statusBarExpanded.setVisibility(VISIBLE);
                //下拉通知栏
                panelView.setTranslationY(e2.getY() - panelView.getHeight());
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
