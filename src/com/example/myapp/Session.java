package com.example.myapp;

import android.content.Context;
import model.Login;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UI on 2015/12/30.
 */
public class Session {
    private static HashMap data = new HashMap<String,String>();
    public static HashMap getData(){
        return data;
    }
    public static String getData(String key){
        Object obj = data.get(key);
        if(obj == null){
            return "";
        }else{
            return obj.toString();
        }
    }
    public static void remove(Object key){
        data.remove(key);
    }
    public static void setData(String key,String value){
        data.put(key,value);
    }
}
