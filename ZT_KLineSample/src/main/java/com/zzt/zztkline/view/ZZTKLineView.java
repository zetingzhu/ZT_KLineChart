package com.zzt.zztkline.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.utils.Transformer;
import com.vinsonguo.klinelib.chart.AppCombinedChart;
import com.vinsonguo.klinelib.chart.ChartInfoViewHandler;
import com.vinsonguo.klinelib.chart.ColorContentYAxisRenderer;
import com.vinsonguo.klinelib.chart.CoupleChartGestureListener;
import com.vinsonguo.klinelib.util.DoubleUtil;
import com.zzt.zztkline.R;
import com.zzt.zztkline.net.KLineData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * kline
 * Created by guoziwei on 2017/10/26.
 */
public class ZZTKLineView extends ZZTBaseView implements CoupleChartGestureListener.OnAxisChangeListener {


    public static final int NORMAL_LINE = 0;

    /**
     * hide line
     */
    public static final int INVISIABLE_LINE = 6;


    public static final int MA5 = 5;
    public static final int MA10 = 10;
    public static final int MA20 = 20;
    public static final int MA30 = 30;


    protected AppCombinedChart mChartPrice;

    protected Context mContext;


    /**
     * yesterday close price
     */
    private double mLastClose;

    /**
     * the digits of the symbol
     */
    private int mDigits = 2;

    public ZZTKLineView(Context context) {
        this(context, null);
    }

    public ZZTKLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZZTKLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.kline_view_item, this);
        mChartPrice = (AppCombinedChart) findViewById(R.id.price_chart);

        mChartPrice.setNoDataText(context.getString(R.string.loading));
        initChartPrice();

        initChartListener();
    }

    protected void initChartPrice() {
        mChartPrice.setScaleEnabled(true);
        mChartPrice.setDrawBorders(true);
        mChartPrice.setBorderWidth(2);
        mChartPrice.setBorderColor(Color.GREEN);
        mChartPrice.setDragEnabled(true);
        mChartPrice.setScaleYEnabled(false);
        mChartPrice.setAutoScaleMinMaxEnabled(true);
        mChartPrice.setDragDecelerationEnabled(false);
        mChartPrice.setDrawMarkers(true);
        Legend lineChartLegend = mChartPrice.getLegend();
        lineChartLegend.setEnabled(false);

        XAxis xAxisPrice = mChartPrice.getXAxis();
        xAxisPrice.setDrawLabels(true);
        xAxisPrice.setDrawAxisLine(false);
        xAxisPrice.setDrawGridLines(false);
        // X轴更多属性
        xAxisPrice.setLabelRotationAngle(0);   // 标签倾斜
        xAxisPrice.setPosition(XAxis.XAxisPosition.BOTTOM);  // X轴绘制位置，默认是顶部

        // 左边
        YAxis axisLeftPrice = mChartPrice.getAxisLeft();
        axisLeftPrice.setLabelCount(10, true);
        axisLeftPrice.setDrawLabels(true);
        axisLeftPrice.setDrawGridLines(false);
        axisLeftPrice.setAxisLineWidth(2);
        axisLeftPrice.setAxisLineColor(Color.GREEN);
        axisLeftPrice.setDrawAxisLine(true);
        axisLeftPrice.setTextSize(30);
        axisLeftPrice.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        axisLeftPrice.setTextColor(mAxisColor);
        axisLeftPrice.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return DoubleUtil.getStringByDigits(value, mDigits);
            }
        });
        int[] colorArray = {mDecreasingColor, mDecreasingColor, mAxisColor, mIncreasingColor, mIncreasingColor};
        Transformer leftYTransformer = mChartPrice.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisLeft(), leftYTransformer);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
        leftColorContentYAxisRenderer.setLabelInContent(true);
        leftColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        mChartPrice.setRendererLeftYAxis(leftColorContentYAxisRenderer);

        // 右边
        YAxis axisRightPrice = mChartPrice.getAxisRight();
        axisRightPrice.setLabelCount(10, true);
        axisRightPrice.setDrawLabels(false);
        axisRightPrice.setDrawGridLines(false);
        axisLeftPrice.setTextSize(20);
        axisRightPrice.setDrawAxisLine(false);
        axisRightPrice.setTextColor(mAxisColor);
        axisRightPrice.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

//        设置标签Y渲染器
        Transformer rightYTransformer = mChartPrice.getRendererRightYAxis().getTransformer();
        ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(mChartPrice.getViewPortHandler(), mChartPrice.getAxisRight(), rightYTransformer);
        rightColorContentYAxisRenderer.setLabelInContent(true);
        rightColorContentYAxisRenderer.setUseDefaultLabelXOffset(false);
        rightColorContentYAxisRenderer.setLabelColor(colorArray);
        mChartPrice.setRendererRightYAxis(rightColorContentYAxisRenderer);

        // 距离边缘距离
