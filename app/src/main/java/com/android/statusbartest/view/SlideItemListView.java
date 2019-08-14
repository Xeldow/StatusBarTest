package com.android.statusbartest.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Scroller;

//import com.xtc.log.LogUtil;
//import com.xtc.utils.ui.MotionEventUtils;

/**
 * Notification Item 滑动删除实现
 * <p/>
 * Created by LiYang on 2016/5/7.
 */
public class SlideItemListView extends ListView {

    private static final boolean DEBUG = true;

    /**
     * 当前滑动的ListView position
     */
    private int slidePosition;
    /**
     * 手指按下X的坐标
     */
    private int downY;
    /**
     * 手指按下Y的坐标
     */
    private int downX;
    /**
     * 屏幕宽度
     */
    private int screenWidth;
    /**
     * ListView的item
     */
    private View itemView;
    /**
     * 滑动类
     */
    private Scroller scroller;
    private static final int SNAP_VELOCITY = 600;
    /**
     * 速度追踪对象
     */
    private VelocityTracker velocityTracker;
    /**
     * 是否响应滑动，默认为不响应
     */
    private boolean isSlide = false;
    /**
     * 用户滑动的最小距离
     */
    private int touchSlop;
    /**
     *  移除item后的回调接口
     */
    private RemoveListener removeListener;

    private boolean isLeftScroll = false;

    public SlideItemListView(Context context) {
        this(context, null);
    }

    public SlideItemListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideItemListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        screenWidth = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getWidth();
        scroller = new Scroller(context);
        touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    /**
     * 设置滑动删除的回调接口
     */
    public void setRemoveListener(RemoveListener removeListener) {
        this.removeListener = removeListener;
    }

    @Override
    protected void handleDataChanged() {
        super.handleDataChanged();
//        if (DEBUG) LogUtil.d("SlideItemListView","handleDataChanged");
        resetScroll();
    }

    /**
     * 分发事件，主要做的是判断点击的是那个item, 以及通过postDelayed来设置响应左右滑动事件
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
//        if (DEBUG) LogUtil.d("SlideItemListView","dispatchTouchEvent action = " + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                addVelocityTracker(event);

                // 假如scroller滚动还没有结束，我们直接返回
                if (!scroller.isFinished()) {
                    return true;
                }
//                downX = (int) MotionEventUtils.getX(event);
//                downY = (int) MotionEventUtils.getY(event);

                slidePosition = pointToPosition(downX, downY);

                // 无效的position, 不做任何处理
                if (slidePosition == AdapterView.INVALID_POSITION) {
                    return super.dispatchTouchEvent(event);
                }
                break;
            }

            case MotionEvent.ACTION_MOVE:
                judgeIsSlide(event);
                break;

            case MotionEvent.ACTION_UP:
                recycleVelocityTracker();
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    /**
     * 判断是否开始滑动
     * 目前限制向左滑动
     */
    private void judgeIsSlide(MotionEvent event) {
//        if (DEBUG) LogUtil.d("SlideItemListView","judgeIsSlide slidePosition = " + slidePosition);
        // header, 不做任何处理
        if (slidePosition < getHeaderViewsCount()) {
            isSlide = false;
            return ;
        }
//        if (DEBUG) LogUtil.d("SlideItemListView","judgeIsSlide getFirstVisiblePosition = " + getFirstVisiblePosition());
        itemView = getChildAt(slidePosition - getFirstVisiblePosition());

//        if (Math.abs(getScrollVelocity()) > SNAP_VELOCITY
//                || (downX - MotionEventUtils.getX(event) > touchSlop && Math
//                .abs(MotionEventUtils.getY(event) - downY) < touchSlop)) {
//            isSlide = true;
//        }
    }

