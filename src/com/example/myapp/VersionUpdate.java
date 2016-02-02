package com.example.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import util.Request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by UI on 2016/2/1.
 */
public class VersionUpdate {
    private Activity activity;
    private double version;
    private String url;
    private Handler handler;
    private String description;
    private double currentVersion;
    private ProgressDialog pBar;

    public VersionUpdate(Activity activity){
        this.activity = activity;
        getCurrentVersion();
    }

    private void getCurrentVersion(){
        try {
            PackageManager pm = activity.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(activity.getPackageName(), 0);
            String name = pi.versionName;
            currentVersion = Double.valueOf(name);
            Session.setData("version",name);
        } catch (Exception e) {
            Log.e("get version errror:",e.toString());
        }
    }

    private void initHandler(){
        handler = new Handler(activity.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                showUpdateDialog();
            }
        };
    }

    public void checkVersion(){
        new Thread(){
            @Override
            public void run() {
                Request request = new Request(activity);
                String result = request.post("version/get");
                if(!request.equals("") && !request.equals(Request.ERROR)) {
                    JSONTokener jsonTokener = new JSONTokener(result);
                    try {
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        version = jsonObject.getDouble("version");
                        url = jsonObject.getString("url");
                        description = jsonObject.getString("description");

                        if (currentVersion != 0.0 && version > currentVersion) {
                            initHandler();
                            Message message = new Message();
                            handler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        Log.e("json error:", e.toString());
                    } catch (ClassCastException e) {
                        Log.e("class error", toString());
                    }
                }
            }
        }.start();
    }

    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setIcon(R.drawable.ic_launcher_35);
        builder.setTitle("请升级APP至版本:" + version);
        builder.setMessage(description);
        builder.setCancelable(false);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile();
                } else {
                    Toast.makeText(activity,"SD卡不可用，请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.create().show();
    }

    private void requestFile(){
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
                    pBar.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "Test.apk");
                        fileOutputStream = new FileOutputStream(file);
                        byte[] buf = new byte[1000];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process);
                        }
                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    done();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }
    private void downFile() {
        pBar = new ProgressDialog(activity);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.setMessage("请稍候...");
        pBar.setProgress(0);
        pBar.show();
        requestFile();
    }

    private void done() {
        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }
    //安装文件，一般固定写法
    private void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "Test.apk")),
                "application/vnd.android.package-archive");
        activity.startActivity(intent);
    }
}
