package util;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by UI on 2015/12/24.
 */
class T implements Runnable{
    private String name;
    T(String name){
        this.name = name;
    }
    public void run(){
        int i = 0;
        for(;i<10;i++){
            System.out.println(name+","+i);
        }
    }
}
public class Rtest{
    public static void main(String args[]){
//        final Handler h = new Handler(){
//            @Override
//            public void handleMessage(Message msg) {
//                System.out.print(msg.obj.toString());
//            }
//        };
//        AccessNetwork ant = new AccessNetwork("POST","http://192.168.10.105/accountant/customer/v1/business/get","",h);
//        new Thread(ant).start();
    }

}
