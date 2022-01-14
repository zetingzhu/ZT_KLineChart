package com.zzt.klinechart.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import com.zzt.klinechart.entity.DHorizontalLineDao;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画 水平线 计算工具
 */
public class DrawCanvasHorizontalLineUtil extends DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasHorizontalLineUtil.class.getSimpleName();
    // 画图数据
    protected DHorizontalLineDao drawDao;
    // 当前移动的是那个点
    private int tempIndex = 0;


    public DrawCanvasHorizontalLineUtil(Context mContext, View view) {
        super(mContext, view);
        drawDao = new DHorizontalLineDao();
        mPointPaint = initPointPaint(drawDao);
        mLinePaint = initLinePaint(drawDao);
    }

    /**
     * 初始化点的画笔
     */
    public Paint initPointPaint(DHorizontalLineDao drawDao) {
        Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(drawDao.getPointColor());
        mPointPaint.setStrokeWidth(drawDao.getLineWidth());
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPointPaint;
    }

    /**
     * 初始线画笔
     */
    public Paint initLinePaint(DHorizontalLineDao drawDao) {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(drawDao.getLineColor());
        mLinePaint.setStrokeWidth(drawDao.getLineWidth());
        return mLinePaint;
    }


    /**
     * 绘制点击点
     */
    public void drawPointCircle(Canvas mCanvas, Paint mPaint, DHorizontalLineDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 0) {
            for (int i = 0; i < drawDao.getPoints().size(); i++) {
                PointF pointF = drawDao.getPoints().get(i);
                mCanvas.drawCircle(pointF.x, pointF.y, drawDao.getPointRadius(), mPaint);
            }
        }
    }

    /**
     * 绘制直线
     */
    public void drawLine(Canvas mCanvas, DHorizontalLineDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null) {
            SparseArray<PointF> points = drawDao.getPoints();
            Log.d(TAG, "所有点信息：" + points + " size:" + points.size());
            for (int i = 0; i < points.size(); i++) {
                PointF p0 = points.get(i);
                if (i == tempIndex) {
                    // 选中变粗
                    mLinePaint.setStrokeWidth(10);
                } else {
                    mLinePaint.setStrokeWidth(5);
                }
                mCanvas.drawLine(0, p0.y, markerView.getWidth(), p0.y, mLinePaint);
            }
        }
    }

    /**
     * 将点和点的区域保存到对象位置
     *
     * @param index
     * @param pointF
     */
    public void saveIndexPointArea(int index, PointF pointF) {
        drawDao.getPoints().put(index, pointF);
        RectF rectF = new RectF(0, pointF.y - drawDao.getPointRadius(), markerView.getWidth(), pointF.y + drawDao.getPointRadius());
        drawDao.getClickPointArea().put(index, rectF);
    }

    /**
     * 得到是否点击在了线上
     */
    private boolean getThirdMiddlePoints(SparseArray<PointF> points, PointF pointF) {
        if (points != null && points.size() > 3) {
            for (int i = 0; i < points.size(); i++) {
                int nextIndex = (i + 1) % 4;
                PointF pointA = points.get(i);
                PointF pointB = points.get(nextIndex);
                double ac = getDistanceTwoPoints(pointA, pointF);
                double bc = getDistanceTwoPoints(pointB, pointF);
                double ab = getDistanceTwoPoints(pointA, pointB);
                // 计算结果
                double result = (ac + bc) - ab;
                Log.e(TAG, "计算得到的差值为多少：" + result);
                if (result <= 0.5D) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否在点的区域内
     */
    private boolean hasTouchPointArea(SparseArray<RectF> clickPointArea, MotionEvent event) {
        for (int i = 0; i < clickPointArea.size(); i++) {
            RectF rectF = clickPointArea.get(i);
            if (rectF.contains(event.getX(), event.getY())) {
                // 修改某个点
                PointF pointF = new PointF(event.getX(), event.getY());
                updateIndexPoint(i, pointF);
                markerView.invalidate();
                return true;
            }
        }
        return false;
    }

    /**
     * 计算两点距离
     *
     * @return
     */
    public double getDistanceTwoPoints(PointF p1, PointF p2) {
        return Math.sqrt(Math.abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }


    /**
     * 保存点击的点
     */
    public void saveClickPoint(MotionEvent event, DHorizontalLineDao drawDao) {
        PointF pointF2 = new PointF(event.getX(), event.getY());
        int size = drawDao.getPoints().size();
        saveIndexPointArea(size, pointF2);
        tempIndex = -1;
    }


    /**
     * 修改某个点
     */
    public void updateIndexPoint(int index, PointF updatePoint) {
        if (index != -1) {
            saveIndexPointArea(index, updatePoint);
            tempIndex = index;
        }
    }

    @Override
    public void onTouchActionMove(View view, MotionEvent event) {
        if (drawDao.getPoints() != null) {
            if (drawDao.getPoints().size() > 0 && tempIndex >= 0) {
                PointF pointF = new PointF(event.getX(), event.getY());
                updateIndexPoint(tempIndex, pointF);
                markerView.invalidate();
            }
        }
    }

    @Override
    public void onTouchActionCancel(View view, MotionEvent event) {
        tempIndex = -1;
        mLinePaint.setStrokeWidth(5);
        drawDao.setDrawState(DHorizontalLineDao.DRAW_STATE_ROTATION);
        markerView.invalidate();
    }

    @Override
    public void onSingleTapConfirmed(MotionEvent e) {
        logEventW("onSingleTapConfirmed", e);
        if (drawDao.getPoints() != null) {
            saveClickPoint(e, drawDao);
            markerView.invalidate();
        }
    }


    @Override
    public void onLongPress(MotionEvent e) {
        if (drawDao != null) {
            SparseArray<RectF> clickPointArea = drawDao.getClickPointArea();
            if (isNotEmpty(clickPointArea)) {
                hasTouchPointArea(clickPointArea, e);
            }
        }
    }

    public void onDraw(Canvas canvas) {
        if (drawDao.getPoints() != null) {
            drawLine(canvas, drawDao);
            drawPointCircle(canvas, mPointPaint, drawDao);
        }
    }

    @Override
    public boolean isHasDrawUtil(MotionEvent e) {
        if (drawDao != null) {
            SparseArray<RectF> clickPointArea = drawDao.getClickPointArea();
            if (isNotEmpty(clickPointArea)) {
                for (int i = 0; i < clickPointArea.size(); i++) {
                    RectF rectF = clickPointArea.get(i);
                    if (rectF.contains(e.getX(), e.getY())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
