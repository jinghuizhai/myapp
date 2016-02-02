package com.example.myapp;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by UI on 2016/1/27.
 */
public class MenuListener extends GestureDetector.SimpleOnGestureListener {
    private boolean inRange = false;
    private LinearLayout menuBelow;
    private LinearLayout menuWrap;

    public void setMenuBelow(LinearLayout menuBelow){
        this.menuBelow = menuBelow;
    }

    public void setMenuWrap(LinearLayout menuWrap){
        this.menuWrap = menuWrap;
    }

    public void callout(){
        menuBelow.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(menuWrap,"X",0.0F)
                .setDuration(100)
                .start();
        ObjectAnimator.ofFloat(menuBelow,"alpha",0.5F)
                .setDuration(100)
                .start();
    }

    public void onTouch(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_UP){
            int width = menuWrap.getWidth();
            if(menuWrap.getX() > (-width+150)){
                callout();
            }else{
                menuWrap.setX(-width);
                menuBelow.setAlpha(0.0F);
                menuBelow.setVisibility(View.INVISIBLE);
            }
            inRange = false;
        }
    }

    public boolean onDown(MotionEvent e) {
        if(e.getX() > 0 && e.getX() < 50){
            inRange = true;
            menuBelow.setVisibility(View.VISIBLE);
        }
        return super.onDown(e);
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return super.onFling(e1, e2, velocityX, velocityY);
    }
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        int width = menuWrap.getWidth();
        if(inRange){
            float tmp = e2.getX() - e1.getX();
            if(tmp > 0 && tmp < width){
                menuBelow.setAlpha(tmp/2/width);
                menuWrap.setX(-width+tmp);
            }
        }
        return super.onScroll(e1, e2, distanceX, distanceY);
    }
}
