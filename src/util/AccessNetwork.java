package util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by UI on 2015/12/24.
 */
public class AccessNetwork implements Runnable{
    private String op ;
    private String url;
    private HashMap params;
    private Handler h;

    public AccessNetwork(String op, String url, HashMap params, Handler h) {
        super();
        this.op = op;
        this.url = url;
        this.params = params;
        this.h = h;
    }
    public void run() {
        Message m = new Message();
//        m.what = 0x123;
//        if(op.equals("GET")){
//            m.obj = Request.post(url, params);
//        }
//        if(op.equals("POST")){
//            m.obj = Request.post(url, params);
//        }
        h.sendMessage(m);
    }

}
