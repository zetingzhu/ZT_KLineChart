package com.zzt.klinechart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.zzt.klinechart.entity.DParallelLineDao;

import java.util.Collection;

/**
 * @author: zeting
 * @date: 2022/1/13
 */
public abstract class DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasBaseUtil.class.getSimpleName();
    // 上下文
    protected Context mContext;
    // 自定义画图
    protected View markerView;
    // 点画笔
    protected Paint mPointPaint;
    // 线画笔
    protected Paint mLinePaint;

    public DrawCanvasBaseUtil(Context mContext, View markerView) {
        this.mContext = mContext;
        this.markerView = markerView;
    }


    public abstract void onTouchActionMove(View view, MotionEvent event);

    public abstract void onTouchActionCancel(View view, MotionEvent event);

    public abstract void onSingleTapConfirmed(MotionEvent e);

    public abstract void onLongPress(MotionEvent e);

    public abstract void onDraw(Canvas canvas);

    /**
     * 是否操作当前画图工具
     */
    public abstract boolean isHasDrawUtil(MotionEvent e);

    /**
     * 判断list是否为空
     */
    public static boolean isEmpty(SparseArray<?> list) {
        return list == null || list.size() == 0;
    }

    /**
     * 判断list不为空
     */
    public static boolean isNotEmpty(SparseArray<?> coll) {
        return !isEmpty(coll);
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
