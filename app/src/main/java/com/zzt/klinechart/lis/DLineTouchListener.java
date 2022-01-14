package com.zzt.klinechart.lis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.zzt.klinechart.draw.DrawCanvasBaseUtil;
import com.zzt.klinechart.entity.DLineDao;
import com.zzt.klinechart.draw.DrawCanvasLineUtil;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 监听画图事件
 */
public class DLineTouchListener<T extends DrawCanvasBaseUtil> extends GestureDetector.SimpleOnGestureListener implements View.OnTouchListener {
    private static final String TAG = DLineTouchListener.class.getSimpleName();

    // 保存所有的画图工具
    @NonNull
    private final List<T> mCallbacks;

    // 手势检测器
    protected GestureDetector mGestureDetector;
    // 触摸状态
    protected static final int NONE = 0;
    protected static final int DRAG = 1;
    // 保存当前触摸状态的整数字段
    protected int mTouchMode = NONE;
    // 当前画图工具
    private DrawCanvasBaseUtil tempDrawCanvasUtil;

    public DLineTouchListener(Context mContext) {
        mGestureDetector = new GestureDetector(mContext, this);
        this.mCallbacks = new ArrayList<>(3);
    }

    /**
     * 添加画图工具
     */
    public void addCanvasUtil(T canvasUtil) {
        tempDrawCanvasUtil = canvasUtil;
        mCallbacks.add(0, canvasUtil);
    }

    /**
     * 移除画图工具
     */
    public void removeCanvasUtil(DrawCanvasBaseUtil canvasUtil) {
        mCallbacks.remove(canvasUtil);
    }

    /**
     * 移除所有画图工具
     */
    public void removeAllCanvasUtil( ) {
        mCallbacks.clear();
    }

    /**
     * 切换其他的画图工具
     */
    public void resetCanvasUtil(T canvasUtil) {
        tempDrawCanvasUtil = canvasUtil;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (mTouchMode == NONE) {
            mGestureDetector.onTouchEvent(event);
        }
        // Handle touch events here...
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:

                break;
            case MotionEvent.ACTION_MOVE:
                mTouchMode = DRAG;
                logEventD("MotionEvent.ACTION_MOVE ", event);
                if (tempDrawCanvasUtil != null) {
                    tempDrawCanvasUtil.onTouchActionMove(view, event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                logEventD("ACTION_UP ACTION_CANCEL", event);
                mTouchMode = NONE;
                if (tempDrawCanvasUtil != null) {
                    tempDrawCanvasUtil.onTouchActionCancel(view, event);
                }
                break;
        }
        return true; // indicate event was handled
    }

    @Override
    public void onLongPress(MotionEvent e) {
        super.onLongPress(e);
        // 选判断是操作的那个工具
        getTempForLongPress(e);
        // 在操作当前临时工具
        if (tempDrawCanvasUtil != null) {
            tempDrawCanvasUtil.onLongPress(e);
        }
    }

    /**
     * 通过长按获取到当前操作工具
     */
    public void getTempForLongPress(MotionEvent e) {
        try {
            for (T mCallback : mCallbacks) {
                boolean hasDrawUtil = mCallback.isHasDrawUtil(e);
                if (hasDrawUtil) {
                    resetCanvasUtil(mCallback);
                }
            }
        } catch (ConcurrentModificationException ex) {
            throwCallbackListModifiedWhileInUse(ex);
        }
    }

    @Override
    public void onShowPress(MotionEvent e) {
        super.onShowPress(e);
        logEventW("onShowPress", e);
    }

    /**
     * 双击
     */
    @Override
    public boolean onDoubleTap(MotionEvent e) {
        logEventW("onDoubleTap", e);
        return super.onDoubleTap(e);
    }

    /**
     * 单击
     */
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        logEventW("onSingleTapConfirmed", e);
        if (tempDrawCanvasUtil != null) {
            tempDrawCanvasUtil.onSingleTapConfirmed(e);
        }
        return super.onSingleTapConfirmed(e);
    }

    /**
     * 绘制
     */
    public void onDraw(Canvas canvas) {
        try {
            for (DrawCanvasBaseUtil mCallback : mCallbacks) {
                mCallback.onDraw(canvas);
            }
        } catch (ConcurrentModificationException ex) {
            throwCallbackListModifiedWhileInUse(ex);
        }
    }

    /**
     * 获取当前画图工具
     */
    public DrawCanvasBaseUtil getCurrentCanvasUtil() {
        return tempDrawCanvasUtil;
    }

    /**
     * 获取所有画图列表
     */
    public List<T> getListCallbacks() {
        return mCallbacks;
    }

    /**
     * 获取所有画图数量
     */
    public int getListCount() {
        return mCallbacks.size();
    }

    private void throwCallbackListModifiedWhileInUse(ConcurrentModificationException parent) {
        throw new IllegalStateException("不支持在调度回调期间添加和删除回调", parent);
    }

    public void logEventD(String tag, MotionEvent... motionEvent) {
        Log.d(TAG, logEvent(tag, motionEvent));
    }

    public void logEventW(String tag, MotionEvent... motionEvent) {
        Log.w(TAG, logEvent(tag, motionEvent));
    }

    public String logEvent(String tag, MotionEvent... motionEvent) {
        StringBuilder strBuil = new StringBuilder();
        strBuil.append("[" + tag + "]");
        if (motionEvent != null) {
            for (MotionEvent event : motionEvent) {
                strBuil.append("   X:" + event.getX() + "  Y:" + event.getY() + " getAction:" + event.getAction());
            }
        }
        return strBuil.toString();
    }
}
