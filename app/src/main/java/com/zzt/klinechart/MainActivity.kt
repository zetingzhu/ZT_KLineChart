package com.zzt.klinechart

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import com.zzt.adapter.BtnHorizontalRecyclerAdapter
import com.zzt.klinechart.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    val TAG = MainActivity::class.java.simpleName
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        var btnList = mutableListOf<String>()
        btnList.add(0, "开始画线")
        btnList.add(1, "开始画矩形")
        btnList.add(2, "当前画图数量")
        btnList.add(3, "画3段折线")
        btnList.add(4, "画5段折线")
        btnList.add(5, "画平行线")
        btnList.add(6, "黄金分割线")
        btnList.add(7, "画水平线")
        btnList.add(8, "清空所有画图")


        BtnHorizontalRecyclerAdapter.setAdapterData(
            binding.rvHorBtn, btnList
        ) { itemView, position, data ->
            when (position) {
                0 -> {
                    binding.markerLineView.openDrawLine()
                }
                1 -> {
                    binding.markerLineView.openDrawRect()
                }
                2 -> {
                    binding.markerLineView.countDrawLine()
                }
                3 -> {
                    binding.markerLineView.openPolylineRect(3)
                }
                4 -> {
                    binding.markerLineView.openPolylineRect(5)
                }
                5 -> {
                    binding.markerLineView.openParallelLineRect()
                }
                6 -> {
                    binding.markerLineView.openGoldenSectionLineRect()
                }
                7 -> {
                    binding.markerLineView.openHorizontalLineRect()
                }
                8 -> {
                    binding.markerLineView.clearAllDraw()
                }
                else -> {

                }
            }
            Log.d(TAG, "点击了 其他 ：$data")
        }

        /**
         *  Custom view `AppCompatImageView` has setOnTouchListener called on it but does not override performClick
         */
        var mGestureDetector =
            GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
                override fun onShowPress(e: MotionEvent?) {
                    Log.d(TAG, "触摸")
                    super.onShowPress(e)
                }

                override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                    Log.d(TAG, "单击")
                    return super.onSingleTapConfirmed(e)
                }

                override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
                    Log.d(TAG, "双击")
                    return super.onDoubleTapEvent(e)
                }
            })

        binding.img1.visibility = View.GONE
        binding.img1.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
                //touch事件传给onTouchEvent()
                mGestureDetector.onTouchEvent(p1);
                return true
            }
        })
    }

}