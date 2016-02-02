package model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UI on 2016/2/2.
 */
public class Cookie extends Model {
    public Cookie(Context context){
        super(context);
    }
    public long delete(){
        return super.delete("cookie");
    }
    public long add(HashMap map){
        return insert("cookie",map);
    }
    public ArrayList find(){
        return query("select name,expire from `cookie` limit ?",new String[]{"1"});
    }
}
