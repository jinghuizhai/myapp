package util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import com.example.myapp.LoginActivity;
import com.example.myapp.ProgressbarGestureListener;
import com.example.myapp.Session;
import com.example.myapp.Util;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by UI on 2015/12/23.
 */

public class Request{
    public static HashMap toastMap = new HashMap(){{
        put("1","请先登录");
        put("2","没有此手机号码");
        put("3","手机号码不符合要求");
        put("4","验证码发送失败");
        put("5","网络状态差，请稍候再试");
        put("6","请先获取验证码");
        put("7","验证码不正确");
        put("8","账号或密码错误");
        put("9","服务器出现错误，请重试");
        put("10","请先选定项目");
        put("11","密码不符合要求");
        put("12","修改密码失败");
        put("13","更新密码失败，请重新获取验证码");
    }};
    public static String clientCoookie = null;
    public static String baseUrl = "http://192.168.10.105/accountant/customer/v1/";
    public static String ERROR = "ERROR";
    private Context context;
    private long timeout = 1000*10;
    private Timer callbackTimer;
    private Handler handler;
    private HttpClient httpClient;
    private ProgressbarGestureListener progressbarGestureListener;
    private LinearLayout progressBar;

    public Request(Context context){
        this.context = context;
        handler = new Handler(context.getMainLooper());
    }

    private void hide(){
        handler.post(new Runnable() {
            public void run() {
                if(progressbarGestureListener != null){
                    progressbarGestureListener.hide();
                }
                if(progressBar != null){
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    public void callback(ProgressbarGestureListener progressbarGestureListener, LinearLayout progressBar){
        this.progressbarGestureListener = progressbarGestureListener;
        this.progressBar = progressBar;
        TimerTask timerTask = new TimerTask() {
            public void run() {
                hide();
                if(httpClient != null){
                    httpClient.getConnectionManager().shutdown();
                    Util.toast(context,"连接超时，请重试");
                }
            }
        };
        callbackTimer = new Timer();
        callbackTimer.schedule(timerTask,timeout);
    }

    public String post(String url){
        return post(url,null);
    }

    public String post(String url,HashMap map){
        String ret = "";
        if(!Request.getNetStates(context)){
            Util.toast(context,"网络状态差,请稍后再试");
            ret = Request.ERROR;
            if(callbackTimer != null){
                callbackTimer.cancel();
            }
            hide();
        }else{
            if(map == null){
                map = new HashMap();
            }
            List params = parseParams(map);
            url = Request.baseUrl+url;
            try{
                HttpEntity requestHttpEntity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(requestHttpEntity);
                if(!Session.getData("cookie").equals("")){
                    httpPost.setHeader("Cookie","PHPSESSID="+Session.getData("cookie"));
                }
                httpClient = new DefaultHttpClient();
                HttpResponse response = httpClient.execute(httpPost);
                if(response != null){
                    Header[] headers = response.getHeaders("Set-Cookie");
                    if (headers != null){
                        for(int i=0;i < headers.length;i++){
                            String cookie = headers[i].getValue();
                            Pattern pt = Pattern.compile("PHPSESSID=([^;]+);");
                            Matcher mt = pt.matcher(cookie);
                            if(mt.find()){
                                String cook = mt.group(1);
                                if(Request.clientCoookie == null){
                                    Request.clientCoookie = cook;
                                }
                            }
                        }
                    }
                    //handle content
                    HttpEntity httpEntity = response.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            inputStream));
                    String result = "";
                    String line = "";
                    while (null != (line = reader.readLine()))
                    {
                        result += line;
                    }
                    ret = result;
                }
            }catch (IOException e){
                Log.e("post request",e.toString());
            }finally {
                if(callbackTimer != null){
                    callbackTimer.cancel();
                }
            }
        }
        return ret;
    }

    public void setTimeout(long timeout){
        this.timeout = timeout;
    }

    public static boolean getNetStates(Context ac){
        ConnectivityManager cm = (ConnectivityManager) ac.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    public List parseParams(HashMap map){
        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        Iterator iter = map.entrySet().iterator();
        while(iter.hasNext()){
            Map.Entry entry = (Map.Entry)iter.next();
            NameValuePair pair = new BasicNameValuePair(entry.getKey().toString(), entry.getValue().toString());
            pairList.add(pair);
        }
        return pairList;
    }

    public static void alert(Activity context, int key){
        //重新登录
        if(key == 1){
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            context.startActivity(intent);
            Util.toast(context,"为保证账号安全，需要定期登录");
            context.finish();
        }else{
            Util.toast(context,Request.getToast(key));
        }
    }

    private static String getToast(String key){
        Object value = toastMap.get(key);
        if(value == null){
            return "未定义";
        }else{
            return value.toString();
        }
    }
    private static String getToast(int key){
        return getToast(String.valueOf(key));
    }
}