    /**
     * 处理我们拖动ListView item的逻辑
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & MotionEvent.ACTION_MASK;
//        if (DEBUG) LogUtil.d("SlideItemListView","onTouchEvent action = " + action);
        if (isSlide && slidePosition != AdapterView.INVALID_POSITION) {
            addVelocityTracker(ev);
//            int x = (int) MotionEventUtils.getX(ev);
            switch (action) {
                case MotionEvent.ACTION_MOVE:
//                    int deltaX = downX - x;
//                    downX = x;

                    // 手指拖动itemView滚动, deltaX大于0向左滚动，小于0向右滚
                    if (itemView != null) {
//                        itemView.scrollBy(deltaX, 0);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    int velocityX = getScrollVelocity();
                    if (velocityX > SNAP_VELOCITY) {
                        scrollRight();
                    } else if (velocityX < -SNAP_VELOCITY) {
                        scrollLeft();
                    } else {
                        scrollByDistanceX();
                    }

                    recycleVelocityTracker();
                    isSlide = false;
                    break;

                case MotionEvent.ACTION_CANCEL:
                    resetScroll();
                    recycleVelocityTracker();
                    isSlide = false;
                    break;

                default:
                    break;
            }
            super.onTouchEvent(ev);
            return true;
        }
        return super.onTouchEvent(ev);
    }

    private void resetScroll() {
        if (itemView != null) {
            isLeftScroll = false;
            itemView.scrollTo(0, 0);
        }
    }

    /**
     * 往右滑动，getScrollX()返回的是左边缘的距离，就是以View左边缘为原点到开始滑动的距离，所以向右边滑动为负值
     */
    private void scrollRight() {
        if (itemView != null) {
            isLeftScroll = false;
            final int delta = (screenWidth + itemView.getScrollX());
            scroller.startScroll(itemView.getScrollX(), 0, -delta, 0,
                    Math.abs(delta));
            postInvalidate();
        }
    }

    /**
     * 向左滑动，根据上面我们知道向左滑动为正值
     */
    private void scrollLeft() {
        if (itemView != null) {
            isLeftScroll = true;
            final int delta = (screenWidth - itemView.getScrollX());
            scroller.startScroll(itemView.getScrollX(), 0, delta, 0,
                    Math.abs(delta));
            postInvalidate();
        }
    }

    /**
     * 根据手指滚动itemView的距离来判断是滚动到开始位置还是向左或者向右滚动
     */
    private void scrollByDistanceX() {
        if (itemView != null) {
            // 如果向左滚动的距离大于屏幕的三分之一，就让其删除
            if (itemView.getScrollX() >= screenWidth / 3) {
                scrollLeft();
            } else if (itemView.getScrollX() <= -screenWidth / 3) {
                scrollRight();
            } else {
                resetScroll();
            }
        }
    }

    @Override
    public void computeScroll() {
        // 调用startScroll的时候scroller.computeScrollOffset()返回true，
        if (itemView == null){
            return;
        }

        if (scroller.computeScrollOffset()) {
            // 让ListView item根据当前的滚动偏移量进行滚动
            itemView.scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();

            // 滚动动画结束的时候调用回调接口
            if (scroller.isFinished()) {
                if (removeListener != null && isLeftScroll) {
                    itemView.setVisibility(GONE);
                    removeListener.removeItem(slidePosition);
                }
                resetScroll();
            }
        }
    }

    /**
     * 添加用户的速度跟踪器
     */
    private void addVelocityTracker(MotionEvent event) {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }

        velocityTracker.addMovement(event);
    }

    /**
     * 移除用户速度跟踪器
     */
    private void recycleVelocityTracker() {
        if (velocityTracker != null) {
            velocityTracker.recycle();
            velocityTracker = null;
        }
    }

    /**
     * 获取X方向的滑动速度,大于0向右滑动，反之向左
     */
    private int getScrollVelocity() {
        velocityTracker.computeCurrentVelocity(1000);
        return (int) velocityTracker.getXVelocity();
    }

    /**
     * 当ListView item滑出屏幕，回调这个接口
     * 我们需要在回调方法removeItem()中移除该Item,然后刷新ListView
     */
    public interface RemoveListener {
        void removeItem(int position);
    }
}
