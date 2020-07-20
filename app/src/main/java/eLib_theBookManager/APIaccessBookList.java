package eLib_theBookManager;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

import eLib_theBookManager.model.Book;

public class APIaccessBookList  extends AsyncTask<String,Void, ArrayList<Book>> {

    ProgressDialog dialog;
    private Context mContext;

    public APIaccessBookList (Context context ){
        mContext = context;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(mContext);
        dialog.setIndeterminate(true);
        dialog.setMessage("Please Wait...");
        dialog.setTitle("Loading Messages");
        dialog.setCancelable(true);
        dialog.show();
    }


    //jj- api url
    private String apiurl = "https://www.googleapis.com/";
    private static final String TAG = "APIaccessBookList";
    //jj-empty contructor
    public APIaccessBookList(){}
    //jj - search book by isbn using API
    public ArrayList<Book> searchbookbyisbn(String isbn) throws IOException, JSONException {
        BufferedReader reader = null;
        ArrayList<Book> listofresults = new ArrayList<>();
        if(isbn!=null) {
            String[] isbns = isbn.split(";");
            for (String isbntosearch : isbns) {
                if(isbntosearch==null||isbntosearch=="") {
                    continue;
                }
                Log.v(TAG, "Searching api isbn =" + isbntosearch);
                //jj-sets the url to GET data as json
                URL url = new URL(apiurl + "books/v1/volumes?q=isbn:" + isbntosearch);
                Log.v(TAG, "url searching =" + url.toString());
                //jj-opens the connection
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                int responsecode = conn.getResponseCode();
                //jj-checks if the response is successful
                if (responsecode != 200)
                    throw new RuntimeException("HttpResponseCode: " + responsecode);
                else {
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
                    if (bookjsonobj != null) {
                        String booktitle = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("title");
                        Log.v(TAG, "Creation");
                        Log.v(TAG, booktitle);

                        //qh - added this block of code because some books dont have value
                        String bookauthor;
                        if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("authors")) {
                            bookauthor = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
                        } else {
                            bookauthor = "Author Data not Available";
                        }
                        Log.v(TAG, bookauthor);
                        //qh - added this block of code because some books dont have value
                        String genrelist = "";
                        String bookgenre;
                        if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("categories")) {
                            JSONArray subjects = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("categories");
                            //jj-loops through all the subjects in the list of subjects and adds to a string
                            for (int i = 0; i < subjects.length(); i++) {
                                genrelist = genrelist + subjects.getString(i) + ";";
                            }
                            //jj-removes the last ";" at the end of list of genres
                            bookgenre = genrelist.substring(0, genrelist.length() - 1);
                        } else {
                            bookgenre = "Genres not available";
                        }

                        //qh - added this block of code because some books dont have value
                        String bookdes;
                        if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("description")) {
                            bookdes = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("description");
                        } else {
                            bookdes = "No Description";
                        }
                        String bookpdate = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("publishedDate");

                        //jj-creates the book object with json data

                        Book x = new Book(booktitle, bookauthor, bookdes, bookgenre, bookpdate, isbntosearch);
                        Log.v(TAG, "Book created =" + x.getIsbn() + "====" + x.getBookgenre() + "====" + x.getBooktitle() + "====" + x.getBookabout());
                        Log.v(TAG, "added+" + x.getIsbn());
                        listofresults.add(x);
                    }
                    else {
                        Book x = new Book("Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available");
                        listofresults.add(x);
                    }
                }
            }
        }
        return listofresults;
    }
    private Exception exception;

    //jj-method that calls the searchbook method and runs it in background
    @Override
    protected ArrayList<Book> doInBackground(String... urls) {
        try {
            return searchbookbyisbn(urls[0]);
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    protected void onPostExecute(ArrayList<Book> books) {
        super.onPostExecute(books);
        dialog.dismiss();
    }
}
