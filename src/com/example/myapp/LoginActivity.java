package com.example.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import model.Cookie;
import model.Login;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.LongBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.myapp.Util.*;

/**
 * Created by UI on 2015/12/26.
 */
public class LoginActivity extends Activity {
    private LinearLayout progressBar;
    private LoginActivity self = this;
    private Button forgetBtn;
    private Button okBtn;
    private Handler handler;
    private RelativeLayout loginView;
    private String phone;
    private String password;
    private EditText textPhone;
    private EditText textPassword;

    private void reset(){
        textPhone.setText("");
        textPassword.setText("");
    }
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        forgetBtn = (Button)findViewById(R.id.forget);
        okBtn = (Button) findViewById(R.id.ok);
        loginView = (RelativeLayout) findViewById(R.id.login_wrap);
        progressBar = (LinearLayout)findViewById(R.id.progressBar);
        textPhone = (EditText)findViewById(R.id.phone);
        textPassword = (EditText)findViewById(R.id.password);
        forgetBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent();
                intent.setClass(self,ForgetActivity.class);
                startActivity(intent);
            }
        });
        //注册事件
        okRegister();
        initHandler();
        //clean
        reset();
    }
    //insert cookie into datebase
    private void saveCookie(String cookie){
        HashMap map = new HashMap();
        //date after one month
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH,1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String expire = dateFormat.format(calendar.getTime());
        map.put("name",cookie);
        map.put("expire",expire);
        Cookie cookie1 = new Cookie(self);
        cookie1.delete();
        cookie1.add(map);
    }

    private void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 100){
                    okBtn.setAlpha(1.0f);
                    okBtn.setEnabled(true);
                    progressBar.setVisibility(View.INVISIBLE);
                    String result = msg.obj.toString();
                    try{
                        JSONTokener jt = new JSONTokener(result);
                        JSONObject job = (JSONObject) jt.nextValue();
                        int errorCode = job.getInt("error_code");
                        if(errorCode == 0){
                            String company = job.getString("company");
                            Session.setData("company",company);
                            //put user object into database
                            Login obj = new Login(self);
                            obj.deleteAll();
                            obj.add(company,phone,password);
                            //put cookie into database
                            saveCookie(Request.clientCoookie);
                            Session.setData("cookie",Request.clientCoookie);
                            //toggle activity
                            Intent intent = new Intent();
                            intent.setClass(self,DashboardActivity.class);
                            startActivity(intent);
                        }else{
                            //user not exists
                            Request.alert(self,errorCode);
                            okBtn.setEnabled(true);
                            okBtn.setAlpha((float)1);
                        }
                    }catch (Exception e){
                        Request.alert(self,9);
                    }
                }
            }
        };
    }

    private void requestData(){
        if(Request.getNetStates(self)){
            progressBar.setVisibility(View.VISIBLE);
            okBtn.setEnabled(false);
            okBtn.setAlpha((float)0.5);
            new Thread(){
                public void run(){
                    HashMap map = new HashMap();
                    map.put("phone",phone);
                    map.put("password",password);
                    Request request = new Request(self);
                    String result = request.post("guest/get",map);
                    Message msg = new Message();
                    msg.obj = result;
                    msg.what = 100;
                    handler.sendMessage(msg);
                }
            }.start();
        }else{
            okBtn.setAlpha(1.0f);
            okBtn.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
            Request.alert(self,5);
        }
    }

    private void okRegister(){
        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean tag = true;
                phone = textPhone.getText().toString().trim();
                password = textPassword.getText().toString().trim();
                Pattern pt = Pattern.compile("(^1[3458][0-9]{9}$)");
                Matcher mt = pt.matcher(phone);
                if(!mt.find()){
                    tag = false;
                    Toast.makeText(LoginActivity.this,"手机号格式不正确",Toast.LENGTH_SHORT).show();
                }
                if(password.length() < 6){
                    tag = false;
                    Toast.makeText(LoginActivity.this,"密码不能小于6个字符",Toast.LENGTH_SHORT).show();
                }
                if(tag){
                    okBtn.setAlpha(0.5f);
                    okBtn.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    requestData();
                }
            }
        });
    }
}
