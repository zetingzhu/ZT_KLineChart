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

import com.zzt.klinechart.entity.DLineDao;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画线计算工具
 */
public class DrawCanvasLineUtil extends DrawCanvasBaseUtil {
    private static final String TAG = DrawCanvasLineUtil.class.getSimpleName();

    // 画图数据
    protected DLineDao dLineDao;
    // 点画笔
    Paint mPointPaint;
    // 线画笔
    Paint mLinePaint;
    // 平移起点
    PointF translationStart;

    public DrawCanvasLineUtil(Context mContext, View view) {
        super(mContext, view);
        dLineDao = new DLineDao();
        mPointPaint = initPointPaint(dLineDao);
        mLinePaint = initLinePaint(dLineDao);
    }


    /**
     * 初始化点的画笔
     */
    public Paint initPointPaint(DLineDao dLineDao) {
        Paint mPointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointPaint.setColor(dLineDao.getPointColor());
        mPointPaint.setStrokeWidth(dLineDao.getLineWidth());
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        return mPointPaint;
    }

    /**
     * 初始线画笔
     */
    public Paint initLinePaint(DLineDao dLineDao) {
        Paint mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(dLineDao.getLineColor());
        mLinePaint.setStrokeWidth(dLineDao.getLineWidth());
        return mLinePaint;
    }

    /**
     * 绘制点击点
     *
     * @param mCanvas
     * @param mPaint
     * @param dLineDao
     */
    public void drawLeftCircle(Canvas mCanvas, Paint mPaint, DLineDao dLineDao) {
        if (dLineDao != null && dLineDao.getPoints() != null && dLineDao.getPoints().size() > 0) {
            PointF pointF = dLineDao.getPoints().get(0);
            mCanvas.drawCircle(pointF.x, pointF.y, dLineDao.getPointRadius(), mPaint);
        }
    }


    /**
     * 绘制右边点
     *
     * @param mCanvas
     * @param mPaint
     * @param dLineDao
     */
    public void drawRightCircle(Canvas mCanvas, Paint mPaint, DLineDao dLineDao) {
        if (dLineDao != null && dLineDao.getPoints() != null && dLineDao.getPoints().size() > 1) {
            PointF pointF = dLineDao.getPoints().get(1);
            mCanvas.drawCircle(pointF.x, pointF.y, dLineDao.getPointRadius(), mPaint);
        }
    }

    /**
     * 绘制直线
     */
    public void drawLine(Canvas mCanvas, Paint mPaint, DLineDao dLineDao) {
        if (dLineDao != null && dLineDao.getLinePoints() != null && dLineDao.getLinePoints().size() == 2) {
            PointF sl = dLineDao.getLinePoints().get(0);
            PointF sr = dLineDao.getLinePoints().get(1);
            mCanvas.drawLine(sl.x, sl.y, sr.x, sr.y, mPaint);
        }
    }


    /**
     * 两点计算直线
     */
    public void computeLine(float w, float h, DLineDao dLineDao) {
        if (dLineDao != null && dLineDao.getPoints() != null && dLineDao.getPoints().size() > 1) {
            PointF pointFF = dLineDao.getPoints().get(0);
            PointF pointFS = dLineDao.getPoints().get(1);

//            mCanvas.drawLine(pointFF.x, pointFF.y, pointFS.x, pointFS.y, mPaint);
//            Log.i(TAG, "直线 - 点击两个点 1：" + pointFF.toString() + "  2：" + pointFS.toString());
            SparseArray<PointF> screenEdgePointF = getScreenEdgePointF(w, h, pointFF, pointFS);
            dLineDao.setLinePoints(screenEdgePointF);
        }
    }


