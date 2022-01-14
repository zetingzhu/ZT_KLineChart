package com.zzt.zztkline.net;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * @author: zeting
 * @date: 2020/12/7
 * Retrofit 工具封装
 */
public class ApiRetrofitUtils {
    private static final String TAG = ApiRetrofitUtils.class.getSimpleName();

    private static ApiRetrofitUtils apiRetrofit;
    private static OkHttpClient client;
    private static Retrofit retrofit;

    public static ApiRetrofitUtils getInstance() {
        if (apiRetrofit == null) {
            synchronized (Object.class) {
                if (apiRetrofit == null) {
                    apiRetrofit = new ApiRetrofitUtils();
                }
            }
        }
        return apiRetrofit;
    }


    private static synchronized OkHttpClient getOkHttpClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    Log.i(TAG, message);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, message);
                }
            }
        });

        //包含header、body数据
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return client = new OkHttpClient.Builder()
                //添加log拦截器
                .addInterceptor(interceptor)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized Retrofit getRetrofit(String baseUrl) {
        return retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(getOkHttpClient())
                .build();
    }

    public <T> T getApiService(String ShowUrl, Class<T> service) {
        Retrofit retrofit = getRetrofit(ShowUrl);
        T t = retrofit.create(service);
        return t;
    }

}