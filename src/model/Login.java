package model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UI on 2015/12/25.
 */
public class Login extends Model{
    public Login(Context context){
        super(context);
    }
    public long add(String company,String phone,String password){
        HashMap params = new HashMap();
        params.put("phone",phone);
        params.put("password",password);
        params.put("company",company);
        return insert("user",params);
    }
    public ArrayList login(String phone,String password){
        return query("select * from user where phone=? and password=?",new String[]{phone,password});
    }
    public int updatePassword(String phone,String password){
        HashMap params = new HashMap();
        params.put("password",password);
        return update("user",params,"phone=?",new String[]{phone});
    }
    public int delete(String phone){
        return delete("user","phone=?",phone);
    }
    public int deleteAll(){
        return super.delete("user");
    }
    public ArrayList findOne(){
        ArrayList list = query("select * from user limit ?",String.valueOf(1));
        return list;
    }
}
