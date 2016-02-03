package com.example.myapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.util.ArrayList;
import android.os.Handler;
import util.Request;

/**
 * Created by UI on 2015/12/26.
 */
public class BusinessFragment extends RichFragment{
    private GestureDetector gestureDetector;
    private ProgressbarGestureListener progressbarGestureListener;
    private View view;
    private Handler handler;
    private BusinessFragment self;
    private TextView employee;
    private TextView phone;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        self = this;
        view = inflater.inflate(R.layout.business, null);
        initHandler();
        employee = (TextView)view.findViewById(R.id.employee);
        phone = (TextView)view.findViewById(R.id.phone);
        phoneRegister();
        LinearLayout progressBar = (LinearLayout)view.findViewById(R.id.progressBar);
        progressbarGestureListener = new ProgressbarGestureListener();
        progressbarGestureListener.setView(progressBar);
        progressbarGestureListener.setFragment(self);
        progressbarGestureListener.show();
        gestureDetector = new GestureDetector(this.getActivity(),progressbarGestureListener);
        requestData();
        return view;
    }
    private void phoneRegister(){
        phone.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                String phoneNum = phone.getText().toString();
                if(Util.validate("phone",phoneNum)){
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+phoneNum));
                    self.startActivity(intent);
                }
            }
        });
    }
    public void onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        progressbarGestureListener.onTouch(event);
    }

    private int getWidth(){
        DisplayMetrics dm = new DisplayMetrics();
        this.getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void initHandler(){
        handler = new Handler(self.getActivity().getMainLooper()) {
            public void handleMessage(Message msg) {
                progressbarGestureListener.hide();
                if(msg.what == 101){
                    Bundle data = msg.getData();
                    int layoutId              = data.getInt("layoutId");
                    final int businessId      = data.getInt("businessId");
                    int percent               = data.getInt("percent");
                    final String businessName = data.getString("businessName");
                    LinearLayout businessRegister = (LinearLayout) view.findViewById(layoutId);
                    businessRegister.setVisibility(View.VISIBLE);
                    HorizontalProgressBarView hp = new HorizontalProgressBarView(self.getActivity(),null);
                    hp.setMwidth(self.getWidth()-70);
                    hp.setProgress(percent);
                    businessRegister.addView(hp);
                    businessRegister.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra("business_id",businessId);
                            intent.putExtra("tag",businessName);
                            intent.setClass(BusinessFragment.this.getActivity(),BusinessDetailActivity.class);
                            startActivity(intent);
                        }
                    });
                }else if(msg.what == 102){
                    Util.toast(BusinessFragment.this.getActivity(),msg.obj.toString());
                }else if(msg.what == 103){
                    Bundle bundle = msg.getData();
                    String phoneStr = bundle.getString("phone");
                    String nameStr = bundle.getString("name");
                    employee.setText(nameStr);
                    phone.setText(phoneStr);
                }
            }
        };
    }
    public void requestData(){
        new Thread(){
            public void run() {
                Request request = new Request(self.getActivity());
                request.callback(progressbarGestureListener, null);
                String result = request.post("business/get");
                if (!result.equals("") && !request.equals(Request.ERROR)) {
                    JSONTokener jsonParser = new JSONTokener(result);
                    try {
                        JSONObject job = (JSONObject) jsonParser.nextValue();
                        int errorCode = job.getInt("error_code");
                        if (errorCode == 0) {
                            JSONArray ja = job.getJSONArray("data");
                            JSONObject employeeObject = job.getJSONObject("employee");
                            //set employee.name and .phone
                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putString("name",employeeObject.getString("name"));
                            bundle.putString("phone",employeeObject.getString("phone"));
                            message.setData(bundle);
                            message.what = 103;
                            handler.sendMessage(message);

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jj = (JSONObject) ja.get(i);
                                String businessName = jj.getString("business_name");
                                int percent = jj.getInt("percent");
                                int businessId = jj.getInt("business_id");
                                int layoutId = 0;
                                if (businessName.equals("register")) {
                                    layoutId = R.id.business_register;
                                } else if (businessName.equals("change")) {
                                    layoutId = R.id.business_change;
                                } else if (businessName.equals("logout")) {
                                    layoutId = R.id.business_logout;
                                }
                                if (percent == 0) {
                                    percent = 1;
                                }
                                if (layoutId != 0) {
                                    Message msg = new Message();
                                    msg.what = 101;
                                    Bundle data = new Bundle();
                                    data.putString("businessName", businessName);
                                    data.putInt("percent", percent);
                                    data.putInt("businessId", businessId);
                                    data.putInt("layoutId", layoutId);
                                    msg.setData(data);
                                    handler.sendMessage(msg);
                                }
                            }
                        } else {
                            Request.alert(self.getActivity(),errorCode);
                        }
                    } catch (JSONException e) {
                        Log.e("json error", e.toString());
                    }catch (ClassCastException e){
                        Log.e("class case error", e.toString());
                    }
                }
            }
        }.start();
    }
}
