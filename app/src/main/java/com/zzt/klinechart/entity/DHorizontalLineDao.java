package com.zzt.klinechart.entity;

import android.graphics.Color;
import android.util.SparseArray;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 黄金分割线 的基本信息
 */
public class DHorizontalLineDao extends DBaseDao {

    public DHorizontalLineDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(4);
        this.lineColor = Color.parseColor("#00FFCC");
    }

}
