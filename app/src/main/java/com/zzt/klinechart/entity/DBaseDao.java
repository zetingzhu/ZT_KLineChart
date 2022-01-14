package com.zzt.klinechart.entity;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/14
 * 画图工具基础属性
 */
public class DBaseDao {
    // 当前状态旋转
    public final static int DRAW_STATE_ROTATION = 0;
    // 当前状态平移
    public final static int DRAW_STATE_TRANSLATION = 1;
    // 当前画图状态
    protected int drawState = DRAW_STATE_ROTATION;
    // 保存的点的数量
    protected SparseArray<PointF> points;
    // 点击区域集合 点
    protected SparseArray<RectF> clickPointArea;
    // 点颜色
    protected int pointColor = Color.parseColor("#FF0000");
    // 点的半径
    protected int pointRadius = 10;
    // 线段颜色
    protected int lineColor = Color.parseColor("#0022FF");
    // 线段宽度
    protected int lineWidth = 5;
    // 当前线段是否可以使用
    protected boolean isEnableCanvas ;

    public boolean isEnableCanvas() {
        return isEnableCanvas;
    }

    public void setEnableCanvas(boolean enableCanvas) {
        isEnableCanvas = enableCanvas;
    }

    public int getDrawState() {
        return drawState;
    }

    public void setDrawState(int drawState) {
        this.drawState = drawState;
    }

    public SparseArray<PointF> getPoints() {
        return points;
    }

    public void setPoints(SparseArray<PointF> points) {
        this.points = points;
    }

    public SparseArray<RectF> getClickPointArea() {
        return clickPointArea;
    }

    public void setClickPointArea(SparseArray<RectF> clickPointArea) {
        this.clickPointArea = clickPointArea;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public int getPointRadius() {
        return pointRadius;
    }

    public void setPointRadius(int pointRadius) {
        this.pointRadius = pointRadius;
    }

    public int getLineColor() {
        return lineColor;
    }

    public void setLineColor(int lineColor) {
        this.lineColor = lineColor;
    }

    public int getLineWidth() {
        return lineWidth;
    }

    public void setLineWidth(int lineWidth) {
        this.lineWidth = lineWidth;
    }

}
