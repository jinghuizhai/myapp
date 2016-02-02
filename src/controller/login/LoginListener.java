package controller.login;

import android.content.Context;
import android.util.Log;
import util.GestureListener;

/**
 * Created by UI on 2015/12/24.
 */
public class LoginListener extends GestureListener{
    public LoginListener(Context context){
        super(context);
    }
    public boolean left(){
        Log.i("red:","left");
        return false;
    }
    public boolean right(){
        Log.i("red:","right");
        return false;
    }

}
