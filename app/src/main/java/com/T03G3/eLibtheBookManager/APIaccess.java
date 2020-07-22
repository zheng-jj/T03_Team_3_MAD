package com.T03G3.eLibtheBookManager;

import android.os.AsyncTask;
import android.util.Log;

import com.T03G3.eLibtheBookManager.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//jj- async to run in background(not in main thread)
//jj- this class is only used to get books
public class APIaccess extends AsyncTask<String,Void,Book>{
    //jj- api url
    private String apiurl = "https://www.googleapis.com/";
    private static final String TAG = "APIaccess";
    //jj-empty contructor
    public APIaccess(){}
    //jj - search book by isbn using API
    public Book searchbookbyisbn(String isbn) throws IOException, JSONException {
        BufferedReader reader = null;
        if(isbn==null||isbn==""){
            return null;
        }
        //jj-sets the url to GET data as json
        URL url = new URL(apiurl+"books/v1/volumes?q=isbn:"+isbn+"&maxResults=1");
        //jj-opens the connection
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        int responsecode = conn.getResponseCode();
        //jj-checks if the response is successful
        if(responsecode != 200)
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        else
        {
            //jj - gets the string from the url
            JSONObject bookjsonobj = null;
            InputStream stream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            String newstring = buffer.toString();
            bookjsonobj = new JSONObject(newstring);
            //jj - use this get data from json object to parse into object
            if(bookjsonobj != null) {
                String booktitle = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("title");
                Log.v(TAG,"Creation");
                Log.v(TAG,booktitle);

                //qh - added this block of code because some books dont have value
                String bookauthor;
                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("authors")){
                    bookauthor = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
                }
                else{
                    bookauthor = "Author Data not Available";
                }
                Log.v(TAG,bookauthor);
                //qh - added this block of code because some books dont have value
                String genrelist = "";
                String bookgenre;
                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("categories")){
                    JSONArray subjects = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("categories");
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
                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("description")){
                    bookdes = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("description");
                }
                else{
                    bookdes = "No Description";
                }
                String bookpdate = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("publishedDate");

                //jj-creates the book object with json data

                Book x = new Book(booktitle, bookauthor, bookdes, bookgenre, bookpdate, isbn);
                Log.v(TAG,"Book created ="+x.getIsbn()+"===="+x.getBookgenre()+"===="+x.getBooktitle()+"===="+x.getBookabout());
                Log.v(TAG, "added+" + x.getIsbn());
                return x;

            }
            else{
                Book x = new Book("Book data not available","Book data not available","Book data not available","Book data not available","Book data not available","Book data not available");
                return x;
            }
        }
    }

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected Book doInBackground(String... urls) {
        try {
            Log.v(TAG, urls[0]);
            Log.v(TAG, "TestLOG");
            return searchbookbyisbn(urls[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
