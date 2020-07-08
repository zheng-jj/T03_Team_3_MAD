package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.google.gson.annotations.JsonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

//qh - followed jj to create search booktitle
public class APIaccessSearchBookTitle extends AsyncTask<String,Void,List<SearchClass>>{
    private String apiurl = "https://www.googleapis.com/";
    //private String apiurl = "https://openlibrary.org/";

    private static final String TAG = "APIaccess2";

    public APIaccessSearchBookTitle(){}
    //qh - search book by title using API
    public List<SearchClass> searchbookbytitle(String title) throws IOException, JSONException {
        //jj-sets the url to GET data as json
        BufferedReader reader = null;
        String newtitle = title.replace(' ', '+');
        Log.v(TAG,newtitle);
        URL url = new URL(apiurl+"books/v1/volumes?q=" + newtitle + ":intitle&maxResults=5");
        //URL url = new URL(apiurl+"search.json?q=" + newtitle);

        //jj-opens the connection
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setRequestMethod("GET");
        int responsecode = conn.getResponseCode();
        //jj-checks if the response is successful
        if(responsecode != 200)
            throw new RuntimeException("HttpResponseCode: " +responsecode);
        else
        {

            Scanner sc = new Scanner(url.openStream());
            List<JSONObject> booklist = new ArrayList<JSONObject>(){};
            List<SearchClass> booklistBOOK = new ArrayList<SearchClass>(){};
            JSONObject bookjsonobj = null;
            //qh - to read the url
            InputStream stream = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";
            //qh - jjs method didnt work for me so i used my own method
            while ((line = reader.readLine()) != null) {
                buffer.append(line+"\n");
            }
            String newstring = buffer.toString();
            bookjsonobj = new JSONObject(newstring);
            Log.v(TAG, newstring);
            //qh - all the json object stuff
            JSONArray jsonarray = bookjsonobj.getJSONArray("items");
            //JSONArray jsonarray = bookjsonobj.getJSONArray("docs");
            for (int i = 0; i < jsonarray.length(); i++) {
                String booktitle = bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("title");
                //String booktitle = bookjsonobj.getJSONArray("docs").getJSONObject(i).getString("title");

                //String des = bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("description");
                String des = "This book was retrieved from Google Books";
                String isbn = new String();
                if (bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").has("industryIdentifiers")){
                    isbn = bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("industryIdentifiers").getJSONObject(0).getString("identifier");
                }
                else {
                    isbn = "";
                }
                //String isbn = new String();
                //JSONArray isbnlist = bookjsonobj.getJSONArray("docs").getJSONObject(i).getJSONArray("isbn");
                //for (int x = 0; i < 1; i++) {
                    //isbn = isbnlist.getString(0);
                //}

                SearchClass newsearchobject = new SearchClass(booktitle,des,"Book",isbn);
                booklistBOOK.add(newsearchobject);

            }

            return booklistBOOK;
        }
    }
    private Exception exception;

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected List<SearchClass> doInBackground(String... urls) {
        try {
            Log.v(TAG, urls[0]);
            Log.v(TAG, "I WANT TO SEE  WHAT URLS IS");

            return searchbookbytitle(urls[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(List<SearchClass> searchlist) {
    }
}