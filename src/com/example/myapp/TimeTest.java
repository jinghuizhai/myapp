package com.example.myapp;

import android.os.Handler;
import android.util.Log;
import android.widget.Button;

import java.sql.Array;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by UI on 2015/12/25.
 */

interface walk{
    void walk();
}
interface shout{
    void shout();
}


public class TimeTest{
    private static int a;
    public static void main(String args[]){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println(new Date());
                a ++;
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,0,1000);
        while(true){
            if(a > 5){
                timer.cancel();
            }
        }
    }
}
