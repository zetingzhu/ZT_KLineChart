package com.zzt.zztkline.net;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author: zeting
 * @date: 2020/12/7
 */
public interface RequestApi {

    String baseUrl = "http://money.finance.sina.com.cn";

    /**
     * http://money.finance.sina.com.cn/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?ma=no&symbol=sh601390&scale=60&datalen=200
     *
     * @param symbol  [市场][股票代码]
     * @param scale   [周期] 5、10、30、60分钟
     * @param datalen [长度] [长度]
     * @return
     */
    @GET("/quotes_service/api/json_v2.php/CN_MarketData.getKLineData?ma=no")
    Call<List<KLineData>> getLoginStatus1(@Query("symbol") String symbol,
                                          @Query("scale") String scale,
                                          @Query("datalen") String datalen);


    static RequestApi getApi() {
        return ApiRetrofitUtils.getInstance().getApiService(baseUrl, RequestApi.class);
    }

}
