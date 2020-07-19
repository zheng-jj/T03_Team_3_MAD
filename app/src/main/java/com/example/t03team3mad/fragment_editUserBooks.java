package com.example.t03team3mad;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
import java.util.concurrent.ExecutionException;

public class fragment_editUserBooks extends Fragment {
    private static final String TAG = "edituserbooksFragment";
    ArrayList<Book> userfav = new ArrayList<>();
    RecyclerView favouritebooks;
    AdapterFavBooksList bookadapter;
    Button savebookchanges;
    User usertoEdit2;
    private CollectionReference mCollectionBook2 = FirebaseFirestore.getInstance().collection("Book");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edituserbooks,container,false);
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            usertoEdit2=usertoEdit;
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }

        //jj - load favourite user books recyclerview
        favouritebooks = (RecyclerView) view.findViewById(R.id.edituserbookrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        bookadapter = new AdapterFavBooksList(userfav);
        favouritebooks.setAdapter(bookadapter);

        savebookchanges = view.findViewById(R.id.savebooks);

        AsyncTask<String, Void, Void> task = new fragment_editUserBooks.APIaccessBookListUser(getContext()).execute(usertoEdit.getUserisbn());
        try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadBookurlsbooks();




        return view;
    }


    //jj loads the url into book objc
    public void loadBookurlsbooks() {
        mCollectionBook2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Book book : userfav){
                        Log.v(TAG,"Bookloop="+book.getIsbn());
                        for(DocumentSnapshot doc : data){
                            Log.v(TAG,"Docloop="+doc.getReference().getId());
                            if(doc.getReference().getId().equals(book.getIsbn())){
                                book.setimglink(doc.getString("coverurl"));
                            }
                        }
                    }
                    bookadapter.notifyDataSetChanged();
                }
            }
        });
    }

    //jj-moved the method here so that there wont be crashes due to slow load times
    public class APIaccessBookListUser  extends AsyncTask<String,Void, Void> {

        ProgressDialog dialog;
        private Context mContext;

        public APIaccessBookListUser (Context context ){
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
        private static final String TAG = "APIaccessBookListUser";
        //jj-empty contructor
        public APIaccessBookListUser(){}
        //jj - search book by isbn using API
        public void searchbookbyisbn(String isbn) throws IOException, JSONException {
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
                            userfav.add(x);
                        }
                        else {
                            Book x = new Book("Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available");
                            userfav.add(x);
                        }
                    }
                }
            }
            loadBookurlsbooks();

            bookadapter.mBooklist=userfav;
            bookadapter.notifyDataSetChanged();
            final User finalUsertoEdit = usertoEdit2;



            //choose to remove/unremove books from user's list
            final ArrayList<Book> finalUserfav = userfav;
            //jj- moved on click listener here so that so it wont crash
            savebookchanges.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(finalUserfav!=null) {
                        for (Book x : finalUserfav) {
                            Log.v(TAG, "Previously in list book :" + x.getBooktitle() + " " + x.getIsbn());
                        }
                    }
                    String isbn = "";
                    if(finalUserfav!=null) {
                        for (Book favoritebook : bookadapter.mBooklistToBeRemoved) {
                            Log.v(TAG, "Removing book :" + favoritebook.getBooktitle() + " " + favoritebook.getIsbn());
                            finalUserfav.remove(favoritebook);
                        }
                    }
                    if(finalUserfav!=null) {
                        for (Book remainbooks : finalUserfav) {
                            Log.v(TAG, "Currently in list book :" + remainbooks.getBooktitle() + " " + remainbooks.getIsbn());
                            isbn = isbn + remainbooks.getIsbn() + ";";
                        }
                    }
                    if (isbn.equals("")){
                        isbn="";
                    }
                    else {
                        isbn.substring(0, isbn.length() - 1);
                    }
                    Log.v(TAG,"New isbn user =  "+isbn);
                    finalUsertoEdit.setUserisbn(isbn);

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("UserToEdit", finalUsertoEdit);
                    Log.v(TAG,"user sending to edit = "+finalUsertoEdit.getUsername());
                    fragment_editUser fragment_editUser = new fragment_editUser();
                    fragment_editUser.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(fragment_editUser,getActivity(),"EditUserBooks");
                }
            });
        }



        //jj-method that calls the searchbook method and runs it in background
        @Override
        protected Void doInBackground(String... urls) {
            try {
                searchbookbyisbn(urls[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            dialog.dismiss();
        }
    }



}
