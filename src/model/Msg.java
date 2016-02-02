package model;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UI on 2016/1/14.
 */
public class Msg extends Model {
    public Msg(Context context){
        super(context);
    }
    public long add(HashMap map){
       return insert("msg",map);
    }
    public ArrayList find(int readed){
        return query("select * from `msg` where readed=?",
                new String[]{String.valueOf(readed)});
    }
    public int update(){
        HashMap map = new HashMap();
        map.put("readed",1);
        return update("msg",map,"'1'=?",new String[]{"1"});
    }
    public ArrayList max(){
        return query("select max(msg_id) max from `msg` limit ?",new String[]{String.valueOf(1)});
    }
    public long deleteById(String msgId){
        return delete("msg","msg_id=?",new String[]{msgId});
    }

}
