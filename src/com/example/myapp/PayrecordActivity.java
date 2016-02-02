package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UI on 2016/1/26.
 */
public class PayrecordActivity extends Activity implements Requester{
    private Handler handler;
    private PayrecordActivity self = this;
    private ListView listView;
    private LinearLayout progressBar;
    private TextView noRecord;

    private void initHandler(){
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 100){
                    progressBar.setVisibility(View.INVISIBLE);
                    String result = msg.obj.toString();
                    try{
                        JSONTokener jsonTokener = new JSONTokener(result);
                        JSONObject jsonObject = (JSONObject)jsonTokener.nextValue();
                        int errorCode = jsonObject.getInt("error_code");
                        if(errorCode == 0){
                            JSONArray data = jsonObject.getJSONArray("data");
                            ArrayList array = new ArrayList();
                            for(int i = 0,length = data.length();i<length;i++){
                                JSONObject job = (JSONObject) data.get(i);
                                HashMap map = new HashMap();
                                map.put("index",i+1);
                                map.put("money",job.getString("money")+"￥");
                                map.put("date","服务至:"+job.getString("deadline"));
                                array.add(map);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(self,
                                    array,
                                    R.layout.payrecord_listview,
                                    new String[]{"index","money","date"},
                                    new int[]{R.id.index,R.id.money,R.id.date});
                            listView.setAdapter(adapter);
                            if(array.size() == 0){
                                noRecord.setVisibility(View.VISIBLE);
                            }
                        }else{
                            Request.alert(self,errorCode);
                        }
                    }catch (JSONException e){
                        Log.e("json error",e.toString());
                    }
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payrecord);
        QuickToggle.back(this,(TextView)findViewById(R.id.back));
        listView = (ListView)findViewById(R.id.listview);
        progressBar = (LinearLayout)findViewById(R.id.progressBar);
        noRecord = (TextView)findViewById(R.id.no_record);
        initHandler();
        requestData();
    }

    public void requestData() {
        new Thread(){
            @Override
            public void run() {
                Request request = new Request(self);
                request.callback(null,progressBar);
                String result = request.post("payrecord/get");
                Message message = new Message();
                message.what = 100;
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
    }
}
