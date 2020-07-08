package com.example.t03team3mad;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;

public class DatabaseAccess {
    private static final String TAG = "DatabaseAccess";
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DatabaseAccess instance;
    private Cursor temp = null;
    private Cursor temp2 = null;
    private Cursor count = null;
    private Cursor cursor = null;
    private Cursor insert = null;
    String output;
    String output2;
    Integer out;
    // jo - constructor
    private DatabaseAccess (Context context){
        this.openHelper = new DatabaseOpenHelper(context);
    }
    // jo -gets state of the database
    public static DatabaseAccess getInstance(Context context){
        if(instance == null){
            instance = new DatabaseAccess(context);
        }
        return instance;
    }
    // jo - allows us to open the database to be used in fragments
    public void open(){
        this.db = openHelper.getWritableDatabase();

    }
    // jo - close database after use
    public void close(){
        if(db!=null){
            this.db.close();
        }
    }
    // jo - basic way of extracting data
    public String getElement(String column,String Table,String condition_column,String condition){
        temp = db.rawQuery ("select " + column +" from " + Table +" where " + condition_column + " = '"+ condition + "'",new  String[]{} );
        while(temp.moveToNext()){
            output = temp.getString(0);
        }
        return output;
    }
    // jo - get count of all existing tuples in the database from a table
    public String getCount(String column,String Table){
        count = db.rawQuery ("select count(" + column +") from " + Table ,new  String[]{} );
        while(count.moveToNext()){
            output2 = count.getString(0);
        }
        return output2;
    }
    // jo - addData for reviews
    public boolean addData(String idr,String idu,String review, String ISBN){
        ContentValues contentValues = new ContentValues();
        contentValues.put("IDU",idu);
        contentValues.put("IDR",idr);
        contentValues.put("Review",review);
        contentValues.put("ISBN",ISBN);

        long result = db.insert("Reviews",null,contentValues);
        if (result== -1){
            return false;
        }
        else{
            return true;
        }
    }

