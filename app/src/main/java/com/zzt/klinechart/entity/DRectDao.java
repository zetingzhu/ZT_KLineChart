package com.zzt.klinechart.entity;

import android.graphics.Color;
import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 画框的基本信息
 */
public class DRectDao extends DBaseDao {
    public DRectDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(4);
        this.pointColor = Color.parseColor("#FF0000");
        this.lineColor = Color.parseColor("#0022FF");
    }
}
