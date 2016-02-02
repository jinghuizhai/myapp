package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import model.Cookie;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by UI on 2015/12/25.
 */
public class CoverActivity extends Activity{
    private CoverActivity self = this;
    private Cookie cookieDb;

    //取得cookie,过期返回"",不过期返回cookie
    public String getCookie(){
        ArrayList result = cookieDb.find();
        if(result.size() > 0){
            HashMap map = (HashMap) result.get(0);
            String name = map.get("name").toString();
            String expire = map.get("expire").toString();
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try{
                Date date2 = dateFormat.parse(expire);
                if(date2.getTime() > new Date().getTime()){
                    return name;
                }else{
                    return "";
                }
            }catch (ParseException e){
                Log.e("parse date error",e.toString());
                return "";
            }
        }else{
            return "";
        }
    }
    private void checkCookie(){
        String cookie = getCookie();
        if(!cookie.equals("")){
            Session.setData("cookie",cookie);
            Intent intent = new Intent();
            intent.setClass(self,DashboardActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent();
            intent.setClass(self,LoginActivity.class);
            startActivity(intent);
        }
        self.finish();
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cover);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");
        TextView textview = (TextView)findViewById(R.id.cover_logo);
        textview.setTypeface(iconfont);
        textview.setTextSize(100);
        cookieDb = new Cookie(this);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                checkCookie();
            }
        };
        new Timer().schedule(timerTask,2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
