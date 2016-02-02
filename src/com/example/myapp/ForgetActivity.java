package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;
import util.Request;

import java.util.*;

/**
 * Created by UI on 2016/1/7.
 */
public class ForgetActivity extends Activity{
    private boolean countdown(Button btn,int second){
        if(second > 0){
            try {
                String rest = second+"秒后可重发";
                btn.setText(rest);
                Thread.sleep(1000);
                second--;
                countdown(btn, second);
            }catch (InterruptedException e){
                return false;
            }
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget);

        final Activity self = this;
        final Button smsButton = (Button)findViewById(R.id.button_sms);
        final EditText phoneInput = (EditText)findViewById(R.id.input_phone);
        final EditText smsInput = (EditText)findViewById(R.id.input_sms);
        final Button okButton = (Button)findViewById(R.id.button_ok);
        final LinearLayout smsWrap = (LinearLayout)findViewById(R.id.sms_wrap);

        phoneInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phone = phoneInput.getText().toString();
                if(phone.length() == 11 && Util.validate("phone",phone)){
                    smsButton.setEnabled(true);
                    smsButton.setAlpha((float)1);
                }else{
                    smsButton.setEnabled(false);
                    smsButton.setAlpha((float)0.5);
                }
            }
            public void afterTextChanged(Editable editable) {

            }
        });

        smsInput.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sms = smsInput.getText().toString();
                if(sms.length() == 6 && Util.validate("sms",sms)){
                    okButton.setEnabled(true);
                    okButton.setAlpha((float)1);
                }else{
                    okButton.setEnabled(false);
                    okButton.setAlpha((float)0.5);
                }
            }

            public void afterTextChanged(Editable editable) {

            }
        });

        final Handler smsHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                final Handler that = this;
                //update sms button text
                if(msg.what == 103){
                    String text = msg.obj.toString();
                    smsButton.setText(text);
                }
                //reset sms button to normal status
                if(msg.what == 104){
                    smsButton.setText("发送验证码");
                    smsButton.setEnabled(true);
                    smsButton.setAlpha((float)1);
                }
                //return login or validate sms go on
                if(msg.what == 105){
                    try{
                        String result = msg.obj.toString();
                        JSONTokener jt = new JSONTokener(result);
                        JSONObject job = (JSONObject) jt.nextValue();
                        int errorCode = job.getInt("error_code");
                        if(errorCode == 0){
                            //return login activity
                            Intent intent = new Intent();
                            intent.setClass(self,LoginActivity.class);
                            startActivity(intent);
                            Util.toast(self,"请重新登录");
                        }else{
                            Request.alert(self,errorCode);
                        }
                    }catch (JSONException e){
                        Log.i("---","error");
                        okButton.setEnabled(true);
                        okButton.setAlpha((float)1);
                    }
                }
                if(msg.what == 102){
                    try {
                        String result = msg.obj.toString();
                        JSONTokener jt = new JSONTokener(result);
                        JSONObject job = (JSONObject) jt.nextValue();
                        int errorCode = job.getInt("error_code");
                        //success
                        if(errorCode == 0){
                            //set timer
                            final Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                private int begin = 10;
                                @Override
                                public void run() {
                                    if(begin > 0){
                                        Message msg = new Message();
                                        msg.obj = begin+"秒后可重新发送";
                                        msg.what = 103;
                                        that.sendMessage(msg);
                                        begin--;
                                    }else{
                                        Message msg = new Message();
                                        msg.what = 104;
                                        that.sendMessage(msg);
                                        timer.cancel();
                                    }
                                }
                            },0, 1000);
                            //rest component show
                            smsWrap.setVisibility(View.VISIBLE);
                            Animation an = AnimationUtils.loadAnimation(self,R.anim.alpha_plus);
                            smsWrap.startAnimation(an);
                        }else{
                            smsButton.setEnabled(true);
                            smsButton.setAlpha((float)1);
                            Request.alert(self,errorCode);
                        }

                    }catch (JSONException e){
                        Request.alert(self,5);
                        smsButton.setEnabled(true);
                        smsButton.setAlpha((float)1);
                    }
                }
            }
        };

        smsButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                final String phone = phoneInput.getText().toString().trim();
                smsButton.setEnabled(false);
                smsButton.setAlpha((float)0.5);
                new Thread(){
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("phone",phone);
                        Request request = new Request(self);
                        String result = request.post("sms/get",map);
                        Message msg = new Message();
                        msg.what = 102;
                        msg.obj = result;
                        smsHandler.sendMessage(msg);
                    }

                }.start();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String sms = smsInput.getText().toString();
                final HashMap map = new HashMap();
                map.put("sms",sms);
                new Thread(){
                    @Override
                    public void run() {
                        Request request = new Request(self);
                        String result = request.post("newpassword/get",map);
                        Message msg = new Message();
                        msg.obj = result.trim();
                        msg.what = 105;
                        smsHandler.sendMessage(msg);
                    }
                }.start();
            }
        });


        Button backButton = (Button)findViewById(R.id.button_back);
        QuickToggle.back(this,backButton);
    }
}
