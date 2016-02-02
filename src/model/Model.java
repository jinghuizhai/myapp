package model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by UI on 2015/12/26.
 */
public class Model {

    static SQLiteDatabase db = null;

    public Model(Context context){
        if(db == null){
            DatabaseHelper helper = new DatabaseHelper(context);
            db = helper.getWritableDatabase();
        }
    }
    public long insert(String tableName, HashMap params){
        try {
            ContentValues cv = new ContentValues();
            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                cv.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return db.insert(tableName,null,cv);
        }catch (Exception e){
            Log.e(e.toString(),"insert error");
            return 0;
        }
    }
    public int delete(String tableName,String clause,String[] args){
        try{
            return db.delete(tableName,clause,args);
        }catch (Exception e){
            Log.e(e.toString(),"delete error");
            return 0;
        }
    }
    public int delete(String tableName){
        try{
            String sql = "DELETE FROM "+tableName.toUpperCase();
            db.execSQL(sql);
            return 1;
        }catch (Exception e){
            Log.i(e.toString(),"delete all error");
            return 0;
        }
    }
    public int delete(String tableName,String clause,String param){
        try{
            return this.delete(tableName,clause,new String[]{param});
        }catch(SQLiteException e){
            Log.e(e.toString(),"delete error");
            return 0;
        }
    }
    public int update(String tableName,HashMap params,String whereClause,String[] whereArgs){
        try {
            ContentValues cv = new ContentValues();
            Iterator iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                cv.put(entry.getKey().toString(), entry.getValue().toString());
            }
            return db.update(tableName, cv, whereClause, whereArgs);
        }catch(SQLiteException e){
            Log.e(e.toString(),"update error");
            return 0;
        }
    }
    public ArrayList query(String statement,String args){
        return this.query(statement,new String[]{args});
    }
    public ArrayList query(String statement,String[] args){
        try{
            ArrayList list = new ArrayList();
            Cursor c = db.rawQuery(statement,args);
            if(c.moveToFirst()){
                for(int k = 0;k < c.getCount();k++){
                    HashMap map = new HashMap();
                    c.move(k);
                    String[] columns = c.getColumnNames();
                    for(int i = 0;i<columns.length;i++){
                        String result = c.getString(c.getColumnIndex(columns[i]));
                        Log.i(result,columns[i]);
                        map.put(columns[i],result);
                    }
                    list.add(map);
                }
            }
            return list;
        }catch (SQLiteException e){
            Log.e(e.toString(),"error query");
            return new ArrayList();
        }
    }
}
