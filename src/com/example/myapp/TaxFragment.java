package com.example.myapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by UI on 2015/12/28.
 */
public class TaxFragment extends RichFragment{
    private GestureDetector gestureDetector;
    private ProgressbarGestureListener progressbarGestureListener;
    private Handler handler;
    private int[] currentDate;
    private TaxFragment self;
    private View view;
    private LinearLayout countWrap;
    private TextView nation;
    private TextView local;
    private TextView noDataView;

    private int[] getCurrentDate(){
        if(currentDate == null){
            Calendar cl = Calendar.getInstance();
            int year = cl.get(Calendar.YEAR);
            int month = cl.get(Calendar.MONTH)+1;
            currentDate = new int[]{year,month};
        }
        return currentDate;
    }

    private void setYear(int year){
        currentDate[0] = year;
    }

    private void setMonth(int month){
        currentDate[1] = month;
    }

    private void setCurrentDate(int[] currentDate){
        this.currentDate = currentDate;
    }

    private void initSpinner(Spinner sp,String value){
        int length = sp.getCount(),i = 0;
        for(;i < length;i++){
            String item = sp.getItemAtPosition(i).toString();
            if(item.equals(value)){
                sp.setSelection(i);
                break;
            }
        }
    }

    private void initHandler(){
        handler = new Handler() {
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
                            JSONArray ja = job.getJSONArray("data");
                            ArrayList array = new ArrayList();
                            for(int i = 0;i<ja.length();i++){
                                JSONObject jo = (JSONObject)ja.get(i);
                                String name = jo.getString("name");
                                String fee = jo.getString("fee");
                                HashMap map = new HashMap();
                                map.put("title",name);
                                map.put("content",fee);
                                array.add(map);
                            }
                            SimpleAdapter adapter = new SimpleAdapter(self.getActivity(),
                                    array,
                                    R.layout.tax_listview,
                                    new String[]{"title","content"},
                                    new int[]{R.id.title,R.id.content});
                            ListView lv = (ListView)view.findViewById(R.id.listview);
                            lv.setAdapter(adapter);
                            String nationResult = job.getString("nation");
                            String localResult = job.getString("local");
                            if(nationResult.equals("0") && localResult.equals("0")){
                                countWrap.setVisibility(View.INVISIBLE);
                            }else{
                                nation.setText("国税："+nationResult);
                                local.setText("地税："+localResult);
                                countWrap.setVisibility(View.VISIBLE);
                            }
                            if(nationResult.equals("0") && localResult.equals("0") && adapter.getCount() == 0){
                                noDataView.setVisibility(View.VISIBLE);
                            }else{
                                noDataView.setVisibility(View.INVISIBLE);
                            }
                        }else{
                            Request.alert(self.getActivity(),errorCode);
                        }
                    }catch (Exception e){
                        Log.e("tax request error:",e.toString());
                    }
                }
            }
        };
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        self = this;
        view = inflater.inflate(R.layout.tax, null);
        nation = (TextView)view.findViewById(R.id.nation);
        local = (TextView)view.findViewById(R.id.local);
        countWrap = (LinearLayout)view.findViewById(R.id.count_wrap);
        noDataView = (TextView)view.findViewById(R.id.no_data);
        LinearLayout progressBar = (LinearLayout)view.findViewById(R.id.progressBar);
        final Spinner yearSpinner = (Spinner)view.findViewById(R.id.year_spinner);
        final Spinner monthSpinner = (Spinner)view.findViewById(R.id.month_spinner);

        initSpinner(yearSpinner,String.valueOf(getCurrentDate()[0]));
        initSpinner(monthSpinner,String.valueOf(getCurrentDate()[1]));

        progressbarGestureListener = new ProgressbarGestureListener();
        progressbarGestureListener.setView(progressBar);
        progressbarGestureListener.setFragment(self);
        progressbarGestureListener.show();
        gestureDetector = new GestureDetector(this.getActivity(),progressbarGestureListener);

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressbarGestureListener.show();
                String item = adapterView.getItemAtPosition(i).toString();
                setYear(Integer.valueOf(item));
                requestData();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                progressbarGestureListener.show();
                String item = adapterView.getItemAtPosition(i).toString();
                setMonth(Integer.valueOf(item));
                requestData();
            }
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        initHandler();
        return view;
    }
    public String initContent(){
        return null;
    };

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
                HashMap map = new HashMap();
                map.put("year",getCurrentDate()[0]);
                map.put("month",getCurrentDate()[1]);
                Request request = new Request(self.getActivity());
                request.callback(progressbarGestureListener,null);
                String result = request.post("tax/get",map);
                Message msg = new Message();
                msg.what = 100;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }
}
