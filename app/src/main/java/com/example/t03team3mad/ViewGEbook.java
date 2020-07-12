package com.example.t03team3mad;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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


public class ViewGEbook extends Fragment {
    TextView pricer;
    TextView rcurrency;
    TextView pricel;
    TextView lcurrency;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_view_g_ebook,container,false);

        pricer = view.findViewById(R.id.priceretail);
        rcurrency = view.findViewById(R.id.retailcurrency);
        pricel = view.findViewById(R.id.pricelist);
        lcurrency = view.findViewById(R.id.listcurrency);
        Bundle bundle = this.getArguments();
        String isbn = bundle.getString("isbn");
        AsyncTask<String,Void, HashMap> getDetails = new ViewGEbook.APIaccessGetDetails().execute(isbn);
        HashMap<String, String> values=new HashMap<>();
        try {
            values = getDetails.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return view;
    }
    public class APIaccessGetDetails extends AsyncTask<String,Void, HashMap> {
        //jj- api url
        private String apiurl = "https://www.googleapis.com/";
        private static final String TAG = "APIaccessGetDetails";
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
                    pricel.setText(listprice);
                    Log.v(TAG,listprice);
                    String listcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("listPrice")){
                        listcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("listPrice").getString("currencyCode");
                    }
                    else{
                        listcurrency = null;
                    }
                    lcurrency.setText(listcurrency);
                    Log.v(TAG,listcurrency);
                    String retailprice;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        retailprice = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("amount");
                    }
                    else{
                        retailprice = null;
                    }
                    Log.v(TAG,retailprice);
                    pricer.setText(retailprice);
                    String Retailcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        Retailcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("currencyCode");
                    }
                    else{
                        Retailcurrency = null;
                    }
                    rcurrency.setText(Retailcurrency);
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
                    Log.v(TAG,"Retail price ="+retailprice);

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
