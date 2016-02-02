package com.example.myapp;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by UI on 2016/1/19.
 */
public class RoundProgressbarView extends View{
    public RoundProgressbarView(Context context){
        super(context);
    }

    protected void onDraw(Canvas canvas) {

        canvas.drawRGB(255,255,255);
        int width = canvas.getWidth()/2;
        int height = canvas.getHeight()/2;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(50);
        paint.setColor(Color.BLACK);
        paint.setAlpha(20);
        canvas.drawCircle(width+2,height+2,100,paint);

        Paint paint2 = new Paint();
        paint2.setAntiAlias(true);
        paint2.setStyle(Paint.Style.FILL);
        paint2.setStrokeWidth(50);
        paint2.setColor(Color.WHITE);
        canvas.drawCircle(width,height,100,paint2);


//        Paint small = new Paint();
//        small.setColor(Color.RED);
//        small.setStrokeWidth(4);
//        small.setColor(Color.RED);  //设置进度的颜色
//        RectF oval1=new RectF(150,20,180,40);
//        canvas.drawArc(oval1, 180, 180, false, small);//小弧形
//        oval1.set(190, 20, 220, 40);
//        canvas.drawArc(oval1, 180, 180, false, small);//小弧形
//        oval1.set(160, 30, 210, 60);
//        canvas.drawArc(oval1, 0, 180, false, small);//
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        RectF oval = new RectF(100,100,1500,1500);
        canvas.drawArc(oval,0,360,true,paint);

        Paint text = new Paint();
        text.setAntiAlias(true);
        text.setTextSize(16);
        text.setColor(Color.BLUE);

        canvas.drawText("haha",100,100,text);

    }
}
