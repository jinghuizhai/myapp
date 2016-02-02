package model;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "mecaiwu.db"; //数据库名称
    private static final int version = 1; //数据库版本

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table user(user_id INTEGER(11) PRIMARY KEY,company varchar(50),phone varchar(20) not null , password varchar(60) not null );";
        db.execSQL(sql);
        sql = "create table msg(msg_id INTEGER(11) PRIMARY KEY," +
                "type varchar(50) not null," +
                "guest_id integer(11) not null," +
                "title varchar(50) not null," +
                "readed integer(11) not null," +
                "content varchar(500) not null," +
                "date datetime not null);";
        db.execSQL(sql);
        sql = "create table cookie(cookie_id INTEGER(11) PRIMARY KEY,name varchar(50) not null,expire datetime not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
    public boolean tableExist(String tabName){
        boolean result = false;
        if(tabName == null){
            throw new RuntimeException("table you query is not exist");
        }
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            String sql = "select count(*) as c from "+DB_NAME+" where type ='table' and name ='"+tabName.trim()+"'";
            cursor = db.rawQuery(sql, null);
            if(cursor.moveToNext()){
                int count = cursor.getInt(0);
                if(count>0){
                    result = true;
                }
            }
        } catch (Exception e) {
            Log.i("error",e.toString());
            // TODO: handle exception
        }
        return result;
    }


}