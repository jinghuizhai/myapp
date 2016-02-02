package com.example.myapp;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import model.Msg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;
import util.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.os.Handler;

/**
 * Created by UI on 2015/12/28.
 */
public class MsgActivity extends Activity implements Requester{
    private HashSet<String> boxSet = new HashSet<String>();
    private Handler handler;
    private MsgActivity self = this;
    private Button deleteBtn;
    private Button selectAllBtn;
    private Button editBtn;
    private ListView listView;
    private TextView noRecord;
    private LinearLayout bottomTool;
    private SimpleAdapter adapter;
    private ArrayList array = new ArrayList();
    private LinearLayout progressBar;
    private int readed = 0;//0 == have not read 1 == have read

    public void requestData(){
        new Thread(){
            @Override
            public void run() {
                HashMap map = new HashMap();
                map.put("readed",readed);
                Request request = new Request(self);
                request.callback(null,progressBar);
                String result = request.post("msg/get",map);
                Message message = new Message();
                message.what = 100;
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
    }

    private void initAdapter(){
        adapter = new SimpleAdapter(self,
                array,
                R.layout.msg_listview,
                new String[]{"title","content","date","msg_id"},
                new int[]{R.id.title,R.id.content,R.id.date,R.id.checkBox}){
            public View getView(int position, View convertView, ViewGroup parent) {
                convertView = LayoutInflater.from(self).inflate(R.layout.msg_listview,null);
                final CheckBox box = (CheckBox) convertView.findViewById(R.id.checkBox);
                box.setOnClickListener(new View.OnClickListener(){
                    public void onClick(View view) {
                        if(box.isChecked()){
                            boxSet.add(box.getText().toString());
                        }else{
                            boxSet.remove(box.getText().toString());
                        }
                        if(boxSet.size() > 0){
                            deleteBtn.setTextColor(getResources().getColor(R.color.blue));
                            deleteBtn.setEnabled(true);
                        }else{
                            deleteBtn.setTextColor(getResources().getColor(R.color.gray));
                            deleteBtn.setEnabled(false);
                        }
                    }
                });
                return super.getView(position,convertView,parent);
            }
        };
    }

    private void showMsg(String result){
        try{
            JSONTokener jsonTokener = new JSONTokener(result);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            int errorCode = jsonObject.getInt("error_code");
            if(errorCode == 0){
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                if(jsonArray.length() > 0){
                    for(int i = 0,length = jsonArray.length();i<length;i++){
                        HashMap map = new HashMap();
                        JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
                        map.put("title",jsonObject1.getString("title"));
                        map.put("content",jsonObject1.getString("content"));
                        map.put("date",jsonObject1.getString("create_date"));
                        map.put("msg_id",jsonObject1.getString("msg_id"));
                        array.add(map);
                    }
                    initAdapter();
                    listView.setAdapter(adapter);
                }else{
                    noRecord.setVisibility(View.VISIBLE);
                }
            }else{
                Request.alert(self,errorCode);
            }
        }catch (JSONException e){
            Log.i("json error",e.toString());
        }catch (ClassCastException e){
            Log.i("class cast error",e.toString());
        }
    }

    private void deleteListview(){
        Iterator iter = array.iterator();
        while(iter.hasNext()){
            HashMap map = (HashMap)iter.next();
            if(boxSet.contains(map.get("msg_id"))){
                iter.remove();
                boxSet.remove(map.get("msg_id"));
            }
        }
        adapter.notifyDataSetChanged();
        editBtn.setText("编辑");
        selectAllBtn.setText("全选");
    }

    private void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progressBar.setVisibility(View.INVISIBLE);
                switch (msg.what){
                    case 100:
                        progressBar.setVisibility(View.INVISIBLE);
                        showMsg(msg.obj.toString());
                        break;
                    case 101:
                        bottomTool.setVisibility(View.INVISIBLE);
                        break;
                    case 102:
                        deleteListview();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void historyRegister(){
        noRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                noRecord.setVisibility(View.INVISIBLE);
                readed = 1;
                progressBar.setVisibility(View.VISIBLE);
                requestData();
            }
        });
    }

