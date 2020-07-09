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
public class APIaccessSearchGenre extends AsyncTask<String, Void, ArrayList<Book>> {
    private String apiurl = "https://www.googleapis.com/";

    private static final String TAG = "APIaccess2";

    public APIaccessSearchGenre(){}
    //Chris - search book by genre using API
    public ArrayList<Book> searchbookbyGenre(String genre) throws IOException, JSONException {
        //jj-sets the url to GET data as json
        BufferedReader reader = null;
        String newtitle = genre.replace(' ', '+');
        Log.v(TAG,newtitle);
        URL url = new URL(apiurl+"books/v1/volumes?q="+genre+"subject&maxResults=10");

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
            List<JSONObject> booklist = new ArrayList<JSONObject>(){};
            ArrayList<Book> booklistBOOK = new ArrayList<Book>(){};
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

                genre=bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("authors");
                String author = bookjsonobj.getJSONArray("items").getJSONObject(i).getJSONObject("volumeInfo").getString("authors");
                String Loadauthor=author.substring(2,author.length()-2);
                //String isbn = new String();
                //JSONArray isbnlist = bookjsonobj.getJSONArray("docs").getJSONObject(i).getJSONArray("isbn");
                //for (int x = 0; i < 1; i++) {
                //isbn = isbnlist.getString(0);
                //}

                Book newsearchobject = new Book(booktitle,Loadauthor,des,genre,"123",isbn,0);
                booklistBOOK.add(newsearchobject);

            }

            return booklistBOOK;
        }
    }
    private Exception exception;

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected ArrayList<Book> doInBackground(String... urls) {
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