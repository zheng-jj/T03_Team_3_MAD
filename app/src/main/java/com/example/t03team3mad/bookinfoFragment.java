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

    private CollectionReference mCollectionRefbooks = FirebaseFirestore.getInstance().collection("Book");
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
        ImageView image = view.findViewById(R.id.imageView2);

        showrating = view.findViewById(R.id.showrate);
        Bundle bundle = this.getArguments();
        if (bundle.getParcelable("currentbook") != null) {
            final Book receivedbook = bundle.getParcelable("currentbook"); // Key
            isbn = receivedbook.getIsbn();
            getdata();
            viewcount(receivedbook.getIsbn());
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());

            title.setText(receivedbook.getBooktitle());
            synopsis.setText(receivedbook.getBookabout());
            releasedate.setText(receivedbook.getPdate());

            //qh - search author of book by id
            String authorid = receivedbook.getBookauthor();
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            try {
                Author authorbook = databaseAccess.searchauthorbyida(authorid);
                author.setText(authorbook.getAuthorname());
            } catch (Exception e) {
                author.setText(receivedbook.getBookauthor());
            }
            databaseAccess.close();


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


            //QH = SETS IMAGE FROM STRING
            String filename = "book" + receivedbook.getIsbn() + ".jpg";
            Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/" + filename);
            image.setImageBitmap(bmImg);


            //jj- Allows users to favourite the book
            final Button favourite = view.findViewById(R.id.favourite);
            final DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();

            //user favourite books loaded from firebase
            ArrayList<Book> userbooklist = new ArrayList<>();
            try {
                userbooklist = loaduserbooks(MainActivity.loggedinuser);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            DBaccess.close();

            final DatabaseAccess DBaccess2 = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess2.open();
            if(userbooklist!=null) {
                //searches to see if userbooklist containst this book
                Iterator<Book> itr = userbooklist.iterator();
                while (itr.hasNext()) {
                    Book book = itr.next();
                    if (book.getIsbn().equals(receivedbook.getIsbn())) {
                        favourite.setBackgroundResource(R.drawable.unliked);
                        break;
                    }
                }
            }
            //jo - get user id
            user = DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu()));
            Button review = view.findViewById(R.id.reviewpage);
            //jo - button to review page + send bundles
            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book", receivedbook);
                    Log.v(TAG,"book info sending data =  "+ receivedbook);
                    reviewpageFragment rpage = new reviewpageFragment();
                    rpage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(rpage,getActivity(),"reviewpage");
                }
            });
            //jo - button to addreview page + send bundles
            Button addreview = view.findViewById(R.id.addreview);
            addreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    bundle.putParcelable("book", receivedbook);
                    Log.v(TAG,"book info sending data =  "+ user);
                    fragment_addreview addrpage = new fragment_addreview();
                    addrpage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(addrpage,getActivity(),"addreviewPage");
                }
            });
            ArrayList<Book> Userbooklist = new ArrayList<>();
            if(userbooklist==null){
                Userbooklist = new ArrayList<>();
            }
            else {
                Userbooklist = userbooklist;
            }
            final ArrayList<Book>[] finalUserbooklist = new ArrayList[]{userbooklist};
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
                    ArrayList<String> isbnlist = new ArrayList<>();
                    if(finalUserbooklist[0] !=null) {
                        for (Book x : finalUserbooklist[0]) {
                            isbnlist.add(x.getIsbn());
                        }
                    }
                    else{
                        finalUserbooklist[0] = new ArrayList<>();
                    }
                    if(!isbnlist.contains(receivedbook.getIsbn())) {
                        //jj-adds book to list
                        finalUserbooklist[0].add(receivedbook);
                        for (Book x : finalUserbooklist[0]) {
                            isbn = isbn + x.getIsbn() + ';';
                        }
                        //removes the ";" at the end of isbn string
                        isbn.substring(0, isbn.length() - 1);
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
                    Intent ViewGetDetails= new Intent(getContext(),ViewGetDetails.class);
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

        mCollectionRefbooks.document(isbn).set(data);
    }
    public void getdata() {
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

                    calculateRatings();
                }
                else{
                    generaterecord();
                    calculateRatings();
                }
            }

        });




    }













}

