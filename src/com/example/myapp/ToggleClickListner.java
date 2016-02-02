package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

/**
 * Created by UI on 2015/12/28.
 */
public class ToggleClickListner implements View.OnClickListener {
    private Activity from = null;
    private Class to = null;
    private Intent intent = null;

    public ToggleClickListner(Activity from, Class to, Intent intent){
        this.from = from;
        this.to = to;
        this.intent = intent;
    }

    public void onClick(View v){
        intent.setClass(from,to);
        from.startActivity(intent);
    }
}
