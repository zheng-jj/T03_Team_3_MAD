package com.example.t03team3mad;
import java.util.HashMap;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private Cursor temp = null;
    private Cursor count = null;
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




}
