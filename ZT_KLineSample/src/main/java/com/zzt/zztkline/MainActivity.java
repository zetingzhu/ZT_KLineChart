package com.zzt.zztkline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.zzt.zztkline.net.KLineData;
import com.zzt.zztkline.net.RequestApi;
import com.zzt.zztkline.view.ZZTKLineView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private ZZTKLineView mKLineView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mKLineView = findViewById(R.id.kline);
        showMacd();
    }

    public void showMacd() {
        RequestApi.getApi().getLoginStatus1("sh601390", "60", "200")
                .enqueue(new Callback<List<KLineData>>() {
                    @Override
                    public void onResponse(Call<List<KLineData>> call, Response<List<KLineData>> response) {
                        List<KLineData> body = response.body();
                        Log.e(TAG, "onResponse>>>>>> " + response.body());
                        mKLineView.initData(body);
                    }

                    @Override
                    public void onFailure(Call<List<KLineData>> call, Throwable throwable) {
                        Log.e(TAG, "onFailure>>>>>> " + throwable.toString());
                    }

                });

    }
}