    /**
     * 两点式求屏幕边缘坐标
     */
    public SparseArray<PointF> getScreenEdgePointF(float w, float h, PointF pointA, PointF pointB) {
        SparseArray<PointF> sparseArray = new SparseArray<>();
        // 屏幕矩阵
        RectF screen = new RectF(0, 0, w, h);

        PointF pointFL = new PointF();
        PointF pointFR = new PointF();
        if (pointA.x - pointB.x == 0) {
            pointFL.set(pointA.x, 0);
            pointFR.set(pointA.x, h);
        } else if (pointA.y - pointB.y == 0) {
            pointFL.set(0, pointA.y);
            pointFR.set(w, pointA.y);
        } else {


            // k=(y2-y1)/(x2-x1)
            // 直线斜率K
            float k = (pointB.y - pointA.y) / (pointB.x - pointA.x);
            // X轴为0时候Y值
            float y0 = k * (0 - pointA.x) + pointA.y;
            boolean containsX = screen.contains(0, y0);
            if (containsX) {
                pointFL.set(0, y0);
                Log.w(TAG, "直线 - 屏幕内 X轴为0时候Y值:" + pointFL);
            } else {
                //  Y轴为屏幕搞时候X值
                float xW = (h - pointA.y) / k + pointA.x;
                pointFL.set(xW, h);
                Log.w(TAG, "直线 - 屏幕边缘  Y轴为屏幕搞时候X值:" + pointFL);
            }

            // Y轴为0时候X值
            float x0 = (0 - pointA.y) / k + pointA.x;
            boolean containsY = screen.contains(x0, 0);
            if (containsY) {
                pointFR.set(x0, 0);
                Log.w(TAG, "直线 - 屏幕内  Y轴为0时候X值:" + pointFR);
            } else {
                // X轴为屏幕宽时候Y值
                float yw = k * (w - pointA.x) + pointA.y;
                pointFR.set(w, yw);
                Log.w(TAG, "直线 - 屏幕边缘  X轴为屏幕宽时候Y值:" + pointFR);
            }

            Log.w(TAG, "直线 - 最后计算出屏幕两边点是多少 1：" + pointFL.toString() + "  2：" + pointFR.toString());
        }

        sparseArray.put(0, pointFL);
        sparseArray.put(1, pointFR);
        return sparseArray;
    }


