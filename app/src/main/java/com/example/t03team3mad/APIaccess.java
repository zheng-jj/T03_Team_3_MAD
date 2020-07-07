package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import com.example.t03team3mad.model.Book;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

//jj- async to run in background(not in main thread)
//jj- this class is only used to get books
public class APIaccess extends AsyncTask<String,Void,Book>{
    //jj- api url
    private String apiurl = "https://openlibrary.org/";
    private static final String TAG = "APIaccess";
    //jj-empty contructor
    public APIaccess(){}
    //jj - search book by isbn using API
    public Book searchbookbyisbn(String isbn) throws IOException, JSONException {
        //jj-sets the url to GET data as json
        URL url = new URL(apiurl+"api/books?bibkeys=ISBN:"+isbn+"&jscmd=details&format=json");
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
                String booktitle = bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").getString("title");
                Log.v(TAG,"Creation");
                Log.v(TAG,booktitle);

                //qh - added this block of code because some books dont have value
                String bookauthor;
                if (bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").has("authors")){
                    bookauthor = bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").getJSONArray("authors").getJSONObject(0).getString("name");
                }
                else{
                    bookauthor = "Author Data not Available";
                }

                Log.v(TAG,bookauthor);
                //qh - added this block of code because some books dont have value
                String genrelist = "";
                String bookgenre;
                if (bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").has("subjects")){
                    JSONArray subjects = bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").getJSONArray("subjects");
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
                if (bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").has("description")){
                    bookdes = bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").getString("description");
                }
                else{
                    bookdes = "No Description";
                }
                String bookpdate = bookjsonobj.getJSONObject("ISBN:"+isbn).getJSONObject("details").getString("publish_date");
                //jj-creates the book object with json data
                Book x = new Book(booktitle,bookauthor,bookdes,bookgenre,bookpdate,isbn);
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
