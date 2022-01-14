package com.zzt.klinechart.entity;

import android.graphics.Color;
import android.graphics.PointF;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 黄金分割线 的基本信息
 */
public class DGoldenSectionLineDao extends DBaseDao {

    // 平行比例
    private List<Float> goldenSection;
    // 虚线颜色
    private int dashedColor;
    // 分割线文字颜色
    private int textColor;


    public DGoldenSectionLineDao() {
        this.clickPointArea = new SparseArray<>(2);
        this.points = new SparseArray<>(4);
        goldenSection = new ArrayList<>(4);
        goldenSection.add(0.191f);
        goldenSection.add(0.382f);
        goldenSection.add(0.5f);
        goldenSection.add(0.618f);
        this.dashedColor = Color.parseColor("#FF0000");
        this.textColor = Color.parseColor("#BB0022FF");
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getDashedColor() {
        return dashedColor;
    }

    public void setDashedColor(int dashedColor) {
        this.dashedColor = dashedColor;
    }

    public List<Float> getGoldenSection() {
        return goldenSection;
    }

    public void setGoldenSection(List<Float> goldenSection) {
        this.goldenSection = goldenSection;
    }
}
