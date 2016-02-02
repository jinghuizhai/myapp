package com.example.myapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;
/**
 * Created by UI on 2015/12/26.
 */
public class AccountingFragment extends RichFragment{
    private Handler handler;
    private AccountingFragment self;
    private GestureDetector gestureDetector;
    private ProgressbarGestureListener progressbarGestureListener;
    private LinearLayout deadlineWrap;
    private TextView noRecord;
    private TextView diff;
    private TextView employee;
    private TextView phone;
    private Button moreRecord;
    private LinearLayout employeeWrap;

    private void initHandler(){
        progressbarGestureListener.hide();
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                progressbarGestureListener.hide();
                if(msg.what == 100){
                    String result = msg.obj.toString();
                    try{
                        JSONTokener jt = new JSONTokener(result);
                        JSONObject job = (JSONObject) jt.nextValue();
                        int errorCode = job.getInt("error_code");
                        if(errorCode == 0){
                            JSONObject data = job.getJSONObject("data");
                            if(data.has("name")) {
                                String name = data.getString("name");
                                String phone = data.getString("phone");
                                self.employee.setText(name);
                                self.phone.setText(phone);
                                if (data.has("deadline")) {
                                    String deadline = data.getString("deadline");
                                    String money = data.getString("money");
                                    int diff = data.getInt("diff");
                                    moreRecord.setVisibility(View.VISIBLE);
                                    self.diff.setText(String.valueOf(diff));
                                    deadlineWrap.setVisibility(View.VISIBLE);
                                    noRecord.setVisibility(View.INVISIBLE);
                                } else {
                                    noRecord.setVisibility(View.VISIBLE);
                                    deadlineWrap.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                employeeWrap.setVisibility(View.INVISIBLE);
                            }
                        }else{
                            Request.alert(self.getActivity(),errorCode);
                        }
                    }catch (Exception e){
                        Log.i("error",e.toString());
                    }
                }
            }
        };
    }

    //�л���֧����¼
    private void toPayRecord(){
        Intent intent = new Intent();
        intent.setClass(this.getActivity(),PayrecordActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        self = this;
        View view = inflater.inflate(R.layout.accounting, null);
        LinearLayout progressBar = (LinearLayout)view.findViewById(R.id.progressBar);
        deadlineWrap = (LinearLayout) view.findViewById(R.id.deadline_wrap);
        noRecord = (TextView)view.findViewById(R.id.no_record);
        diff = (TextView)view.findViewById(R.id.diff);
        employee = (TextView)view.findViewById(R.id.employee);
        phone = (TextView)view.findViewById(R.id.phone);
        moreRecord = (Button)view.findViewById(R.id.more_record);
        employeeWrap = (LinearLayout)view.findViewById(R.id.employee_wrap);
        progressbarGestureListener = new ProgressbarGestureListener();
        progressbarGestureListener.setView(progressBar);
        progressbarGestureListener.setFragment(self);
        progressbarGestureListener.show();
        gestureDetector = new GestureDetector(this.getActivity(),progressbarGestureListener);
        initHandler();
        progressbarGestureListener.show();
        requestData();
        moreRecord.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                toPayRecord();
            }
        });
        return view;
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        progressbarGestureListener.onTouch(event);
    }

    @Override
    public void requestData() {
        new Thread(){
            @Override
            public void run() {
                Request request = new Request(self.getActivity());
                request.callback(progressbarGestureListener,null);
                String result = request.post("accounting/get");
                Message message = new Message();
                message.what = 100;
                message.obj = result;
                handler.sendMessage(message);
            }
        }.start();
    }
}
