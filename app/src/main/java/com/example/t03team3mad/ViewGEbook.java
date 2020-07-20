package com.example.t03team3mad;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    TextView pricel;
    ImageView pdfavailtextv;
    ImageView epubavailtextv;
    Button preview;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_view_g_ebook,container,false);

        pricer = view.findViewById(R.id.priceretail);

        pricel = view.findViewById(R.id.pricelist);

        pdfavailtextv = view.findViewById(R.id.pdftext);
        epubavailtextv = view.findViewById(R.id.epubtext);
        preview = view.findViewById(R.id.preview);
        TextView title = view.findViewById(R.id.booktitle);

        Bundle bundle = this.getArguments();
        String isbn = ViewToGet.book.getIsbn();
        AsyncTask<String,Void, HashMap> getDetails = new ViewGEbook.APIaccessGetDetails().execute(isbn);
        title.setText(ViewToGet.book.getBooktitle());

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
            final URL url = new URL(apiurl+"books/v1/volumes?q=isbn:"+isbn+"&maxResults=1");
            Log.v(TAG,"Url searching ="+url);
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
                        pricel.setText(listprice);
                        Log.v(TAG,listprice);
                    }
                    else{
                        listprice = null;
                        pricel.setText("Not Available");
                    }

                    String listcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("listPrice")){
                        listcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("listPrice").getString("currencyCode");
                        pricel.setText(listprice+" "+listcurrency);
                        Log.v(TAG,listcurrency);
                    }
                    else{
                        listcurrency = null;
                    }

                    String retailprice;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        retailprice = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("amount");
                        Log.v(TAG,retailprice);
                        pricer.setText(retailprice);
                    }

                    else{
                        retailprice = null;
                        pricer.setText("Not Available");
                    }

                    String Retailcurrency;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").has("retailPrice")){
                        Retailcurrency = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("saleInfo").getJSONObject("retailPrice").getString("currencyCode");
                        pricer.setText(retailprice+" "+Retailcurrency);
                        Log.v(TAG,Retailcurrency);
                    }

                    else{
                        Retailcurrency = null;
                    }




                    Boolean epubavail;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("accessInfo").has("epub")) {
                        epubavail = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("accessInfo").getJSONObject("epub").getBoolean("isAvailable");
                        Log.v(TAG, epubavail.toString());
                        if (epubavail) {
                            epubavailtextv.setImageResource(R.drawable.tick2);
                        }
                    }
                    Boolean pdfavail;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("accessInfo").has("pdf")) {
                        pdfavail = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("accessInfo").getJSONObject("pdf").getBoolean("isAvailable");
                        Log.v(TAG, pdfavail.toString());
                        if (pdfavail) {
                            pdfavailtextv.setImageResource(R.drawable.tick2);
                        }
                    }
                    final String previewLink;
                    if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("previewLink")){
                        previewLink = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("previewLink");
                        preview.setText("Preview Available (Click here to open in browser)");
                        preview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(previewLink));
                                startActivity(i);
                            }
                        });
                        Log.v(TAG,previewLink);
                    }
                    else{
                        preview.setText("Preview For this Book is Not Available");
                    }

                    return values;
                }
            }
            return null;
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
