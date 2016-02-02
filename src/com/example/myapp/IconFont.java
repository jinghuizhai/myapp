package com.example.myapp;

import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * Created by UI on 2015/12/28.
 */
public class IconFont {
    private static Typeface iconfont = null;
    public static void loadFont(Activity ac,int[] arr){
        if(iconfont == null){
           iconfont = Typeface.createFromAsset(ac.getAssets(), "iconfont/iconfont.ttf");
        }
        for(int i = 0;i<arr.length;i++){
            int id = arr[i];
            TextView textview = (TextView)ac.findViewById(id);
            textview.setTypeface(iconfont);
        }
    }
    public static void loadFont(Activity ac,int i){
        int[] arr = {i};
        IconFont.loadFont(ac,arr);
    }
}