//        mChartPrice.setViewPortOffsets(0, 0, 0, 0);
//        List<IBarLineScatterCandleBubbleDataSet<? extends Entry>> dataSets = mChartPrice.getData().getDataSets();
    }

    private void initChartListener() {
        mChartPrice.setOnChartGestureListener(new CoupleChartGestureListener(this, mChartPrice));
        mChartPrice.setOnTouchListener(new ChartInfoViewHandler(mChartPrice));
    }

    public void initData(List<KLineData> hisDatas) {
        mData.clear();
        mData.addAll(ZZTDataUtils.calculateHisData(hisDatas));

        ArrayList<CandleEntry> lineCJEntries = new ArrayList<>(INIT_COUNT);
        ArrayList<Entry> ma5Entries = new ArrayList<>(INIT_COUNT);
        ArrayList<Entry> ma10Entries = new ArrayList<>(INIT_COUNT);
        ArrayList<Entry> ma20Entries = new ArrayList<>(INIT_COUNT);
        ArrayList<Entry> ma30Entries = new ArrayList<>(INIT_COUNT);
        ArrayList<Entry> paddingEntries = new ArrayList<>(INIT_COUNT);

        for (int i = 0; i < mData.size(); i++) {
            KLineData hisData = mData.get(i);
            lineCJEntries.add(new CandleEntry(i, Float.parseFloat(hisData.getHigh()), Float.parseFloat(hisData.getLow()),
                    Float.parseFloat(hisData.getOpen()), Float.parseFloat(hisData.getClose())));

            if (!Double.isNaN(hisData.getMa5())) {
                ma5Entries.add(new Entry(i, (float) hisData.getMa5()));
            }

            if (!Double.isNaN(hisData.getMa10())) {
                ma10Entries.add(new Entry(i, (float) hisData.getMa10()));
            }

            if (!Double.isNaN(hisData.getMa20())) {
                ma20Entries.add(new Entry(i, (float) hisData.getMa20()));
            }

            if (!Double.isNaN(hisData.getMa30())) {
                ma30Entries.add(new Entry(i, (float) hisData.getMa30()));
            }
        }

        if (!mData.isEmpty() && mData.size() < MAX_COUNT) {
            for (int i = mData.size(); i < MAX_COUNT; i++) {
                paddingEntries.add(new Entry(i, Float.parseFloat(mData.get(mData.size() - 1).getClose())));
            }
        }

        LineData lineData = new LineData(
                setLine(INVISIABLE_LINE, paddingEntries),
                setLine(MA5, ma5Entries),
                setLine(MA10, ma10Entries),
                setLine(MA20, ma20Entries),
                setLine(MA30, ma30Entries));
        CandleData candleData = new CandleData(setKLine(NORMAL_LINE, lineCJEntries));
        CombinedData combinedData = new CombinedData();
        combinedData.setData(lineData);
        combinedData.setData(candleData);
        mChartPrice.setData(combinedData);

        mChartPrice.setVisibleXRange(MAX_COUNT, MIN_COUNT);
        mChartPrice.notifyDataSetChanged();
        moveToLast(mChartPrice);

        mChartPrice.getXAxis().setAxisMaximum(combinedData.getXMax() + 0.5f);

        mChartPrice.zoom(MAX_COUNT * 1f / INIT_COUNT, 0, 0, 0);

        lineData.removeDataSet(0);

        KLineData hisData = getLastData();
        setDescription(mChartPrice, String.format(Locale.getDefault(), "MA5:%.2f  MA10:%.2f  MA20:%.2f  MA30:%.2f",
                hisData.getMa5(), hisData.getMa10(), hisData.getMa20(), hisData.getMa30()));
    }


    private LineDataSet setLine(int type, ArrayList<Entry> lineEntries) {
        LineDataSet lineDataSetMa = new LineDataSet(lineEntries, "ma" + type);
        lineDataSetMa.setDrawValues(false);
        if (type == NORMAL_LINE) {
            lineDataSetMa.setColor(getResources().getColor(R.color.normal_line_color));
            lineDataSetMa.setCircleColor(ContextCompat.getColor(mContext, R.color.normal_line_color));
        } else if (type == MA5) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma5));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA10) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma10));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA20) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma20));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else if (type == MA30) {
            lineDataSetMa.setColor(getResources().getColor(R.color.ma30));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setHighlightEnabled(false);
        } else {
            lineDataSetMa.setColor(getResources().getColor(R.color.hide_line));
            lineDataSetMa.setCircleColor(mTransparentColor);
            lineDataSetMa.setVisible(true);
            lineDataSetMa.setHighlightEnabled(false);
        }
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSetMa.setLineWidth(1f);
        lineDataSetMa.setCircleRadius(1f);

        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setDrawCircleHole(false);

        return lineDataSetMa;
    }

    public CandleDataSet setKLine(int type, ArrayList<CandleEntry> lineEntries) {
        CandleDataSet set = new CandleDataSet(lineEntries, "KLine" + type);
        set.setDrawIcons(false);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setShadowColor(Color.DKGRAY);
        set.setShadowWidth(0.75f);
        set.setDecreasingColor(mDecreasingColor);
        set.setDecreasingPaintStyle(Paint.Style.FILL);
        set.setShadowColorSameAsCandle(true);
        set.setIncreasingColor(mIncreasingColor);
        set.setIncreasingPaintStyle(Paint.Style.FILL);
        set.setNeutralColor(ContextCompat.getColor(getContext(), R.color.increasing_color));
        set.setDrawValues(true);
        set.setValueTextSize(10);
        set.setHighlightEnabled(true);
        if (type != NORMAL_LINE) {
            set.setVisible(false);
        }
        return set;
    }


    @Override
    public void onAxisChange(BarLineChartBase chart) {
        float lowestVisibleX = chart.getLowestVisibleX();
        if (lowestVisibleX <= chart.getXAxis().getAxisMinimum()) return;
        int maxX = (int) chart.getHighestVisibleX();
        int x = Math.min(maxX, mData.size() - 1);
        KLineData hisData = mData.get(x < 0 ? 0 : x);
        setDescription(mChartPrice, String.format(Locale.getDefault(), "MA5:%.2f  MA10:%.2f  MA20:%.2f  MA30:%.2f",
                hisData.getMa5(), hisData.getMa10(), hisData.getMa20(), hisData.getMa30()));

    }
}
