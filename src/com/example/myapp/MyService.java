package com.example.myapp;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import model.Msg;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.jar.JarOutputStream;

/**
 * Created by UI on 2015/12/31.
 */
public class MyService extends Service{
    private MyService self = this;
    /** 标识服务如果被杀死之后的行为 */
    private int mStartMode;

    /** 绑定的客户端接口 */
    private IBinder mBinder;

    /** 标识是否可以使用onRebind */
    private boolean mAllowRebind;

    /** 当服务被创建时调用. */
    @Override
    public void onCreate() {
        final Context context = this.getApplicationContext();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Request request = new Request(context);
                String result = request.post("msgcount/get");
                try{
                    JSONTokener jt = new JSONTokener(result);
                    JSONObject job = (JSONObject) jt.nextValue();
                    int errorCode = job.getInt("error_code");
                    if(errorCode == 0){
                        int count = job.getInt("data");
                        if(count > 0){
                            Session.setData("msg",String.valueOf(count));
                            notice();
                        }
                    }
                }catch (JSONException e){
                    Log.e("json error",e.toString());
                }
            }
        },0,1000*60*30);
    }
    private void notice(){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        int icon = R.drawable.ic_launcher;
        CharSequence tickerText = "米蝶财务";
        long when = System.currentTimeMillis();
        Notification notification = new Notification(icon, tickerText, when);
        Context context = getApplicationContext();
        CharSequence contentTitle = "米蝶财务";
        CharSequence contentText = "你的服务有新进度，点击查看";
        Intent notificationIntent = new Intent(this, DashboardActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        notification.setLatestEventInfo(context, contentTitle, contentText,
                contentIntent);
        mNotificationManager.notify(1, notification);
    }
    /** 调用startService()启动服务时回调 */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }

    /** 通过bindService()绑定到服务的客户端 */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** 通过unbindService()解除所有客户端绑定时调用 */
    @Override
    public boolean onUnbind(Intent intent) {
        return mAllowRebind;
    }

    /** 通过bindService()将客户端绑定到服务时调用*/
    @Override
    public void onRebind(Intent intent) {

    }

    /** 服务不再有用且将要被销毁时调用 */
    @Override
    public void onDestroy() {

    }
}
