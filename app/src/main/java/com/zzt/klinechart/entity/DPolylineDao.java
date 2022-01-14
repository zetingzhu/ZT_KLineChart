package com.zzt.klinechart.entity;

import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/13
 * 画折线基本信息
 */
public class DPolylineDao extends DBaseDao {
    public DPolylineDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(4);
    }
}
