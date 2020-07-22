package com.T03G3.eLibtheBookManager;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.T03G3.eLibtheBookManager.model.buyBook;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class ViewPrice extends Fragment {

    Context context;
    RecyclerView recyclerView;
    AdapterBuyBook adapterBuyBook;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view_price,container,false);

        context=getContext();

        TextView title = view.findViewById(R.id.booktitle);
        title.setText(ViewToGet.book.getBooktitle());

        recyclerView = (RecyclerView) view.findViewById(R.id.nlbList);

        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recyclerView.setLayoutManager(llm);

        AsyncTask<String, Void, String> task = new APIaccessGetPrice().execute(ViewToGet.book.getIsbn());

        return view;
    }




    public class APIaccessGetPrice extends AsyncTask<String,Void, String> {
        //jj- api url
        private String apiurl = "https://www.directtextbook.com/xml.php?key=";
        private static final String TAG = "APIaccessGetDetails";
        //jj-empty contructor
        HashMap<String, String> values = new HashMap<>();
        public APIaccessGetPrice(){}
        //jj - search book by isbn using API


        public String searchbookprice(String isbn) throws IOException, JSONException {
            BufferedReader reader = null;
            if(isbn==null||isbn==""){
                return null;
            }

            //jj-sets the url to GET data
            String urlstring = apiurl+getResources().getString(R.string.directkey)+"&ean="+isbn;

            //Get Document Builder
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = factory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

            Log.v(TAG, "searching url ="+ urlstring);
            if(builder == null){
                Log.v(TAG, "builder is null");
            }
            else{
                Log.v(TAG,"builder not null");
            }

            final URL url = new URL(urlstring);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            int responsecode = conn.getResponseCode();
            //jj-checks if the response is successful
            if(responsecode != 200)
                throw new RuntimeException("HttpResponseCode: " +responsecode);
            else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String xmlResponse = response.toString();
                Log.v(TAG,xmlResponse);

                return xmlResponse;
            }
        }

        //jj-method that calls the searchbook method and runs it in background
        @Override
        protected String doInBackground(String... urls) {
            try {
                return searchbookprice(urls[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);

            //jj-removes the header from xml string(<?xml version="1.0"  encoding="ISO-8859-1"?>)
            String XmlString=string.replaceAll("<?xml version=\"1.0\"  encoding=\"ISO-8859-1\"?>", "").trim();


            //String XmlString=string;
            Log.v(TAG,"string being parsed ="+XmlString);

            DocumentBuilder builder = null;
            try {
                builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
//            InputStream is = null;
//            try {
//                is = new ByteArrayInputStream(XmlString.getBytes("UTF-8"));
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            Document doc = null;
            try {
                //jj-converts the XML string into a document
                doc = builder.parse(new InputSource( new StringReader(XmlString)) );

                if(doc!=null){
                    Log.v(TAG,"doc is not null");
                }
                else{
                    Log.v(TAG,"doc is null");
                }

                NodeList nList = doc.getElementsByTagName("item");

                ArrayList<buyBook> listofVendors = new ArrayList<>();

                for (int temp = 0; temp < nList.getLength(); temp++) {

                    Node nNode = nList.item(temp);
                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
                    //jj- gets the data from the XML string
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        System.out.println("vendor : "
                                + eElement.getElementsByTagName("vendor").item(0).getTextContent());
                        System.out.println("price : "
                                + eElement
                                .getElementsByTagName("price")
                                .item(0)
                                .getTextContent());

                        //jj-creates the new buy book object to be displayed in recycler view
                        String vendor = eElement.getElementsByTagName("vendor").item(0).getTextContent();
                        String condition = eElement.getElementsByTagName("condition").item(0).getTextContent();
                        String price = eElement.getElementsByTagName("price").item(0).getTextContent();
                        String currency = eElement.getElementsByTagName("currency").item(0).getTextContent();
                        String url = eElement.getElementsByTagName("url").item(0).getTextContent();
                        buyBook toadd = new buyBook(vendor,condition,url,price,currency);
                        listofVendors.add(toadd);
                    }
                }
                //jj-notify the recyclerview on the new list of vendors
                adapterBuyBook = new AdapterBuyBook(listofVendors,context);
                recyclerView.setAdapter(adapterBuyBook);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }


        }

    }
}
