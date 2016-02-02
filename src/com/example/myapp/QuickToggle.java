package com.example.myapp;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.provider.CallLog;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by UI on 2015/12/28.
 */
public class QuickToggle {
    public static void back(final Activity ac,TextView tv){
//        BackClickListener mcl = new BackClickListener(ac);
//        tv.setOnClickListener(mcl);
        tv.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ac.finish();
            }
        });
    }
    public static void toggle(Activity from, Class to, LinearLayout ll,Intent intent){
        ToggleClickListner tcl = new ToggleClickListner(from,to,intent);
        ll.setOnClickListener(tcl);
    }
    public static void toggle(Activity from, Class to, LinearLayout ll){
        Intent intent = new Intent();
        ToggleClickListner tcl = new ToggleClickListner(from,to,intent);
        ll.setOnClickListener(tcl);
    }
    public static void toggle(Activity from, Class to, TextView ll){
        Intent intent = new Intent();
        ToggleClickListner tcl = new ToggleClickListner(from,to,intent);
        ll.setOnClickListener(tcl);
    }
    public static void toggle(Activity from, Class to, TextView ll,Intent intent){
        ToggleClickListner tcl = new ToggleClickListner(from,to,intent);
        ll.setOnClickListener(tcl);
    }
}