    /**
     * 得到第三个点是否在前两个点中间
     */
    public boolean getThirdMiddlePoints(PointF pointA, PointF pointB, PointF pointC) {
        double ac = getDistanceTwoPoints(pointA, pointC);
        double bc = getDistanceTwoPoints(pointB, pointC);
        double ab = getDistanceTwoPoints(pointA, pointB);
        // 计算结果
        double result = (ac + bc) - ab;
        Log.e(TAG, "计算得到的差值为多少：" + result);
        return result <= 0.5D;
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
    public void saveFirstPoint(MotionEvent event, DLineDao dLineDao) {
        // 第一个点
        dLineDao.getPoints().put(0, new PointF(event.getX(), event.getY()));
        // 保存第一个点集合
        RectF rectF0 = new RectF(event.getX() - dLineDao.getPointRadius(), event.getY() - dLineDao.getPointRadius(), event.getX() + dLineDao.getPointRadius(), event.getY() + dLineDao.getPointRadius());
        dLineDao.getClickPointArea().put(0, rectF0);
    }

    /**
     * 修改第一个点
     */
    public void updateFirstPoint(PointF updatePoint, DLineDao dLineDao) {
        // 第一个点
        dLineDao.getPoints().put(0, updatePoint);
        // 保存第一个点集合
        RectF rectF0 = new RectF(updatePoint.x - dLineDao.getPointRadius(), updatePoint.y - dLineDao.getPointRadius(),
                updatePoint.x + dLineDao.getPointRadius(), updatePoint.y + dLineDao.getPointRadius());
        dLineDao.getClickPointArea().put(0, rectF0);
    }


    /**
     * 保存第二个点
     */
    public void saveSecondPoint(float width, float height, MotionEvent event, DLineDao dLineDao) {
        dLineDao.getPoints().put(1, new PointF(event.getX(), event.getY()));
        RectF rectF0 = new RectF(event.getX() - dLineDao.getPointRadius(),
                event.getY() - dLineDao.getPointRadius(),
                event.getX() + dLineDao.getPointRadius(),
                event.getY() + dLineDao.getPointRadius());
        dLineDao.getClickPointArea().put(1, rectF0);
        // 计算直线坐标
        computeLine(width, height, dLineDao);
    }

    /**
     * 修改第二个点
     */
    public void updateSecondPoint(float width, float height, PointF updatePoint, DLineDao dLineDao) {
        dLineDao.getPoints().put(1, updatePoint);
        RectF rectF0 = new RectF(updatePoint.x - dLineDao.getPointRadius(), updatePoint.y - dLineDao.getPointRadius(),
                updatePoint.x + dLineDao.getPointRadius(), updatePoint.y + dLineDao.getPointRadius());

        dLineDao.getClickPointArea().put(1, rectF0);
        // 计算直线坐标
        computeLine(width, height, dLineDao);
    }


    /**
     * 平移两个直线点
     */
    public void translationLinePoint(float w, float h, PointF eventS, PointF eventE, DLineDao dLineDao) {
        if (dLineDao.getPoints() != null && dLineDao.getPoints().size() == 2) {
            // 屏幕矩阵
            RectF screen = new RectF(0, 0, w, h);

            float moveY = eventE.y - eventS.y;
            float moveX = eventE.x - eventS.x;

            PointF point0 = dLineDao.getPoints().get(0);
            PointF point1 = dLineDao.getPoints().get(1);
            Log.d(TAG, "老点：" + point0 + " - " + point1);

            // 锚点都在屏幕内才移动
            if (screen.contains(point0.x + moveX, point0.y + moveY) && screen.contains(point1.x + moveX, point1.y + moveY)) {

                point0.set(point0.x + moveX, point0.y + moveY);
                point1.set(point1.x + moveX, point1.y + moveY);
                Log.d(TAG, "新点：" + point0 + " - " + point1);

                updateFirstPoint(point0, dLineDao);
                updateSecondPoint(w, h, point1, dLineDao);
            }
        }
    }

    @Override
    public void onTouchActionMove(View view, MotionEvent event) {
        if (dLineDao.getPoints() != null) {
            if (dLineDao.getPoints().size() > 0) {
                if (dLineDao.getDrawState() == DLineDao.DRAW_STATE_ROTATION) {
                    // 做旋转绘制
                    saveSecondPoint(markerView.getWidth(), markerView.getHeight(), event, dLineDao);

                } else if (dLineDao.getDrawState() == DLineDao.DRAW_STATE_TRANSLATION) {
                    // 做平移绘制
                    if (translationStart != null) {
                        translationLinePoint(markerView.getWidth(), markerView.getHeight(), translationStart, new PointF(event.getX(), event.getY()), dLineDao);
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
        dLineDao.setDrawState(DLineDao.DRAW_STATE_ROTATION);
        markerView.invalidate();
    }

    @Override
    public void onSingleTapConfirmed(MotionEvent e) {
        logEventW("onSingleTapConfirmed", e);
        if (dLineDao.getPoints() != null) {
            if (dLineDao.getPoints().size() == 0) {
                saveFirstPoint(e, dLineDao);
            } else {
                saveSecondPoint(markerView.getWidth(), markerView.getHeight(), e, dLineDao);
            }
            markerView.invalidate();
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {
        if (dLineDao != null) {
            SparseArray<RectF> clickPointArea = dLineDao.getClickPointArea();
            if (clickPointArea != null && clickPointArea.size() == 2) {
                RectF rectF = clickPointArea.get(0);
                if (rectF.contains(e.getX(), e.getY())) {
                    // 长按在点区域在第一个点
                    updateFirstPoint(dLineDao.getPoints().get(1), dLineDao);
                    // 保存第二个点
                    saveSecondPoint(markerView.getWidth(), markerView.getHeight(), e, dLineDao);
                    markerView.invalidate();
                } else {
                    // 判断点击点是否在线段附近
                    boolean thirdMiddlePoints = getThirdMiddlePoints(dLineDao.getPoints().get(0), dLineDao.getPoints().get(1), new PointF(e.getX(), e.getY()));
                    if (thirdMiddlePoints) {
                        // 初始化平移起点
                        translationStart = new PointF(e.getX(), e.getY());
                        // 设置平移状态
                        dLineDao.setDrawState(DLineDao.DRAW_STATE_TRANSLATION);
                        // 选中变粗
                        mLinePaint.setStrokeWidth(10);
                        markerView.invalidate();
                    }
                }
            }
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (dLineDao.getPoints() != null) {
            drawLine(canvas, mLinePaint, dLineDao);
            drawLeftCircle(canvas, mPointPaint, dLineDao);
            drawRightCircle(canvas, mPointPaint, dLineDao);
        }
    }

    @Override
    public boolean isHasDrawUtil(MotionEvent e) {
        if (dLineDao != null) {
            SparseArray<RectF> clickPointArea = dLineDao.getClickPointArea();
            if (clickPointArea != null && clickPointArea.size() == 2) {
                RectF rectF0 = clickPointArea.get(0);
                RectF rectF1 = clickPointArea.get(1);
                if (rectF0.contains(e.getX(), e.getY()) || rectF1.contains(e.getX(), e.getY()) || getThirdMiddlePoints(dLineDao.getPoints().get(0), dLineDao.getPoints().get(1), new PointF(e.getX(), e.getY()))) {
                    return true;
                }
            }
        }
        return false;
    }
}