    private void editRegister(){
        editBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                editBtn.setEnabled(false);
                ListView lv = (ListView) findViewById(R.id.listview);
                int i = 0,length = lv.getCount();
                if(length > 0) {
                    if(editBtn.getText().equals("编辑")){
                        for (; i < length; i++) {
                            LinearLayout con = (LinearLayout) lv.getChildAt(i);
                            CheckBox box = (CheckBox) con.findViewById(R.id.checkBox);
                            box.setVisibility(View.VISIBLE);
                        }
                        bottomTool.setVisibility(View.VISIBLE);
                        editBtn.setText("取消");
                    }else{
                        for (; i < length; i++) {
                            LinearLayout con = (LinearLayout) lv.getChildAt(i);
                            CheckBox box = (CheckBox) con.findViewById(R.id.checkBox);
                            box.setVisibility(View.INVISIBLE);
                        }
                        bottomTool.setVisibility(View.INVISIBLE);
                        editBtn.setText("编辑");
                    }
                }else{
                    Util.toast(self,"没有可编辑的记录");
                }
                editBtn.setEnabled(true);
            }
        });
    }
    private void selectRegister(){
        selectAllBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                ListView lv = (ListView) findViewById(R.id.listview);
                int i = 0,length = lv.getCount();
                if(length > 0){
                    if(selectAllBtn.getText().toString().equals("全选")){
                        selectAllBtn.setText("取消全选");
                        deleteBtn.setEnabled(true);
                        deleteBtn.setTextColor(getResources().getColor(R.color.blue));
                        for(;i < length;i++){
                            LinearLayout con = (LinearLayout) lv.getChildAt(i);
                            CheckBox box = (CheckBox)con.findViewById(R.id.checkBox);
                            boxSet.add(box.getText().toString());
                            box.setChecked(true);
                        }
                    }else{
                        selectAllBtn.setText("全选");
                        deleteBtn.setEnabled(false);
                        deleteBtn.setTextColor(getResources().getColor(R.color.gray));
                        for(;i < length;i++){
                            LinearLayout con = (LinearLayout) lv.getChildAt(i);
                            CheckBox box = (CheckBox)con.findViewById(R.id.checkBox);
                            box.setChecked(false);
                        }
                        boxSet.clear();
                    }
                }
            }
        });
    }
    private void deleteRegister(){
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                new Thread(){
                    @Override
                    public void run() {
                        HashMap map = new HashMap();
                        map.put("ids",boxSet);
                        //发送隐藏bottomTool信息
                        Message message = new Message();
                        message.what = 101;
                        handler.sendMessage(message);
                        Request request = new Request(self);
                        request.callback(null,progressBar);
                        String result = request.post("msg/delete",map);
                        Message message1 = new Message();
                        message1.what = 102;
                        message1.obj = result;
                        handler.sendMessage(message1);
                    }
                }.start();
            }
        });
    }
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.msg);
        selectAllBtn = (Button)findViewById(R.id.selectAll);
        deleteBtn = (Button)findViewById(R.id.delete);
        editBtn = (Button)findViewById(R.id.edit);
        listView = (ListView)findViewById(R.id.listview);
        noRecord = (TextView)findViewById(R.id.no_record);
        bottomTool = (LinearLayout)findViewById(R.id.bottom_tool);
        progressBar = (LinearLayout)findViewById(R.id.progressBar);

        QuickToggle.back(this,(TextView)findViewById(R.id.back));
        initHandler();
        readed = 0;
        requestData();

        //注册按钮点击事件
        deleteRegister();
        editRegister();
        selectRegister();
        historyRegister();
    }

    public void onStart(){
        super.onStart();
    }
}
