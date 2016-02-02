package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import model.Login;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;
import util.Request;

import java.util.HashMap;

/**
 * Created by UI on 2015/12/28.
 */
public class EditPassActivity extends Activity {
    private EditPassActivity self = this;
    private Button back;
    private EditText oldPass;
    private EditText newPass;
    private EditText repeatPass;
    private Button ok;
    private Handler handler;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pass);
        oldPass     = (EditText) findViewById(R.id.old_pass);
        newPass     = (EditText) findViewById(R.id.new_pass);
        repeatPass  = (EditText) findViewById(R.id.repeat_pass);
        back        = (Button)findViewById(R.id.back);
        ok          = (Button)findViewById(R.id.ok);
        //注册事件
        okRegister();
        QuickToggle.back(this,back);
        initHandler();
    }

    private void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 100){
                    ok.setAlpha(1F);
                    ok.setEnabled(true);
                }
            }
        };
    }

    private void toLogin(){
        Intent intent = new Intent();
        intent.setClass(self, LoginActivity.class);
        startActivity(intent);
        self.finish();
    }

    private void requestData(final HashMap params){
        new Thread(){
            @Override
            public void run() {
                Request request = new Request(self);
                String result = request.post("guest/put",params);
                try{
                    JSONTokener jsonTokener = new JSONTokener(result);
                    JSONObject jsonObject   = (JSONObject)jsonTokener.nextValue();
                    int errorCode           = jsonObject.getInt("error_code");
                    if(errorCode == 0){
                        Util.toast(self,"修改密码成功");
                        toLogin();
                    }else{
                        Request.alert(self,errorCode);
                    }
                }catch (JSONException e){
                    Log.e("json error",e.toString());
                }
            }
        }.start();
    }

    private void okRegister(){
        ok.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String oldpass      = oldPass.getText().toString();
                String newpass      = newPass.getText().toString();
                String repeatpass   = repeatPass.getText().toString();
                if(oldpass.length() < 6){
                    Util.toast(self,"原始密码格式不正确");
                }else{
                    if(newpass.length() < 6){
                        Util.toast(self,"新密码格式不正确");
                    }else if(!newpass.equals(repeatpass)){
                        Util.toast(self,"两次输入新密码不相同");
                    }else{
                        ok.setAlpha(0.5F);
                        ok.setEnabled(false);
                        HashMap map = new HashMap();
                        map.put("old_pass",oldpass);
                        map.put("new_pass",newpass);
                        requestData(map);
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
