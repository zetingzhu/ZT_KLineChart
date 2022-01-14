package com.zzt.klinechart.lis;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.zzt.klinechart.draw.DrawCanvasBaseUtil;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

/**
 * @author: zeting
 * @date: 2022/1/12
 * 处理所有视图工具类
 */
//public class CompositeDLineCallback<T extends DrawCanvasBaseUtil> {
//
//    @NonNull
//    private final List<DLineTouchListener<DrawCanvasBaseUtil>> mCallbacks;
//
//    public CompositeDLineCallback() {
//        this.mCallbacks = new ArrayList<>(3);
//    }
//
//    public void addDLineCallback(DLineTouchListener<DrawCanvasBaseUtil> callback) {
//        mCallbacks.add(0, callback);
//    }
//
//    public int getListCount() {
//        return mCallbacks.size();
//    }
//
//    public void removeDLineCallback(DLineTouchListener<DrawCanvasBaseUtil> callback) {
//        mCallbacks.remove(callback);
//    }
//
//    public boolean onTouch(View view, MotionEvent event) {
//        try {
//            for (DLineTouchListener<DrawCanvasBaseUtil> callback : mCallbacks) {
//                boolean touchBoo = callback.onTouch(view, event);
//                if (touchBoo) {
//                    return true;
//                }
//            }
//        } catch (ConcurrentModificationException ex) {
//            throwCallbackListModifiedWhileInUse(ex);
//        }
//        return false;
//    }
//
//
//    public void onDraw(Canvas canvas) {
//        try {
//            for (DLineTouchListener<DrawCanvasBaseUtil> callback : mCallbacks) {
//                callback.onDraw(canvas);
//            }
//        } catch (ConcurrentModificationException ex) {
//            throwCallbackListModifiedWhileInUse(ex);
//        }
//    }
//
//    private void throwCallbackListModifiedWhileInUse(ConcurrentModificationException parent) {
//        throw new IllegalStateException("不支持在调度回调期间添加和删除回调", parent);
//    }
//
//}
