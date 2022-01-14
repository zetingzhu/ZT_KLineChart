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

import com.zzt.klinechart.entity.DParallelLineDao;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画平行线 计算工具
 */
public class DrawCanvasParallelLineUtil extends DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasParallelLineUtil.class.getSimpleName();
    // 画图数据
    protected DParallelLineDao drawDao;
    // 画平行线需要点的数量
    private int pointNumber = 2;
    // 当前移动的是那个点
    private int tempIndex = 0;

    public DrawCanvasParallelLineUtil(Context mContext, View view) {
        super(mContext, view);
        drawDao = new DParallelLineDao();
        mPointPaint = initPointPaint(drawDao);
        mLinePaint = initLinePaint(drawDao);
    }


    /**
     * 初始化点的画笔
     */
    public Paint initPointPaint(DParallelLineDao drawDao) {
        Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(drawDao.getPointColor());
        mPointPaint.setStrokeWidth(drawDao.getLineWidth());
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPointPaint;
    }

    /**
     * 初始线画笔
     */
    public Paint initLinePaint(DParallelLineDao drawDao) {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(drawDao.getLineColor());
        mLinePaint.setStrokeWidth(drawDao.getLineWidth());
        return mLinePaint;
    }

    /**
     * 绘制点击点
     */
    public void drawPointCircle(Canvas mCanvas, Paint mPaint, DParallelLineDao drawDao) {
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
    public void drawLine(Canvas mCanvas, Paint mPaint, DParallelLineDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 1) {
            SparseArray<PointF> points = drawDao.getPoints();
            Log.d(TAG, "打印绘制点：" + points);

            PointF p0 = points.get(0);
            PointF p1 = points.get(1);
            mCanvas.drawLine(p0.x, p0.y, p1.x, p1.y, mPaint);

            SparseArray<PointF> parallelPoints = drawDao.getParallelPoints();
            if (isNotEmpty(parallelPoints) && parallelPoints.size() == 2) {
                PointF pl0 = parallelPoints.get(0);
                PointF pl1 = parallelPoints.get(1);

                mCanvas.drawLine(pl0.x, pl0.y, pl1.x, pl1.y, mPaint);
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
        RectF rectF = new RectF(pointF.x - drawDao.getPointRadius(), pointF.y - drawDao.getPointRadius(),
                pointF.x + drawDao.getPointRadius(), pointF.y + drawDao.getPointRadius());
        drawDao.getClickPointArea().put(index, rectF);
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
     * 计算平行线两个点
     */
    private void computeParallelPoint() {
        SparseArray<PointF> points = drawDao.getPoints();
        if (isNotEmpty(points) && points.size() == 3) {
            PointF p0 = points.get(0);
            PointF p1 = points.get(1);
            PointF p2 = points.get(2);
            // 中心点
            PointF c0 = new PointF((p0.x + p1.x) / 2, (p0.y + p1.y) / 2);

            PointF pl0 = new PointF(p0.x - c0.x + p2.x, p0.y - c0.y + p2.y);
            PointF pl1 = new PointF(p1.x - c0.x + p2.x, p1.y - c0.y + p2.y);

            drawDao.getParallelPoints().put(0, pl0);
            drawDao.getParallelPoints().put(1, pl1);
        }
    }

    /**
     * 保存点击的点
     */
    public void saveClickPoint(MotionEvent event, DParallelLineDao drawDao) {
        PointF pointF2 = new PointF(event.getX(), event.getY());
        int size = drawDao.getPoints().size();
        if (size < pointNumber) {
            saveIndexPointArea(size, pointF2);
            tempIndex = size;
        } else {
            saveIndexPointArea(pointNumber, pointF2);
            tempIndex = pointNumber;
            // 计算平行线
            computeParallelPoint();
        }
    }

    /**
     * 修改某个点
     */
    public void updateIndexPoint(int index, PointF updatePoint) {
        saveIndexPointArea(index, updatePoint);
        tempIndex = index;
        Log.d(TAG, "当前修改第几个点：" + index);
        computeParallelPoint();
    }


    @Override
    public void onTouchActionMove(View view, MotionEvent event) {
        if (drawDao.getPoints() != null) {
            if (drawDao.getPoints().size() > 0) {
                if (drawDao.getDrawState() == DParallelLineDao.DRAW_STATE_ROTATION) {
                    // 做旋转绘制
                    PointF pointF = new PointF(event.getX(), event.getY());
                    updateIndexPoint(tempIndex, pointF);
                }
                markerView.invalidate();
            }
        }
    }

    @Override
    public void onTouchActionCancel(View view, MotionEvent event) {
        mLinePaint.setStrokeWidth(5);
        drawDao.setDrawState(DParallelLineDao.DRAW_STATE_ROTATION);
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
            drawLine(canvas, mLinePaint, drawDao);
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
