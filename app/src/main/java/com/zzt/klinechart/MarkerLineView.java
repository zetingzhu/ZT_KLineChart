package com.zzt.klinechart;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.zzt.klinechart.draw.DrawCanvasBaseUtil;
import com.zzt.klinechart.draw.DrawCanvasGoldenSectionLineUtil;
import com.zzt.klinechart.draw.DrawCanvasHorizontalLineUtil;
import com.zzt.klinechart.draw.DrawCanvasLineUtil;
import com.zzt.klinechart.draw.DrawCanvasParallelLineUtil;
import com.zzt.klinechart.draw.DrawCanvasPolylineUtil;
import com.zzt.klinechart.draw.DrawCanvasRectUtil;
import com.zzt.klinechart.lis.DLineTouchListener;

/**
 * @author: zeting
 * @date: 2022/1/10
 */
public class MarkerLineView extends View {
    private static final String TAG = MarkerLineView.class.getSimpleName();

    // 所有绘制线集合
    DLineTouchListener<DrawCanvasBaseUtil> compositeDLineCallback;

    public MarkerLineView(Context context) {
        super(context);
        initView(context);
    }

    public MarkerLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MarkerLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView(Context mContext) {
        compositeDLineCallback = new DLineTouchListener<>(getContext());
    }

    /**
     * 创建画线对象
     */
    public void openDrawLine() {
        DrawCanvasLineUtil drawCanvasUtil = new DrawCanvasLineUtil(getContext(), this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    /**
     * 画矩形
     */
    public void openDrawRect() {
        DrawCanvasRectUtil drawCanvasUtil = new DrawCanvasRectUtil(getContext(), this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    /**
     * 画折线
     */
    public void openPolylineRect(int number) {
        DrawCanvasPolylineUtil drawCanvasUtil = new DrawCanvasPolylineUtil(getContext(), number, this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    /**
     * 画平行线
     */
    public void openParallelLineRect() {
        DrawCanvasParallelLineUtil drawCanvasUtil = new DrawCanvasParallelLineUtil(getContext(), this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    public void countDrawLine() {
        int listCount = compositeDLineCallback.getListCount();
        Log.d(TAG, "绘制的有多少条线：" + listCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (compositeDLineCallback == null) {
            return false;
        }
        return compositeDLineCallback.onTouch(this, event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (compositeDLineCallback != null) {
            compositeDLineCallback.onDraw(canvas);
        }
    }

    public void logEvent(String tag, MotionEvent event) {
        Log.d(TAG, "[" + tag + "]  X:" + event.getX() + "  Y:" + event.getY());
    }


    /**
     * 黄金分割线
     */
    public void openGoldenSectionLineRect() {
        DrawCanvasGoldenSectionLineUtil drawCanvasUtil = new DrawCanvasGoldenSectionLineUtil(getContext(), this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    /**
     * 画水平线
     */
    public void openHorizontalLineRect() {
        DrawCanvasHorizontalLineUtil drawCanvasUtil = new DrawCanvasHorizontalLineUtil(getContext(), this);
        compositeDLineCallback.addCanvasUtil(drawCanvasUtil);
    }

    /**
     * 清空所有画图
     */
    public void clearAllDraw() {
        compositeDLineCallback.removeAllCanvasUtil();
        postInvalidate();
    }
}
