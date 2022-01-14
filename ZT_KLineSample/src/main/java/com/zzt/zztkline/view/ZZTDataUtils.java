package com.zzt.zztkline.view;

import com.vinsonguo.klinelib.model.HisData;
import com.vinsonguo.klinelib.model.KDJ;
import com.vinsonguo.klinelib.model.MACD;
import com.zzt.zztkline.net.KLineData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dell on 2017/11/9.
 */

public class ZZTDataUtils {


    /**
     * calculate average price and ma data
     */
    public static List<KLineData> calculateHisData(List<KLineData> list, KLineData lastData) {


        List<Double> ma5List = calculateMA(5, list);
        List<Double> ma10List = calculateMA(10, list);
        List<Double> ma20List = calculateMA(20, list);
        List<Double> ma30List = calculateMA(30, list);

        for (int i = 0; i < list.size(); i++) {
            KLineData hisData = list.get(i);

            hisData.setMa5(ma5List.get(i));
            hisData.setMa10(ma10List.get(i));
            hisData.setMa20(ma20List.get(i));
            hisData.setMa30(ma30List.get(i));
        }
        return list;
    }

    public static List<KLineData> calculateHisData(List<KLineData> list) {
        return calculateHisData(list, null);
    }

    /**
     * calculate MA value, return a double list
     *
     * @param dayCount for example: 5, 10, 20, 30
     */
    public static List<Double> calculateMA(int dayCount, List<KLineData> data) {
        dayCount--;
        List<Double> result = new ArrayList<>(data.size());
        for (int i = 0, len = data.size(); i < len; i++) {
            if (i < dayCount) {
                result.add(Double.NaN);
                continue;
            }
            double sum = 0;
            for (int j = 0; j < dayCount; j++) {
                sum += Double.parseDouble(data.get(i - j).getOpen());
            }
            result.add(+(sum / dayCount));
        }
        return result;
    }

    /**
     * calculate last MA value, return a double value
     */
    public static double calculateLastMA(int dayCount, List<HisData> data) {
        dayCount--;
        double result = Double.NaN;
        for (int i = 0, len = data.size(); i < len; i++) {
            if (i < dayCount) {
                result = Double.NaN;
                continue;
            }
            double sum = 0;
            for (int j = 0; j < dayCount; j++) {
                sum += data.get(i - j).getOpen();
            }
            result = (+(sum / dayCount));
        }
        return result;
    }


}