    //jj- this method is used to load the users into a list and returns the list(create one for each table so can load into recyclerview)
    public List<User> loadalluserlist() {

        List<User> mUserlist = new ArrayList<User>(){};
        Integer amountofusers =Integer.valueOf(getCount("IDU","USER"));
        cursor = db.rawQuery("Select * From USER",new  String[]{});
        cursor.moveToFirst();
        do {
            String name = cursor.getString(1);
            String des = cursor.getString(2);
            String books = cursor.getString(3);
            int idu =Integer.parseInt(cursor.getString(0));
            Log.v(TAG,Integer.toString(idu));
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
            String title = cursor.getString(0);
            String author = cursor.getString(1);
            String about = cursor.getString(2);
            String genre = cursor.getString(3);
            String pdate = cursor.getString(4);
            String isbn = cursor.getString(5);
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

    //qh - method to search books in the database
    public List<Book> searchauthorbook(String query) {
        List<Book> booklist = new ArrayList<Book>();
        Cursor c = db.rawQuery("SELECT * FROM Book WHERE IDA == '"+query+"'", new String[]{});
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


    //qh - method to search user in the database
    public List<User> searchUser(String query) {
        List<User> userList = new ArrayList<User>();
        Cursor c = db.rawQuery("SELECT * FROM USER WHERE TRIM(NAME) LIKE '%" + query + "%'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String idu = c.getString(0);
                String name = c.getString(1);
                String isbn = c.getString(3);
                String about = c.getString(2);
                User user1 = new User(Integer.parseInt(idu),name,isbn,about);
                userList.add(user1);
                Log.v(TAG, "search"+user1.getUsername());
            } while (c.moveToNext());
        }
        return userList;
    }
    //jj - loads all books the user favourited
    public List<Book> loaduserbooklist(User user) {
        List<Book> userbooklist = new ArrayList<Book>();
        if(user.getUserisbn()!=null){
            String[] userisbn = user.getUserisbn().split(";");
            for(String isbn:userisbn){
                Log.v(TAG,"-"+isbn+"-");}
            for(String isbn:userisbn){
                Book temp = searchbookbyisbn(isbn);
                userbooklist.add(temp);
            }
        }
        return userbooklist;
    }
    //jj - search book by isbn
    public Book searchbookbyisbn(String isbn) {
        Book book1 = null;
        Cursor c = db.rawQuery("SELECT * FROM Book WHERE ISBN == '"+isbn+"'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String title = c.getString(0);
                String ida = c.getString(1);
                String about = c.getString(2);
                String genre = c.getString(3);
                String pdate = c.getString(4);
                book1 = new Book(title, ida, about, genre, pdate, isbn);
                Log.v(TAG,"book created here "+title+isbn);
            } while (c.moveToNext());
        }
        return book1;
    }
    //qh - search book by title
    public Book searchbookbytitle(String title) {
        Book book1 = null;
        System.out.println("debug1");
        Cursor c = db.rawQuery("SELECT * FROM BOOK WHERE TITLE = '"+title+"'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                System.out.println("debug2");
                String ida = c.getString(1);
                String about = c.getString(2);
                String genre = c.getString(3);
                String pdate = c.getString(4);
                String isbn = c.getString(5);
                book1 = new Book(title, ida, about, genre, pdate, isbn);
            } while (c.moveToNext());
        }
        return book1;
    }
    //Chris - Search Book by genre
    public List<Book> searchgenre(String query) {
        List<Book> booklist = new ArrayList<Book>();
        Cursor c = db.rawQuery("SELECT * FROM BOOK WHERE TRIM(GENRE) LIKE '%" + query + "%'", new String[]{});
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
    //qh - search author by ida
    public Author searchauthorbyida(String ida) {
        Author author1 = null;
        Cursor c = db.rawQuery("SELECT * FROM AUTHOR WHERE IDA =="+ida, new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String name = c.getString(1);
                String about = c.getString(2);
                author1 = new Author(Integer.parseInt(ida), name,about);
            } while (c.moveToNext());
        }
        return author1;
    }
    //qh - search author by name
    public Author searchauthorbyname(String name) {
        Author author1 = null;
        Cursor c = db.rawQuery("SELECT * FROM AUTHOR WHERE NAME ='"+name+"'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String ida = c.getString(0);
                String about = c.getString(2);
                author1 = new Author(Integer.parseInt(ida), name,about);
            } while (c.moveToNext());
        }
        return author1;
    }
    //qh - search user by name
    public User searchuserbyname(String name) {
        User user1 = null;
        Cursor c = db.rawQuery("SELECT * FROM USER WHERE NAME ='"+name+"'", new String[]{});
        if (c.moveToFirst() && c.getCount() >= 1) {
            do {
                String idu = c.getString(0);
                String isbn = c.getString(3);
                String about = c.getString(2);
                user1 = new User(Integer.parseInt(idu),name,isbn,about);
            } while (c.moveToNext());
        }
        return user1;
    }

    //qh - search user by id
    public User searchuserbyid(String idu) {
        User user1 = null;
        try {
            Cursor c = db.rawQuery("SELECT * FROM USER WHERE IDU = "+idu, new String[]{});
            if (c.moveToFirst() && c.getCount() >= 1) {
                do {
                    String name = c.getString(1);
                    String isbn = c.getString(3);
                    String about = c.getString(2);
                    user1 = new User(Integer.parseInt(idu),name,isbn,about);
                } while (c.moveToNext());
            }
        }
        catch (Exception e){
            Log.v(TAG,"Error search user by id");
        }
        return user1;
    }
    //jj-gets all the reviews a user has written
    public List<Review> loaduserreviews(User user) {
        List<Review> mReviewlist = new ArrayList<Review>() {};
        temp = db.rawQuery("Select * From Reviews WHERE IDU = '"+user.getUseridu()+"'", new String[]{});
        temp.moveToFirst();
        try {
            do {
                String idus = temp.getString(0);
                String idrs = temp.getString(1);
                String review = temp.getString(2);
                String ISBN = temp.getString(3);
                String uname = user.getUsername();
                String title = searchbookbyisbn(ISBN).getBooktitle();
                String bookname = searchbookbyisbn(ISBN).getBooktitle();
                Review reviewobj = new Review(idus, idrs, uname, title, review, ISBN);
                reviewobj.setBookName(bookname);
                mReviewlist.add(reviewobj);
            } while (temp.moveToNext());
        }catch (Exception e){
            Log.v(TAG,"No reviews made by user");
        }
        return mReviewlist;
    }
    // jo- Get all reviews for the book
    public List<Review> extractreviewbybook(String ISBN) {
        List<Review> mReviewlist = new ArrayList<Review>() {
        };
        temp2 = db.rawQuery("Select * From Reviews WHERE ISBN = '" + ISBN +"'", new String[]{});
        temp2.moveToFirst();
        Log.d("list",getCount("IDR","Reviews"));
        while (temp2.moveToNext()) {

            String idus = temp2.getString(0);
            String idrs = temp2.getString(1);
            String review = temp2.getString(2);
            String uname = getElement("Name", "USER", "IDU", idus);
            String title = getElement("Title", "Book", "ISBN", ISBN);
            Review review1 = new Review(idus,idrs , uname, title, review, ISBN);
            mReviewlist.add(review1);
            Log.d("list",mReviewlist.toString());}

        temp2.close();

        return mReviewlist;
    }

    //Chris - Add User data into the database
    public boolean addData(String idu,String Name,String about, String favouriteb,String following){
        ContentValues contentValues = new ContentValues();
        contentValues.put("IDU",idu);
        contentValues.put("NAME",Name);
        contentValues.put("ABOUT",about);
        contentValues.put("FAVOURITEB",favouriteb);
        contentValues.put("Following",following);

        long result = db.insert("USER",null,contentValues);
        if (result== -1){
            return false;
        }
        else{
            return true;
        }
    }
    //jj- gets the list of users the user is following and returns them in a list
    public List<User> getUserFollowing(String UID){
        Log.v(TAG,"get list of users following for user "+UID);
        List<User> mUserlist = new ArrayList<User>(){};
        cursor = db.rawQuery("Select * From USER WHERE IDU = '"+UID+"'",new  String[]{});
        cursor.moveToFirst();
        try {
            do {
                String following = cursor.getString(4);
                List<String> listOffollowingUsersID = Arrays.asList(following.split(";"));
                Log.v(TAG, Integer.toString(listOffollowingUsersID.size()));
                if (listOffollowingUsersID.size() > 0) {
                    for (String followingID : listOffollowingUsersID) {
                        Log.v(TAG, "Following user's ID " + followingID);
                        mUserlist.add(searchuserbyid(followingID));
                    }
                }
            } while (cursor.moveToNext());
        }
        catch (Exception e){
            Log.v(TAG,"User has no followings");
        }
        return mUserlist;
    }
    //jj- edit user data in database
    //jj- just use this part if u need to change database
    public void editUserData(User user){
        String dbformattedFollowing = user.getfollowingstring();

        ContentValues cv = new ContentValues();
        cv.put("NAME",user.getUsername());
        cv.put("ABOUT",user.getUserabout());
        cv.put("FAVOURITEB",user.getUserisbn());
        if(dbformattedFollowing!=null) {
            if (dbformattedFollowing.equals("")) {
                cv.putNull("FOLLOWING");
            } else {
                cv.put("FOLLOWING", dbformattedFollowing);
            }
        }
        else {
            cv.put("FOLLOWING","");
        }
        db.update("USER", cv, "IDU="+Integer.toString(user.getUseridu()), null);
    }
    //jj-update user following list
    public void updateUserFollowing(User user, String followingstring){
        ContentValues cv = new ContentValues();
        if(followingstring.equals("")){
            cv.putNull("FOLLOWING");
        }
        else {
            cv.put("FOLLOWING", followingstring);
        }
        db.update("USER", cv, "IDU="+Integer.toString(user.getUseridu()), null);
    }

    //Chris -  check user record if exist in local database
    public boolean CheckExistingRecordByUserId(String idu) {
        Cursor c = db.rawQuery("SELECT * FROM USER WHERE IDU =="+idu, new String[]{});

        if (c.moveToFirst() && c.getCount() >= 1) {
            c.close();
            return true;

        }
        else
        {
            c.close();
            return false;
        }

    }
}


