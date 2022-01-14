package com.zzt.klinechart.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * @author: zeting
 * @date: 2022/1/11
 * 关于设置大小的工具
 */
public class SizeUtils {
    public static int dp2px(Context context, float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, context.getResources().getDisplayMetrics());
    }

    public static int sp2px(Context context, float spValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }
}
