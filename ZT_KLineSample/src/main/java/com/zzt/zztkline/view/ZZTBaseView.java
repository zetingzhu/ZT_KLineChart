package com.zzt.zztkline.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.Transformer;
import com.vinsonguo.klinelib.chart.AppCombinedChart;
import com.vinsonguo.klinelib.chart.ColorContentYAxisRenderer;
import com.zzt.klinelib.R;
import com.zzt.zztkline.net.KLineData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/14 0014.
 */

class ZZTBaseView extends LinearLayout {


    protected int mDecreasingColor;
    protected int mIncreasingColor;
    protected int mAxisColor;
    protected int mTransparentColor;


    public int MAX_COUNT = 150;
    public int MIN_COUNT = 10;
    public int INIT_COUNT = 80;

    protected List<KLineData> mData = new ArrayList<>(200);

    public ZZTBaseView(Context context) {
        this(context, null);
    }

    public ZZTBaseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZZTBaseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mAxisColor = ContextCompat.getColor(getContext(), R.color.axis_color);
        mTransparentColor = ContextCompat.getColor(getContext(), R.color.transparent);
        mDecreasingColor = ContextCompat.getColor(getContext(), R.color.decreasing_color);
        mIncreasingColor = ContextCompat.getColor(getContext(), R.color.increasing_color);
    }


    protected void moveToLast(CombinedChart chart) {
        if (mData.size() > INIT_COUNT) {
            chart.moveViewToX(mData.size() - INIT_COUNT);
        }
    }


    protected void setDescription(Chart chart, String text) {
        Description description = chart.getDescription();
//        float dx = chart.getWidth() - chart.getViewPortHandler().offsetRight() - description.getXOffset();
//        description.setPosition(dx, description.getTextSize());
        description.setText(text);
        description.setTextSize(12);
    }

    public KLineData getLastData() {
        if (mData != null && !mData.isEmpty()) {
            return mData.get(mData.size() - 1);
        }
        return null;
    }

}
