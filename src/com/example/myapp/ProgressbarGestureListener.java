package com.example.myapp;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by UI on 2016/1/21.
 */
public class ProgressbarGestureListener extends GestureDetector.SimpleOnGestureListener{
    private View view;
    private RichFragment fragment;
    private boolean loading = false;
    private boolean inRange = false;

    public void setFragment(RichFragment fragment){
        this.fragment = fragment;
    }
    public void onTouch(MotionEvent event){
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float from = view.getY();
            if (from > 100) {
                loading = true;
                ObjectAnimator.ofFloat(view, "Y", from, 50.0F)
                        .setDuration(200)
                        .start();
                if(fragment != null){
                    fragment.requestData();
                }
            } else {
                ObjectAnimator.ofFloat(view, "Y", from, -120.0F)
                        .setDuration(200)
                        .start();
            }
        }
    }
    public void hide(){
        float from = view.getY();
        ObjectAnimator.ofFloat(view,"Y",from,-120F)
                .setDuration(200)
                .start();
        loading = false;
    }
    public void show(){
        view.setY(200);
    }
    @Override
    public boolean onDown(MotionEvent e) {
        if(e.getX() > 200){
            inRange = true;
        }else{
            inRange = false;
        }
        return super.onDown(e);
    }
    public void setView(View view){
        this.view = view;
    }
    public View getView(){
        return view;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if(!loading && inRange){
            float tmp = e2.getY() - e1.getY();
            if(tmp > 0){
                view.setY(-100+tmp/2);
            }
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
