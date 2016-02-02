package com.example.myapp;

import android.app.Activity;
import android.content.Context;
import android.content.Entity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by UI on 2015/12/30.
 */
public class Util {
    public static void toast(final Context context,final String str){
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast tst = Toast.makeText(context, str, Toast.LENGTH_SHORT);
                tst.show();
            }
        });
    }
    public static boolean validate(String model,String str){
        if(model.equals("phone")){
            Pattern pt = Pattern.compile("(^1[3458][0-9]{9}$)");
            Matcher mt = pt.matcher(str);
            if(mt.find()){
                return true;
            }else{
                return false;
            }
        }else if(model.equals("password")){
            if(str.length() >= 6){
                return true;
            }else{
                return false;
            }
        }else if(model.equals("sms")){
            Pattern pt = Pattern.compile("^[0-9]{6}$");
            Matcher mt = pt.matcher(str);
            if(mt.find()){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    public static View progressbar(Context context){
        LinearLayout wrapLinear = new LinearLayout(context);

        FrameLayout.LayoutParams wrapStyle = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapLinear.setGravity(Gravity.CENTER);
        wrapStyle.setLayoutDirection(0);
        wrapLinear.setLayoutParams(wrapStyle);
        wrapLinear.setPadding(0,10,0,10);
        FrameLayout.LayoutParams circleStyle = new FrameLayout.LayoutParams(60,60);
        circleStyle.gravity = Gravity.CENTER;
        View circle = new CircularProgress(context);
        circle.setLayoutParams(circleStyle);
        wrapLinear.addView(circle);
        return wrapLinear;
    }
}
