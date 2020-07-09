package com.example.t03team3mad;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class ViewGetDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_get_details);

        Intent intent = getIntent();

        Bundle PassedData = intent.getExtras().getBundle("Bundle");
        if(PassedData.getParcelable("book")!=null&&PassedData.getParcelable("User_UID")!=null){
            Book toview = PassedData.getParcelable("book");
            String uid = PassedData.getParcelable("User_UID");
            String isbn = "9781908843708";
            AsyncTask<String,Void,HashMap> getDetails = new APIaccessGetDetails().execute(isbn);
            HashMap<String, String> values=new HashMap<>();
            try {
                values = getDetails.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
    public class APIaccessGetDetails extends AsyncTask<String,Void, HashMap> {
        //jj- api url
        private String apiurl = "https://www.googleapis.com/";
        private static final String TAG = "APIaccess";
        //jj-empty contructor
        HashMap<String, String> values = new HashMap<>();
        public APIaccessGetDetails(){}
        //jj - search book by isbn using API
        public HashMap<String, String> searchbookbyisbn(String isbn) throws IOException, JSONException {
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
                    String forsale = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getString("saleability");
                    Log.v(TAG,"Creation");
                    Log.v(TAG,forsale);

                    String listprice;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("listPrice")){
                        listprice = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("listPrice").getString("amount");
                    }
                    else{
                        listprice = null;
                    }
                    Log.v(TAG,listprice);
                    String listcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("currencyCode")){
                        listcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("listPrice").getString("currencyCode");
                    }
                    else{
                        listcurrency = null;
                    }
                    Log.v(TAG,listcurrency);
                    String retailprice;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        retailprice = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("amount");
                    }
                    else{
                        retailprice = null;
                    }
                    Log.v(TAG,retailprice);
                    String Retailcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        Retailcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("currencyCode");
                    }
                    else{
                        Retailcurrency = null;
                    }
                    Log.v(TAG,Retailcurrency);
                    String previewLink;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("previewLink")){
                        previewLink = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("previewLink");
                    }
                    else{
                        previewLink = null;
                    }
                    Log.v(TAG,previewLink);
                    String pdflink;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("accessInfo").getJSONObject("pdf").has("acsTokenLink")){
                        pdflink = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("pdf").getString("acsTokenLink");
                    }
                    else{
                        pdflink = null;
                    }


                    values.put("EPUB","test");
                    return values;

                }
                else{
                    values.put("EPUB","test");
                    return values;
                }
            }
        }

        //jj-method that calls the searchbook method and runs it in background
        @Override
        protected HashMap doInBackground(String... urls) {
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
}
