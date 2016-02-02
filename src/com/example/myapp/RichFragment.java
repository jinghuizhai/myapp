package com.example.myapp;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by UI on 2016/1/25.
 */
public abstract class RichFragment extends CommonFragment{
    public abstract void onTouchEvent(MotionEvent event);
    public abstract void requestData();
}
