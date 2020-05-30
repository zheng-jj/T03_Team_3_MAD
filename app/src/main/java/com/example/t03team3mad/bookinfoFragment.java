package com.example.t03team3mad;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

import com.example.t03team3mad.model.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class bookinfoFragment extends Fragment {
    private static final String TAG = "bookinfoFragment";
    ArrayList<String>data = new ArrayList<>();
    RecyclerView recyclerView;
    //AdapterGenre adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookinfo, container, false);
        TextView title = view.findViewById(R.id.titleview);
        TextView synopsis = view.findViewById(R.id.synopsis);
        TextView releasedate = view.findViewById(R.id.releasedateview);
        RecyclerView Genre = view.findViewById(R.id.genreview);
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
            String[] splitgenre = receivedbook.getBookgenre().split(";");
            int i = 0;
            for (i = 0; i < splitgenre.length; i++) {
                data.add(splitgenre[i]);
            }

            //adapter = new AdapterGenre(data);

//            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
//            //Chris - set the recyclerview's manager to the previously created manager
//            recyclerView.setLayoutManager(llm);
//            recyclerView.setItemAnimator(new DefaultItemAnimator());
//            //recyclerView.setAdapter(adapter);
//            author.setText(receivedbook.getBookauthor());

            //QH = SETS IMAGE FROM STRING
            String filename = "book" + receivedbook.getIsbn() + ".jpg";
            Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/" + filename);
            image.setImageBitmap(bmImg);


            //jj- Allows users to favourite the book
            final Button favourite = view.findViewById(R.id.favourite);
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();
            final List<Book> userbooklist = DBaccess.loaduserbooklist(MainActivity.loggedinuser);
            DBaccess.close();
            for (Book book : userbooklist) {
                if (book.getIsbn().equals(receivedbook.getIsbn())) {
                    favourite.setText("Unfavourite this book");
                }
            }
            favourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //jj-clears user isbn
//                    MainActivity.loggedinuser.setUserisbn(null);
//                    DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
//                    DBaccess.open();
//                    DBaccess.editUserData(MainActivity.loggedinuser);
//                    DBaccess.close();
                    if(favourite.getText().equals("Unfavourite this book")){
                        //jj edits the isbn of the userloggedin in main activity(global variable)
                        userbooklist.remove(receivedbook);
                        String isbn = "";
                        for(Book remainbooks : userbooklist){
                            Log.v(TAG,"Currently in list book :"+remainbooks.getBooktitle() + " "+remainbooks.getIsbn());
                            isbn=isbn+remainbooks.getIsbn()+";";
                        }
                        if (isbn.equals("")){
                            isbn=null;
                        }
                        else {
                            isbn.substring(0, isbn.length() - 1);
                        }
                        //jj-updates new user isbn
                        MainActivity.loggedinuser.setUserisbn(isbn);
                        DatabaseAccess DBaccess2 = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                        DBaccess2.open();
                        DBaccess2.editUserData(MainActivity.loggedinuser);
                        DBaccess2.close();
                        favourite.setText("Favourite this book");
                    }
                    else {
                        //jj-if user didnt like this book previously
                        userbooklist.add(receivedbook);
                        String isbn = "";
                        for(Book remainbooks : userbooklist){
                            Log.v(TAG,"Currently in list book :"+remainbooks.getBooktitle() + " "+remainbooks.getIsbn());
                            isbn=isbn+remainbooks.getIsbn()+";";
                        }
                        //removes the ";" at the end
                        isbn.substring(0, isbn.length() - 1);

                        MainActivity.loggedinuser.setUserisbn(isbn);
                        //updates new user isbn
                        DatabaseAccess DBaccess2 = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                        DBaccess2.open();
                        DBaccess2.editUserData(MainActivity.loggedinuser);
                        DBaccess2.close();
                        favourite.setText("Unfavourite this book");
                    }
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
}

