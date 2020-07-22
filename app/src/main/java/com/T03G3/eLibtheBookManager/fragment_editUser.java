package com.T03G3.eLibtheBookManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.T03G3.eLibtheBookManager.model.Book;
import com.T03G3.eLibtheBookManager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class fragment_editUser extends Fragment {
    private static final String TAG = "userEditFragment";
    List<Book> userBooklist = null;
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    private static final int RESULTLOADIMAGE=1;
    AdapterBookMain bookadapter;
    ArrayList<Book> userfav = new ArrayList<>();
    ImageButton Pic;
    RecyclerView favouritebooks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_editprofile,container,false);
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }
        //jj - loads user into layout
        if(usertoEdit!=null) {
            loaduserintoview(view, usertoEdit);
        }
        Pic=view.findViewById(R.id.newprofileimg);

        //jj - load favourite user books recyclerview
        favouritebooks = (RecyclerView) view.findViewById(R.id.favBooksRecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        bookadapter = new AdapterBookMain(userfav, getContext());
        favouritebooks.setAdapter(bookadapter);


        AsyncTask<String, Void, Void> task = new fragment_editUser.APIaccessBookListUser(getContext()).execute(usertoEdit.getUserisbn());
        try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadBookurlsbooks();


        bookadapter = new AdapterBookMain(userfav, this.getContext());
        if(userfav!=null) {
            //jj- set the recyclerview object to its adapter
            favouritebooks.setAdapter(bookadapter);
        }

        //onclick listener for button to edit user favourite books
        Button editBooks = view.findViewById(R.id.allfavbooks);
        final User finalUsertoEdit = usertoEdit;
        //brings user to edit all user books fragment
        editBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_editUserBooks fragment_editUserBooks = new fragment_editUserBooks();
                Bundle bundle = new Bundle();
                bundle.putParcelable("UserToEdit", finalUsertoEdit);
                Log.v(TAG,"user sending to editBooks = "+ finalUsertoEdit.getUsername());
                fragment_editUserBooks.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment_editUserBooks,getActivity(),"editUserBooks");
            }
        });

        //jj-edits user and refreshes the fragment
        Button saveEdits = view.findViewById(R.id.savechanges);
        final User finalUsertoEdit1 = usertoEdit;
        saveEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mainview = v.getRootView();
                EditText Name = mainview.findViewById(R.id.editName);
                EditText Desc = mainview.findViewById(R.id.editDes);
                finalUsertoEdit1.setUsername(Name.getText().toString());
                finalUsertoEdit1.setUserabout(Desc.getText().toString());
                Log.v(TAG,"new user name: "+finalUsertoEdit1.getUsername()+" New user desc: "+finalUsertoEdit1.getUserabout());
                User todatabase = finalUsertoEdit1;
                if(todatabase.getUserisbn()==""){
                    todatabase.setUserisbn(null);
                }
                //jj-updates database on new user details
                DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                DBaccess.open();
                DBaccess.editUserData(todatabase);
                DBaccess.close();
                if(finalUsertoEdit.getUserisbn()==null){
                    finalUsertoEdit.setUserisbn("");
                }
                //updates firestore
                AsyncTask<User,Void,Void> tasktoupdateUser = new updateFireStoreUser.AccessUser().execute(finalUsertoEdit1);
                try {
                    tasktoupdateUser.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //reloads the fragment
                fragment_user fragment = new fragment_user();

                //updates global variable
                MainActivity.loggedinuser=finalUsertoEdit1;

                //updates database on new image
                Bitmap userprofileimg = ((BitmapDrawable) Pic.getDrawable()).getBitmap();
                //saves image from gallery to internal storage
                try {
                    SaveBitmap(userprofileimg,Integer.toString(finalUsertoEdit1.getUseridu()));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //jj- bundle to be moved to fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable("loggedin", finalUsertoEdit1);
                fragment.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment,getActivity(),"UserFragment");
            }
        });

        //jj-upload image from gallery
        Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jj-creates a new intent to PICK image from the media folder
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //jj-when gallery intent is started, we need to get back a result, hence use startActivityforresult instead of start activity
                startActivityForResult(gallery, RESULTLOADIMAGE);
            }
        });
        return view;
    }
    //jj-method which is called when the user selects image from gallery(startActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checks if the gallery intent is the one who called this method, and if data returned is not null, and also checks if result code is correct(user successfully select image)
        if(requestCode == RESULTLOADIMAGE && data!=null && resultCode==Activity.RESULT_OK ){
            Uri selected = data.getData();
            //sets picture to imagebutton widget
            Pic.setImageURI(selected);
        }
    }
    //jj- used to save a bitmap to internal storage
    private void SaveBitmap(Bitmap bitmap, String uid) throws ExecutionException, InterruptedException {
        FileOutputStream outStream = null;
        try {
            File directory = new File("/data/data/com.T03G3.eLibtheBookManager/app_imageDir");
            directory.mkdirs();
            //image saved name
            String fileName = "user"+uid+".jpg";
            //creates the file
            File filetosave = new File(directory, fileName);
            //saves the file
            outStream = new FileOutputStream(filetosave);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            //jj-media scanner is used to scan all media files without having to restart application
            MediaScannerConnection.scanFile(getActivity(), new String[] { "/data/data/com.T03G3.eLibtheBookManager/app_imageDir" }, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



        //saves bitmap to firestore
        String filename = "user" +uid +".jpg";
        imageTaskSaveParameters parameters = new imageTaskSaveParameters(filename,bitmap);
        //jj gets image from firebase and saves to local storage
        AsyncTask<imageTaskSaveParameters, Void, Void> task = new FirebaseStorageImageSave().execute(parameters);
        task.get();
    }

    //jj - Loads the user information into the layout
    public void loaduserintoview(View view, User user){
        ImageButton Pic = view.findViewById(R.id.newprofileimg);
        EditText Name = view.findViewById(R.id.editName);
        EditText Desc = view.findViewById(R.id.editDes);
        Log.v(TAG,"current user name= "+user.getUsername());
        Log.v(TAG,"current user dec= "+user.getUserabout());
        Name.setText(user.getUsername());
        Desc.setText(user.getUserabout());
        String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.T03G3.eLibtheBookManager/app_imageDir/"+filename);
        Pic.setImageBitmap(bmImg);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    //jj loads the url into book objc
    public void loadBookurlsbooks() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                    if(isbntosearch.equals(null)||isbntosearch.equals("")) {
                        continue;
                    }
                    final Boolean[] uploaded = {false};
                    Log.v(TAG,"ISBn="+isbntosearch);
                    FirebaseFirestore.getInstance().collection("Book").document(isbntosearch).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.getBoolean("uploaded")) {

                                    uploaded[0] =true;
                                    Book toadd = new Book();
                                    toadd.setratng(documentSnapshot.getLong("TotalRating").intValue());
                                    toadd.setBookabout(documentSnapshot.getString("bookabout"));
                                    toadd.setBookauthor(documentSnapshot.getString("bookauthor"));
                                    toadd.setBookgenre(documentSnapshot.getString("bookgenre"));
                                    toadd.setPdate(documentSnapshot.getString("bookpdate"));
                                    toadd.setBooktitle(documentSnapshot.getString("booktitle"));
                                    toadd.setIsbn(documentSnapshot.getId());
                                    userfav.add(toadd);
                                } else {

                                }
                            } else {
                            }
                        }
                    });
                    //jj- if book is not uploaded, search api
                    if (!uploaded[0]) {
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
                            } else {
                                Book x = new Book("Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available");
                                userfav.add(x);
                            }
                        }
                    }
                }
            }
            loadBookurlsbooks();

            bookadapter.mBooklist=userfav;
            bookadapter.notifyDataSetChanged();

        }
        private Exception exception;

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
