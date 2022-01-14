package com.zzt.klinechart.entity;

import android.graphics.PointF;
import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画直线 的基本信息
 */
public class DLineDao extends DBaseDao {

    // 保存直线两个屏幕边缘点数量
    private SparseArray<PointF> linePoints;

    public DLineDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(2);
    }

    public SparseArray<PointF> getLinePoints() {
        return linePoints;
    }

    public void setLinePoints(SparseArray<PointF> linePoints) {
        this.linePoints = linePoints;
    }
}
