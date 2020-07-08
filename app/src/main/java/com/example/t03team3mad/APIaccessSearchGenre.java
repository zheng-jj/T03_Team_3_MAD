package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import com.example.t03team3mad.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//qh - followed jj to create search genre
public class APIaccessSearchGenre extends AsyncTask<String, Void, Book> {
    private String apiurl = "https://openlibrary.org/";

    private static final String TAG = "APIaccess2";

    public APIaccessSearchGenre(){}
    //Chris - search book by genre using API
    public Book searchbookbyGenre(String genre) throws IOException, JSONException {
        //jj-sets the url to GET data as json
        BufferedReader reader = null;
        String newtitle = genre.replace(' ', '+');
        Log.v(TAG,newtitle);
        URL url = new URL(apiurl+"api/books?bibkeys=subjects:"+genre+"&jscmd=details&format=json&maxResults=15");

        //        //jj-opens the connection
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        int responsecode = conn.getResponseCode();
        //jj-checks if the response is successful
        if(responsecode != 200)
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        else
        {
            Scanner sc = new Scanner(url.openStream());
            JSONObject bookjsonobj = null;
            while(sc.hasNext())
            {
                String temps = sc.nextLine();
                //jj-converts string into json object to use
                bookjsonobj = new JSONObject(temps);
                Log.v(TAG,"json created here "+bookjsonobj);
                //jj- i only need 1 record, hence break after 1 loop
                break;
            }
            sc.close();
            //jj - use this get data from json object to parse into object
            if(bookjsonobj != null) {
                String booktitle = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getString("title");
                Log.v(TAG,"Creation");
                Log.v(TAG,booktitle);
                String bookisbn = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getString("isbn_10");
                Log.v(TAG,"Creation");
                Log.v(TAG,booktitle);

                //qh - added this block of code because some books dont have value
                String bookauthor;
                if (bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").has("authors")){
                    bookauthor = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getJSONArray("authors").getJSONObject(0).getString("name");
                }
                else{
                    bookauthor = "Author Data not Available";
                }

                Log.v(TAG,bookauthor);
                //qh - added this block of code because some books dont have value
                String genrelist = "";
                String bookgenre;
                if (bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").has("subjects")){
                    JSONArray subjects = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getJSONArray("subjects");
                    //jj-loops through all the subjects in the list of subjects and adds to a string
                    for (int i = 0; i < subjects.length(); i++) {
                        genrelist = genrelist + subjects.getString(i) +";";
                    }
                    //jj-removes the last ";" at the end of list of genres
                    bookgenre = genrelist.substring(0, genrelist.length() - 1);
                }
                else {
                    bookgenre = "Genres not available";
                }

                //qh - added this block of code because some books dont have value
                String bookdes;
                if (bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").has("description")){
                    bookdes = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getString("description");
                }
                else{
                    bookdes = "No Description";
                }
                String bookpdate = bookjsonobj.getJSONObject("subjects:"+genre).getJSONObject("details").getString("publish_date");
                //jj-creates the book object with json data
                Book x = new Book(booktitle,bookauthor,bookdes,bookgenre,bookpdate,bookisbn);
                return x;
            }
            else{
                Book x = new Book("Book data not available","Book data not available","Book data not available","Book data not available","Book data not available","Book data not available");
                return x;
            }
        }
    }
    private Exception exception;

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected Book doInBackground(String... urls) {
        try {
            Log.v(TAG, urls[0]);
            Log.v(TAG, "I WANT TO SEE  WHAT URLS IS");

            return searchbookbyGenre(urls[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(List searchlist) {
    }
}