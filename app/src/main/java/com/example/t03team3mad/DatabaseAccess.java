package com.example.t03team3mad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.t03team3mad.model.User;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private Cursor temp = null;
    private Cursor count = null;
    private Cursor cursor = null;
    String output;
    String output2;
    Integer out;
    private DatabaseAccess (Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }
    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
    public void open(){
        this.db = openHelper.getWritableDatabase();

    }
    public void close(){
        if(db!=null){
            this.db.close();
        }
    }
    public String getElement(String column,String Table,String condition_column,String condition){
        temp = db.rawQuery ("select " + column +" from " + Table +" where " + condition_column + " = '"+ condition + "'",new  String[]{} );
        while(temp.moveToNext()){
            output = temp.getString(0);
        }
        return output;
    }
    public String getCount(String column,String Table){
        count = db.rawQuery ("select count(" + column +") from " + Table ,new  String[]{} );
        while(count.moveToNext()){
            output2 = count.getString(0);
        }
        return output2;
    }
    public List<User> loadalluserlist() {
        List<User> mUserlist = new ArrayList<User>(){};
        Integer amountofusers =Integer.valueOf(getCount("IDU","USER"));
        cursor = db.rawQuery("Select * From USER",new  String[]{});
        cursor.moveToFirst();
        do {
            String name = cursor.getString(cursor.getColumnIndex("NAME"));
            String des = cursor.getString(cursor.getColumnIndex("ABOUT"));
            String books = cursor.getString(cursor.getColumnIndex("ISBN"));
            int idu =Integer.valueOf(cursor.getString(cursor.getColumnIndex("IDU")));
            User usertoaddtolist = new User(idu,name,books,des);
            mUserlist.add(usertoaddtolist);
            }while (cursor.moveToNext());
        return  mUserlist;
    }
}
