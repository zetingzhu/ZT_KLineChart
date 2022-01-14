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

import com.zzt.klinechart.entity.DRectDao;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画矩形计算工具
 */
public class DrawCanvasRectUtil extends DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasRectUtil.class.getSimpleName();

    // 画图数据
    protected DRectDao drawDao;
    // 点画笔
    Paint mPointPaint;
    // 线画笔
    Paint mLinePaint;
    // 平移起点
    PointF translationStart;

    public DrawCanvasRectUtil(Context mContext, View view) {
        super(mContext, view);
        drawDao = new DRectDao();
        mPointPaint = initPointPaint(drawDao);
        mLinePaint = initLinePaint(drawDao);
    }


    /**
     * 初始化点的画笔
     */
    public Paint initPointPaint(DRectDao drawDao) {
        Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(drawDao.getPointColor());
        mPointPaint.setStrokeWidth(drawDao.getLineWidth());
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPointPaint;
    }

    /**
     * 初始线画笔
     */
    public Paint initLinePaint(DRectDao drawDao) {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(drawDao.getLineColor());
        mLinePaint.setStrokeWidth(drawDao.getLineWidth());
        return mLinePaint;
    }

    /**
     * 绘制点击点
     */
    public void drawLeftCircle(Canvas mCanvas, Paint mPaint, DRectDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 0) {
            PointF pointF = drawDao.getPoints().get(0);
            mCanvas.drawCircle(pointF.x, pointF.y, drawDao.getPointRadius(), mPaint);
        }
    }

    /**
     * 绘制右边点
     */
    public void drawRightCircle(Canvas mCanvas, Paint mPaint, DRectDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 3) {
            PointF pointF = drawDao.getPoints().get(2);
            mCanvas.drawCircle(pointF.x, pointF.y, drawDao.getPointRadius(), mPaint);
        }
    }

    /**
     * 绘制直线
     */
    public void drawLine(Canvas mCanvas, Paint mPaint, DRectDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 3) {
            SparseArray<PointF> points = drawDao.getPoints();
            Log.d(TAG, "打印绘制点：" + points);
            if (points != null && points.size() >= 4) {
                for (int i = 0; i < points.size(); i++) {
                    PointF s1 = points.get(i);
                    int nextIndex = (i + 1) % 4;
                    Log.d(TAG, "获取位置： i:" + i + " next:" + nextIndex);
                    PointF s2 = points.get(nextIndex);
                    mCanvas.drawLine(s1.x, s1.y, s2.x, s2.y, mPaint);
                }
            }
        }
    }

    /**
     * 两点计算直线
     */
    public void computeLine(float w, float h, DRectDao drawDao) {
        if (drawDao != null && drawDao.getPoints() != null && drawDao.getPoints().size() > 1) {
            PointF pointF0 = drawDao.getPoints().get(0);
            PointF pointF2 = drawDao.getPoints().get(2);
            // 第三个点
            PointF pointF1 = new PointF(pointF2.x, pointF0.y);
            saveIndexPointArea(1, pointF1);
            // 第四个点
            PointF pointF3 = new PointF(pointF0.x, pointF2.y);
            saveIndexPointArea(3, pointF3);
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
     * 得到第三个点是否在前两个点中间
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
     * 计算两点距离
     *
     * @return
     */
    public double getDistanceTwoPoints(PointF p1, PointF p2) {
        return Math.sqrt(Math.abs((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y)));
    }


    /**
     * 保存第一个点
     */
    public void saveFirstPoint(MotionEvent event, DRectDao drawDao) {
        // 第一个点
        PointF pointF0 = new PointF(event.getX(), event.getY());
        saveIndexPointArea(0, pointF0);
    }

    /**
     * 修改第一个点
     */
    public void updateFirstPoint(PointF updatePoint, DRectDao drawDao) {
        // 第一个点
        saveIndexPointArea(0, updatePoint);
    }


    /**
     * 保存第二个点
     */
    public void saveSecondPoint(float width, float height, MotionEvent event, DRectDao drawDao) {
        PointF pointF2 = new PointF(event.getX(), event.getY());
        saveIndexPointArea(2, pointF2);
        // 计算另外两个点
        computeLine(width, height, drawDao);
    }

    /**
     * 修改第二个点
     */
    public void updateSecondPoint(float width, float height, PointF updatePoint, DRectDao drawDao) {
        saveIndexPointArea(2, updatePoint);
        // 计算直线坐标
        computeLine(width, height, drawDao);
    }


    /**
     * 平移两个直线点
     */
    public void translationLinePoint(float w, float h, PointF eventS, PointF eventE, DRectDao drawDao) {
        if (drawDao.getPoints() != null && drawDao.getPoints().size() > 3) {
            // 屏幕矩阵
            RectF screen = new RectF(0, 0, w, h);

            float moveY = eventE.y - eventS.y;
            float moveX = eventE.x - eventS.x;

            PointF point0 = drawDao.getPoints().get(0);
            PointF point1 = drawDao.getPoints().get(2);
            Log.d(TAG, "老点：" + point0 + " - " + point1);

            // 锚点都在屏幕内才移动
            if (screen.contains(point0.x + moveX, point0.y + moveY) && screen.contains(point1.x + moveX, point1.y + moveY)) {

                point0.set(point0.x + moveX, point0.y + moveY);
                point1.set(point1.x + moveX, point1.y + moveY);
                Log.d(TAG, "新点：" + point0 + " - " + point1);

                updateFirstPoint(point0, drawDao);
                updateSecondPoint(w, h, point1, drawDao);
            }
        }
    }


    @Override
    public void onTouchActionMove(View view, MotionEvent event) {
        if (drawDao.getPoints() != null) {
            if (drawDao.getPoints().size() > 0) {
                if (drawDao.getDrawState() == DRectDao.DRAW_STATE_ROTATION) {
                    // 做旋转绘制
                    saveSecondPoint(markerView.getWidth(), markerView.getHeight(), event, drawDao);

                } else if (drawDao.getDrawState() == DRectDao.DRAW_STATE_TRANSLATION) {
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
        drawDao.setDrawState(DRectDao.DRAW_STATE_ROTATION);
        markerView.invalidate();
    }

    @Override
    public void onSingleTapConfirmed(MotionEvent e) {
        logEventW("onSingleTapConfirmed", e);
        if (drawDao.getPoints() != null) {
            if (drawDao.getPoints().size() == 0) {
                saveFirstPoint(e, drawDao);
            } else {
                saveSecondPoint(markerView.getWidth(), markerView.getHeight(), e, drawDao);
            }
            markerView.invalidate();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (drawDao != null) {
            SparseArray<RectF> clickPointArea = drawDao.getClickPointArea();
            if (clickPointArea != null && clickPointArea.size() > 3) {
                RectF rectF = clickPointArea.get(0);
                if (rectF.contains(e.getX(), e.getY())) {
                    // 长按在点区域在第一个点
                    updateFirstPoint(drawDao.getPoints().get(2), drawDao);
                    // 保存第二个点
                    saveSecondPoint(markerView.getWidth(), markerView.getHeight(), e, drawDao);
                    markerView.invalidate();
                } else {
                    // 判断点击点是否在线段附近
                    boolean thirdMiddlePoints = getThirdMiddlePoints(drawDao.getPoints(), new PointF(e.getX(), e.getY()));
                    if (thirdMiddlePoints) {
                        // 初始化平移起点
                        translationStart = new PointF(e.getX(), e.getY());
                        // 设置平移状态
                        drawDao.setDrawState(DRectDao.DRAW_STATE_TRANSLATION);
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
            drawLeftCircle(canvas, mPointPaint, drawDao);
            drawRightCircle(canvas, mPointPaint, drawDao);
        }
    }

    @Override
    public boolean isHasDrawUtil(MotionEvent e) {
        if (drawDao != null) {
            SparseArray<RectF> clickPointArea = drawDao.getClickPointArea();
            if (clickPointArea != null && clickPointArea.size() > 3) {
                RectF rectF0 = clickPointArea.get(0);
                RectF rectF2 = clickPointArea.get(2);
                if (rectF0.contains(e.getX(), e.getY()) || rectF2.contains(e.getX(), e.getY()) || getThirdMiddlePoints(drawDao.getPoints(), new PointF(e.getX(), e.getY()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
