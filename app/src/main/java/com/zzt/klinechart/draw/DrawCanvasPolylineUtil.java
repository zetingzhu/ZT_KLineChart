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

import com.zzt.klinechart.entity.DPolylineDao;

/**
 * @author: zeting
 * @date: 2022/1/13
 * 画折线计算工具
 */
public class DrawCanvasPolylineUtil extends DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasPolylineUtil.class.getSimpleName();
    // 画图数据
    protected DPolylineDao drawDao;
    // 平移起点
    PointF translationStart;

    // 折线数量
    private int polylineNumber;

    // 当前移动的是那个点
    private int tempIndex = 0;

    public DrawCanvasPolylineUtil(Context mContext, int number, View view) {
        super(mContext, view);
        drawDao = new DPolylineDao();
        mPointPaint = initPointPaint(drawDao);
        mLinePaint = initLinePaint(drawDao);
        this.polylineNumber = number;
    }


    /**
     * 初始化点的画笔
     */
    public Paint initPointPaint(DPolylineDao drawDao) {
        Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(drawDao.getPointColor());
        mPointPaint.setStrokeWidth(drawDao.getLineWidth());
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPointPaint;
    }

    /**
     * 初始线画笔
     */
    public Paint initLinePaint(DPolylineDao drawDao) {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(drawDao.getLineColor());
        mLinePaint.setStrokeWidth(drawDao.getLineWidth());
        return mLinePaint;
    }

    /**
     * 绘制点击点
     */
    public void drawPointCircle(Canvas mCanvas, Paint mPaint, DPolylineDao drawDao) {
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
    public void drawLine(Canvas mCanvas, Paint mPaint, DPolylineDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 1) {
            SparseArray<PointF> points = drawDao.getPoints();
            Log.d(TAG, "打印绘制点：" + points);
            if (points != null && points.size() > 1) {
                for (int i = 0; i < points.size() - 1; i++) {
                    PointF s1 = points.get(i);
                    int nextIndex = i + 1;
                    PointF s2 = points.get(nextIndex);
                    mCanvas.drawLine(s1.x, s1.y, s2.x, s2.y, mPaint);
                }
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
     * 保存第二个点
     */
    public void saveSecondPoint(MotionEvent event, DPolylineDao drawDao) {
        PointF pointF2 = new PointF(event.getX(), event.getY());
        int size = drawDao.getPoints().size();
        if (size < polylineNumber) {
            saveIndexPointArea(size, pointF2);
            tempIndex = size;
        } else {
            saveIndexPointArea(polylineNumber, pointF2);
            tempIndex = polylineNumber;
        }
    }

    /**
     * 修改某个点
     */
    public void updateIndexPoint(int index, PointF updatePoint) {
        saveIndexPointArea(index, updatePoint);
        tempIndex = index;
        Log.d(TAG, "当前修改第几个点：" + index);
    }


    /**
     * 平移两个直线点
     */
    public void translationLinePoint(float w, float h, PointF eventS, PointF eventE, DPolylineDao drawDao) {
        if (drawDao.getPoints() != null && drawDao.getPoints().size() > 3) {
            // 屏幕矩阵
            RectF screen = new RectF(0, 0, w, h);

            float moveY = eventE.y - eventS.y;
            float moveX = eventE.x - eventS.x;

            for (int i = 0; i < drawDao.getPoints().size(); i++) {
                PointF points = drawDao.getPoints().get(i);
                if (screen.contains(points.x + moveX, points.y + moveY)) {
                    saveIndexPointArea(i, new PointF(points.x + moveX, points.y + moveY));
                } else {
                    break;
                }
            }
        }
    }


    @Override
    public void onTouchActionMove(View view, MotionEvent event) {
        if (drawDao.getPoints() != null) {
            if (drawDao.getPoints().size() > 0) {
                if (drawDao.getDrawState() == DPolylineDao.DRAW_STATE_ROTATION) {
                    // 做旋转绘制
                    PointF pointF = new PointF(event.getX(), event.getY());
                    updateIndexPoint(tempIndex, pointF);
                } else if (drawDao.getDrawState() == DPolylineDao.DRAW_STATE_TRANSLATION) {
                    // 做平移绘制
                    if (translationStart != null) {
                        translationLinePoint(markerView.getWidth(), markerView.getHeight(), translationStart, new PointF(event.getX(), event.getY()), drawDao);
                        translationStart.set(event.getX(), event.getY());
                    }
                }
                markerView.invalidate();
            }
        }
    }

    @Override
    public void onTouchActionCancel(View view, MotionEvent event) {
        mLinePaint.setStrokeWidth(5);
        drawDao.setDrawState(DPolylineDao.DRAW_STATE_ROTATION);
        markerView.invalidate();
    }

    @Override
    public void onSingleTapConfirmed(MotionEvent e) {
        logEventW("onSingleTapConfirmed", e);
        if (drawDao.getPoints() != null) {
            saveSecondPoint(e, drawDao);
            markerView.invalidate();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (drawDao != null) {
            SparseArray<RectF> clickPointArea = drawDao.getClickPointArea();
            if (clickPointArea != null && clickPointArea.size() > polylineNumber) {
                boolean hasTouchBoo = hasTouchPointArea(clickPointArea, e);
                if (!hasTouchBoo) {
                    // 判断点击点是否在线段附近
                    boolean thirdMiddlePoints = getThirdMiddlePoints(drawDao.getPoints(), new PointF(e.getX(), e.getY()));
                    if (thirdMiddlePoints) {
                        // 初始化平移起点
                        translationStart = new PointF(e.getX(), e.getY());
                        // 设置平移状态
                        drawDao.setDrawState(DPolylineDao.DRAW_STATE_TRANSLATION);
                        // 选中变粗
                        mLinePaint.setStrokeWidth(10);
                        markerView.invalidate();
                    }
                }
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
            if (clickPointArea != null && clickPointArea.size() > 3) {
                RectF rectF0 = clickPointArea.get(0);
                RectF rectF2 = clickPointArea.get(0);
                if (rectF0.contains(e.getX(), e.getY()) || rectF2.contains(e.getX(), e.getY()) || getThirdMiddlePoints(drawDao.getPoints(), new PointF(e.getX(), e.getY()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
