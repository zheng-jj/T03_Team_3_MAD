package com.example.t03team3mad;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
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
    //jj- this method is used to load the users into a list and returns the list(create one for each table so can load into recyclerview)
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
    //jj- same as for load userlist
    public List<Book> loadallbooklist() {
        List<Book> mBooklist = new ArrayList<Book>(){};
        Integer amountofusers =Integer.valueOf(getCount("IDU","USER"));
        cursor = db.rawQuery("Select * From Book",new  String[]{});
        cursor.moveToFirst();
        do {
            String title = cursor.getString(cursor.getColumnIndex("TITLE"));
            String author = cursor.getString(cursor.getColumnIndex("AUTHOR"));
            String about = cursor.getString(cursor.getColumnIndex("ABOUT"));
            String genre = cursor.getString(cursor.getColumnIndex("GENRE"));
            String pdate = cursor.getString(cursor.getColumnIndex("PDATE"));
            String isbn = cursor.getString(cursor.getColumnIndex("ISBN"));
            Book booktoaddtolist = new Book(title,author,about,genre,pdate,isbn);
            mBooklist.add(booktoaddtolist);
        }while (cursor.moveToNext());
        return  mBooklist;
    }

    //qh - method to search books in the database
    public List<Book> searchbook(String query) {
        List<Book> booklist = new ArrayList<Book>();
        Cursor c = db.rawQuery("SELECT * FROM BOOK WHERE TRIM(TITLE) LIKE '%" + query + "%'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String title = c.getString(0);
                String ida = c.getString(1);
                String about = c.getString(2);
                String genre = c.getString(3);
                String pdate = c.getString(4);
                String isbn = c.getString(5);
                Book book1 = new Book(title, ida, about, genre, pdate, isbn);
                booklist.add(book1);
                System.out.println(book1.getBooktitle());
            } while (c.moveToNext());
        }
        return booklist;
    }

    //qh - method to search author in the database
    public List<Author> searchAuthor(String query) {
        List<Author> authorList = new ArrayList<Author>();
        Cursor c = db.rawQuery("SELECT * FROM AUTHOR WHERE TRIM(NAME) LIKE '%" + query + "%'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String ida = c.getString(0);
                String name = c.getString(1);
                String about = c.getString(2);
                Author author1 = new Author(Integer.parseInt(ida),name,about);
                authorList.add(author1);
                System.out.println(author1.getAuthorname());
            } while (c.moveToNext());
        }
        return authorList;
    }
}
