package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;

import java.util.ArrayList;
import java.util.HashMap;
import android.os.Handler;

/**
 * Created by UI on 2016/1/6.
 */
public class BusinessDetailActivity extends Activity {
    private Button backBtn;
    private TextView title;
    private RelativeLayout wrap;
    private Handler handler;
    private BusinessDetailActivity self = this;
    private ArrayList array = new ArrayList();
    private SimpleAdapter adapter;
    private int businessId;
    private LinearLayout progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.business_detail);
        backBtn = (Button)findViewById(R.id.back);
        title = (TextView)findViewById(R.id.title);
        wrap = (RelativeLayout)findViewById(R.id.wrap);
        progressBar = (LinearLayout)findViewById(R.id.progressBar);
        QuickToggle.back(this,backBtn);
        Bundle bu = this.getIntent().getExtras();
        businessId = bu.getInt("business_id");
        String tag = bu.getString("tag");
        if(tag.equals("register")){
            title.setText("工商注册");
        }else if(tag.equals("change")){
            title.setText("工商变更");
        }else if(tag.equals("logout")){
            title.setText("工商注销");
        }
        initHandler();
        requestData();
    }

    private void initAdapter(){
        adapter = new SimpleAdapter(self,
                array,
                R.layout.business_listview,
                new String[]{"title","content","date"},
                new int[]{R.id.title,R.id.content,R.id.date}){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView =   LayoutInflater.from(self).inflate(R.layout.business_listview,null);
                TextView tv = (TextView) convertView.findViewById(R.id.title);
                HashMap map = (HashMap)this.getItem(position);
                String date = map.get("date").toString();
                if(date.equals("")){
                    tv.setBackground(getResources().getDrawable(R.drawable.circle_bg_gray));
                }
                return super.getView(position,convertView,parent);
            }

        };
    }

    private void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 100){
                    progressBar.setVisibility(View.INVISIBLE);
                    try{
                        JSONTokener jt = new JSONTokener(msg.obj.toString());
                        JSONObject job = (JSONObject)jt.nextValue();
                        int errorCode = job.getInt("error_code");
                        if(errorCode == 0){
                            JSONArray ja = job.getJSONArray("data");
                            for(int i = 0;i<ja.length();i++){
                                JSONObject jo = (JSONObject)ja.get(i);
                                HashMap map = new HashMap();
                                map.put("title",jo.getString("name"));
                                map.put("content",jo.getString("note"));
                                map.put("date",jo.getString("date_end"));
                                array.add(map);
                            }
                            initAdapter();
                            ListView lv = (ListView)findViewById(R.id.listview);
                            lv.setAdapter(adapter);
                        }else{
                            Request.alert(self,errorCode);
                        }
                    }catch (Exception e){
                        Log.i("--","json error");
                    }
                }
            }
        };
    }

    public void requestData(){
        new Thread(){
            @Override
            public void run() {
                HashMap params = new HashMap();
                params.put("business_id",businessId);
                Request request = new Request(self);
                request.callback(null,progressBar);
                String result = request.post("progress/get",params);
                Message msg = new Message();
                msg.obj = result;
                msg.what = 100;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
