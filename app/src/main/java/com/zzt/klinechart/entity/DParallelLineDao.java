package com.zzt.klinechart.entity;

import android.graphics.PointF;
import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 平行线 的基本信息
 */
public class DParallelLineDao extends DBaseDao {

    // 计算得到保持平行的两个点
    private SparseArray<PointF> parallelPoints;

    public DParallelLineDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(4);
        this.parallelPoints = new SparseArray<>(2);
    }

    public SparseArray<PointF> getParallelPoints() {
        return parallelPoints;
    }

    public void setParallelPoints(SparseArray<PointF> parallelPoints) {
        this.parallelPoints = parallelPoints;
    }
}
