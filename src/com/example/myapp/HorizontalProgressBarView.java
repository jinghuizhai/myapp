package com.example.myapp;

/**
 * Created by UI on 2016/1/5.
 */
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

@SuppressLint("DrawAllocation")
public class HorizontalProgressBarView extends View {
    private int progress = 50;
    private int max = 100;
    private int mwidth = 450;
    private int mhight = 40;
    private int startX;
    private int startY;
    private Paint paint;

    public int getMwidth() {
        return mwidth;
    }

    public void setMwidth(int mwidth) {
        this.mwidth = mwidth;
    }

    public int getMhight() {
        return mhight;
    }

    public void setMhight(int mhight) {
        this.mhight = mhight;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public HorizontalProgressBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setAntiAlias(true);// �����Ƿ񿹾��
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);// �����������
        paint.setColor(Color.parseColor("#EFEFEF"));// ���û��ʻ�ɫ
        paint.setStrokeWidth(5);// ���û��ʿ��
        canvas.drawRect(startX, startY, mwidth, mhight, paint);
        paint.setColor(Color.rgb(27,139,251));
        canvas.drawRect(startX, startY, ((float) progress / max) * mwidth,
                mhight, paint);
        // �����ڲ�����
//        paint.setStrokeWidth(2);
//        paint.setColor(Color.YELLOW);
//        canvas.drawLine(startX, startY + (mhight - startY) / 4 * 1,
//                ((float) progress / max) * mwidth, startY + (mhight - startY)
//                        / 4 * 1, paint);
//        paint.setColor(Color.RED);
//        canvas.drawLine(startX, startY + (mhight - startY) / 4 * 2,
//                ((float) progress / max) * mwidth, startY + (mhight - startY)
//                        / 4 * 2, paint);
//        paint.setColor(Color.WHITE);
//        canvas.drawLine(startX, startY + (mhight - startY) / 4 * 3,
//                ((float) progress / max) * mwidth, startY + (mhight - startY)
//                        / 4 * 3, paint);
        // �����±��������
        paint.setColor(Color.rgb(153,153,153));
        paint.setTextSize(30);
        canvas.drawText(progress + "%", ((float) progress / max) * mwidth - 20,
                                mhight + 30, paint);
    }
}

