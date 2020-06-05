package com.example.t03team3mad;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class bookinfoFragment extends Fragment implements AdapterGenre.OnClickListener {
    private static final String TAG = "bookinfoFragment";
    ArrayList<String>data = new ArrayList<>();
    RecyclerView Genre;
    //AdapterGenre adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final User user;
        View view = inflater.inflate(R.layout.fragment_bookinfo, container, false);
        TextView title = view.findViewById(R.id.titleview);
        TextView synopsis = view.findViewById(R.id.synopsis);
        TextView releasedate = view.findViewById(R.id.releasedateview);
        Genre = view.findViewById(R.id.genreview_layout);
        TextView author = view.findViewById(R.id.authorview);
        ImageView image = view.findViewById(R.id.imageView2);

        Bundle bundle = this.getArguments();
        if (bundle.getParcelable("currentbook") != null) {
            final Book receivedbook = bundle.getParcelable("currentbook"); // Key
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
            Author authorbook = databaseAccess.searchauthorbyida(authorid);
            databaseAccess.close();

            author.setText(authorbook.getAuthorname());

            String[] splitgenre = receivedbook.getBookgenre().split(",");
            int i = 0;
            for (i = 0; i < splitgenre.length; i++) {
                data.add(splitgenre[i]);
            }
            AdapterGenre mAdapter =
                    new AdapterGenre(data,this);

            LinearLayoutManager mLayoutManager =
                    new LinearLayoutManager(this.getContext());

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
            DBaccess.loaduserbooklist(DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu())));
            final List<Book> userbooklist = new ArrayList<>();
            for(Book book:DBaccess.loaduserbooklist(DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu())))){
                userbooklist.add(DBaccess.searchbookbyisbn(book.getIsbn()));
            }

            final String Currentisbn = receivedbook.getIsbn();
            DBaccess.close();
            final DatabaseAccess DBaccess2 = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess2.open();
            final Book CurrentBook = DBaccess.searchbookbyisbn(Currentisbn);
            //searches to see if userbooklist containst this book
            Iterator<Book> itr = userbooklist.iterator();
            while (itr.hasNext())
            {
                Book book = itr.next();
                if (book.getIsbn().equals(CurrentBook.getIsbn()))
                {
                    favourite.setText("Added to list");
                }
            }
            user = DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu()));
            Button review = view.findViewById(R.id.reviewpage);
            review.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("book", CurrentBook);
                    Log.v(TAG,"book info sending data =  "+ CurrentBook);
                    reviewpageFragment rpage = new reviewpageFragment();
                    rpage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(rpage,getActivity(),"reviewpage");
                }
            });
            Button addreview = view.findViewById(R.id.addreview);
            addreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("user", user);
                    bundle.putParcelable("book", CurrentBook);
                    Log.v(TAG,"book info sending data =  "+ user);
                    fragment_addreview addrpage = new fragment_addreview();
                    addrpage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(addrpage,getActivity(),"addreviewPage");
                }
            });

            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
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
                    if(!userbooklist.contains(CurrentBook)){
                        Log.v(TAG,"Book list before change :");
                        for(Book x : userbooklist)
                        {
                            Log.v(TAG,x.getIsbn());
                        }
                        userbooklist.add(CurrentBook);
                        Log.v(TAG,"Book list after change :");
                        for(Book x : userbooklist)
                        {
                            Log.v(TAG,x.getIsbn());
                            isbn=isbn+x.getIsbn()+';';
                        }
                        //removes the ";" at the end of isbn string
                        isbn.substring(0, isbn.length() - 1);
                        favourite.setText("Added to list");
                    }

                    //removes duplicates
                    List<String> isbns = new ArrayList<>(Arrays.asList(isbn.split(";")));
                    Set<String> set = new LinkedHashSet<>(isbns);
                    isbns.clear();
                    isbns.addAll(set);
                    isbn="";
                    for(String x:isbns){
                        isbn=isbn+x+";";
                    }
                    if(isbn.equals(""))
                    {
                        isbn=null;
                    }
                    else {
                        isbn.substring(0, isbn.length() - 1);
                    }
                    Log.v(TAG,"new isbn string added to db="+isbn);
                    //updates user data in database
                    MainActivity.loggedinuser.setUserisbn(isbn);
                    DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                    DBaccess.open();
                    DBaccess.editUserData(MainActivity.loggedinuser);
                    DBaccess.close();
                    Log.v(TAG,"-==========-");
                }
            });
        }
        return view;
    }

    //qh -- the book object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
//For Genre to send genre name to search book by genre
    @Override
    public void OnClick(int postion) {
        String Genre=data.get(postion);
        Log.v(TAG,Genre);
        Bundle bundle = new Bundle();
        bundle.putString("Genre", Genre);  // Key, value
        Book_ByGenre nextFragment = new Book_ByGenre();
        nextFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFragment, "findThisgenre")
                .addToBackStack(null)
                .commit();
    }
}

