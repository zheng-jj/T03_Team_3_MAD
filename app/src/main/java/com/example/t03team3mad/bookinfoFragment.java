package com.example.t03team3mad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class bookinfoFragment extends Fragment implements AdapterGenre.OnClickListener  {
    private static final String TAG = "bookinfoFragment";
    ArrayList<String>data = new ArrayList<>();
    RecyclerView Genre;
    int viewcount;
    String coverurl;
    int rating;
    int ratecount;
    TextView showrating;
    String isbn;
    ImageView image;
    Button addreview;
    private CollectionReference mCollectionRefbooks = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    //AdapterGenre adapter;



    @Override
    //qh - assigns the views and transfers the info to them
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_bookinfo, container, false);
        final User user;
        TextView title = view.findViewById(R.id.titleview);
        TextView synopsis = view.findViewById(R.id.synopsis);
        TextView releasedate = view.findViewById(R.id.releasedateview);
        Genre = view.findViewById(R.id.genreview_layout);
        TextView author = view.findViewById(R.id.authorview);
        image = view.findViewById(R.id.imageView2);
        Log.d(TAG, "UPLOADED BOOK TEST1222131232" );
        showrating = view.findViewById(R.id.showrate);
        addreview = view.findViewById(R.id.addreview);
        Bundle bundle = this.getArguments();
        if (bundle.getParcelable("currentbook") != null) {
            final Book receivedbook = bundle.getParcelable("currentbook"); // Key
            Log.d(TAG, "UPLOADED BOOK RECEIVEDdsdsdsdss" );
            Log.d(TAG,"ISBN: " +receivedbook.getIsbn());
            isbn = receivedbook.getIsbn();
            getdata(receivedbook);
            viewcount(receivedbook.getIsbn());
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());
            Log.d(TAG, "UPLOADED BOOK RECEIVED" + receivedbook.getBooktitle());
            title.setText(receivedbook.getBooktitle());
            synopsis.setText(receivedbook.getBookabout());
            releasedate.setText(receivedbook.getPdate());
            data.add(receivedbook.getBookgenre());
            //Chris - list the genre
            if(receivedbook.getBookgenre().contains(",")) {
            String[] splitgenre = receivedbook.getBookgenre().split(",");
            int i = 0;
            for (i = 0; i < splitgenre.length; i++) {
                data.add(splitgenre[i]);
                }
            }
            if(receivedbook.getBookgenre().contains(";")) {
                String[] genre = receivedbook.getBookgenre().split(";");
                int x = 0;
                for (x = 0; x < genre.length; x++) {
                    data.add(genre[x]);
                }
            }
            AdapterGenre mAdapter =
                    new AdapterGenre(data,this);

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);

            Genre.setLayoutManager(mLayoutManager);
            Genre.setItemAnimator(new DefaultItemAnimator());
            Genre.setAdapter(mAdapter);

            String path = null;
            try {
                path = getimage(receivedbook);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            File check = new File(path);
            int count = 20;
            while(count>0){
                if(check.exists()) {
                    image.setImageBitmap(BitmapFactory.decodeFile(path));
                    image.invalidate();
                    if(image.getDrawable() != null){
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        image.setImageBitmap(BitmapFactory.decodeFile(path));
                        break;
                    }
                    else {
                        continue;
                    }
                }
                else{
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count=count-1;
            }
            //QH = SETS IMAGE FROM STRING
            //String filename = "book" + receivedbook.getIsbn() + ".jpg";
            //Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/" + filename);
            //.setImageBitmap(bmImg);

            if(receivedbook.getimglink()== null||receivedbook.getimglink()==""){
                image.setImageResource(R.drawable.empty);
            }
            else {
                Picasso.with(this.getActivity()).load(coverurl).into(image);
            }
            Log.v(TAG,"loading image from "+receivedbook.getimglink());


            //jj- Allows users to favourite the book
            final Button favourite = view.findViewById(R.id.favourite);


            //searches to see if userbooklist containst this book
            if(MainActivity.loggedinuser.getUserisbn().indexOf(receivedbook.getIsbn())!=-1)
            {
                favourite.setBackgroundResource(R.drawable.unliked);
            }


            Log.v(TAG,MainActivity.loggedinuser.getUserisbn());
            final DatabaseAccess DBaccess2 = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess2.open();

            //jo - get user id
            user = DBaccess2.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu()));
            Button review = view.findViewById(R.id.reviewpage);
            //jo - button to review page + send bundles
            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book", receivedbook);
                    bundle.putParcelable("user", user);
                    Log.v(TAG,"book info sending data =  "+ receivedbook);
                    reviewpageFragment rpage = new reviewpageFragment();
                    rpage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(rpage,getActivity(),"reviewpage");
                }
            });
            //jo - button to addreview page + send bundles




            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                //jj-add book record to local db and firestore
                public void onClick(View v) {
                    String isbn="";
                    //does not work :(
//                    if(userbooklist.contains(CurrentBook)){
//                        Log.v(TAG,"Book list before change :");
//                        for(Book x : userbooklist)
//                        {
//                            Log.v(TAG,x.getIsbn());
//                        }
//                        //iterator used to remove book from collection
//                        Iterator<Book> itr = userbooklist.iterator();
//                        while (itr.hasNext())
//                        {
//                            Book book = itr.next();
//                            if (book.equals(CurrentBook))
//                            {
//                                userbooklist.remove(book);
//                                Log.v(TAG,"Removed: "+book.getIsbn());
//                            }
//                        }
//                        Log.v(TAG,"Book list after change :");
//                        for(Book x : userbooklist)
//                        {
//                            Log.v(TAG,x.getIsbn());
//                            isbn=isbn+x.getIsbn()+';';
//                        }
//                        if(isbn.equals("")){
//                            isbn=null;
//                        }
//                        else {
//                            //removes the ";" at the end of isbn string
//                            isbn.substring(0, isbn.length() - 1);
//                        }
//
//                        favourite.setText("Add to Favourites");
//                    }

                    if(MainActivity.loggedinuser.getUserisbn().indexOf(receivedbook.getIsbn())==-1)
                    {
                        String[] newlist = MainActivity.loggedinuser.getUserisbn().split(";");
                        for(String x: newlist){
                            isbn=isbn+x+";";
                        }
                        favourite.setBackgroundResource(R.drawable.unliked);
                        //jj-adds book to list
                        isbn=isbn+receivedbook.getIsbn()+";";
                        favourite.setBackgroundResource(R.drawable.unliked);

                        //removes duplicates
                        List<String> isbns = new ArrayList<>(Arrays.asList(isbn.split(";")));
                        Set<String> set = new LinkedHashSet<>(isbns);
                        isbns.clear();
                        isbns.addAll(set);
                        isbn = "";
                        for (String x : isbns) {
                            isbn = isbn + x + ";";
                        }
                        if (isbn.equals("")) {
                            isbn = null;
                        } else {
                            isbn.substring(0, isbn.length() - 1);
                        }
                        Log.v(TAG,"previous isbn ="+MainActivity.loggedinuser.getUserisbn());
                        Log.v(TAG, "new isbn string added to db=" + isbn);
                        //updates user data in database
                        MainActivity.loggedinuser.setUserisbn(isbn);
                        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                        DBaccess.open();
                        DBaccess.editUserData(MainActivity.loggedinuser);
                        DBaccess.close();
                        if (MainActivity.loggedinuser.getUserisbn() == null) {
                            MainActivity.loggedinuser.setUserisbn("");
                        }
                        //jj-updates firestore
                        AsyncTask<User, Void, Void> tasktoupdateUser = new updateFireStoreUser.AccessUser().execute(MainActivity.loggedinuser);
                        try {
                            tasktoupdateUser.get();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.v(TAG, "-==========-");
                    }
                }
            });
            Button getBook = view.findViewById(R.id.getBook);
            getBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle passdata = new Bundle();
                    passdata.putString("User_UID",String.valueOf(MainActivity.loggedinuser.getUseridu()));
                    passdata.putParcelable("book",receivedbook);
                    Intent ViewGetDetails= new Intent(getContext(),ViewToGet.class);
                    ViewGetDetails.putExtra("Bundle", passdata);
                    Log.v(TAG,"sending this book to view where to get "+receivedbook.getBooktitle());
                    startActivity(ViewGetDetails);
                }
            });

        }
        return view;
    }



    //Chris - For Genre to send genre name to search book by genre
    @Override
    public void OnClick(int postion) {
        String Genre=data.get(postion);
        Log.v(TAG,"Going to display books based on "+Genre.trim());
        Bundle bundle = new Bundle();
        bundle.putString("Genre", Genre);  // Key, value
        Book_ByGenre nextFragment = new Book_ByGenre();
        nextFragment.setArguments(bundle);
        MainActivity.addFragment(nextFragment,getActivity(),"BookByGenre");
    }
    //jj - gets the user's favourite books
    public ArrayList<Book> loaduserbooks(User user) throws ExecutionException, InterruptedException {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        //DBaccess.open();
        ArrayList<Book> userbooklist = new ArrayList<>();
//        try {
//            //foreach book in user's local db favourited books, get it from the api
//            for(Book book:DBaccess.loaduserbooklist(DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu())))) {
//                AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(book.getIsbn());
//                try {
//                    Book temp = tasktogetbook.get();
//                    if (temp != null) {
//                        Log.v(TAG, "Book created = " + temp.getBooktitle());
//                        Log.v(TAG, "Book isbn = " + temp.getIsbn());
//                        Log.v(TAG, "Book about = " + temp.getBookabout());
//                        Log.v(TAG, "Book date = " + temp.getPdate());
//                        Log.v(TAG, "Book genre = " + temp.getBookgenre());
//                        Log.v(TAG, "Book author = " + temp.getBookauthor());
//                        userbooklist.add(temp);
//                    }
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e){}
//        DBaccess.close();

        AsyncTask<String, Void,ArrayList<Book>> task = new APIaccessBookList(getContext()).execute(user.getUserisbn());

        userbooklist=task.get();
        Log.v(TAG,"fav book list is loaded");
        return userbooklist;

    }
    public void viewcount(String isbn){


        mCollectionRefbooks.document(isbn).update("viewcount", FieldValue.increment(1));
    }
    public void calculateRatings(){
        String showratings;
        Log.d("Test","Calulcateratings ratecount: " +ratecount);
        Log.d("Test","Calulcateratings ratecount: " +rating);
        if(ratecount == 0){
            showratings = "Ratings unavailable";
        }
        else{
            float temp = rating/ratecount;
            showratings = String.valueOf(temp);
            Log.d("Test","Calulcateratings ratecount: " +showratings);
        }


        showrating.setText(showratings);

    }
    public void generaterecord(){
        Map<String, Object> data = new HashMap<String,Object>();

        data.put("viewcount", Long.valueOf(1));
        data.put("TotalRating", Long.valueOf(0));
        data.put("ratecount", Long.valueOf(0));
        data.put("coverurl","");
        data.put("uploaded",false);

        mCollectionRefbooks.document(isbn).set(data);
    }
    public void getdata(final Book receivedbook) {
        mCollectionRefbooks.document(isbn).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    viewcount = documentSnapshot.getLong("viewcount").intValue();
                    rating = documentSnapshot.getLong("TotalRating").intValue();
                    ratecount = documentSnapshot.getLong("ratecount").intValue();
                    coverurl = documentSnapshot.getString("coverurl");
                    Log.d("FireStore","Viewcount :" + viewcount);
                    Log.d("FireStore","rating :" + rating);
                    Log.d("FireStore","ratecount :" + ratecount);
                    Log.d("FireStore","url :" + coverurl);
                    setimage(receivedbook);

                    calculateRatings();
                }
                else{
                    generaterecord();
                    calculateRatings();
                }
            }

        });


    }
    public String getimage(Book book) throws ExecutionException, InterruptedException {
        String filename = "book" +book.getIsbn() +".jpg";
        //jj gets image from firebase and saves to local storage
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
    }

    public void setimage(Book receivedbook) {
        if(coverurl == null || coverurl == ""){
            image.setImageResource(R.drawable.empty);
        }
        else {
            Picasso.with(this.getActivity()).load(coverurl).into(image);
            Log.v(TAG,"THIS IS BOOK URL"+ coverurl);
            Log.v(TAG, "THIS IS BOOK URL" +coverurl);
            Log.v(TAG,"THIS IS BOOK URL" + receivedbook.getimglink());
            Log.v(TAG,"THIS IS BOOK URL" + receivedbook.getimglink());
        }
    }
    public void addrevew(final Book receivedbook){
        mCollectionRefuser.document(String.valueOf(MainActivity.loggedinuser.getUseridu())).collection("Reviews").whereEqualTo("isbn",isbn).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    addreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Bundle bundle = new Bundle();

                            bundle.putParcelable("book", receivedbook);

                            fragment_addreview addrpage = new fragment_addreview();
                            addrpage.setArguments(bundle);
                            //jj-updated the way we add fragments into the view
                            MainActivity.addFragment(addrpage,getActivity(),"addreviewPage");
                        }
                    });
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"You have already reviewed this book ",Toast.LENGTH_LONG).show();

                }

            }
        });

        
    }















}

