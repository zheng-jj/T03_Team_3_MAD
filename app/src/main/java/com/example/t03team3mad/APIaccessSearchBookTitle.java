package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//qh - followed jj to create search booktitle
public class APIaccessSearchBookTitle extends AsyncTask<String,Void,List<SearchClass>>{
    private String apiurl = "https://openlibrary.org/";
    private static final String TAG = "APIaccess";

    public APIaccessSearchBookTitle(){}
    //qh - search book by title using API
    public List<SearchClass> searchbookbytitle(String title) throws IOException, JSONException {
        //jj-sets the url to GET data as json
        String newtitle = title.replace(' ', '+');
        Log.v(TAG,newtitle);
        URL url = new URL(apiurl+"search.json?q=" + newtitle+"&format=json");
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
            while(sc.hasNext())
            {
                String tempstring = sc.nextLine();
                Log.v(TAG, "THIS IS TEMP STRING");
                Log.v(TAG, tempstring);
                bookjsonobj = new JSONObject(tempstring);
                booklist.add(bookjsonobj);
            }
            sc.close();
            int count = 0;
            for (JSONObject i : booklist){

                if(i != null) {
                    String booktitle = bookjsonobj.getJSONArray("docs").getJSONObject(count).getString("title");
                    String des = "This book was retrieved from OpenLibrary";
                    //(bookjsonobj.getJSONArray("docs:").getJSONObject(count).getJSONArray("author_name").getJSONObject(0)).toString();
                    String isbn = (bookjsonobj.getJSONArray("docs").getJSONObject(count).getJSONArray("isbn").getJSONObject(count)).toString();
                    //jj-creates the book object with json data
                    SearchClass newsearchobject = new SearchClass(booktitle,des,"Book",isbn);
                    booklistBOOK.add(newsearchobject);
                }
            }
            return booklistBOOK;
        }
    }
    private Exception exception;

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected List<SearchClass> doInBackground(String urls[]) {
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