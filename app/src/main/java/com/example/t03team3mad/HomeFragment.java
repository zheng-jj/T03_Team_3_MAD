package com.example.t03team3mad;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
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
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements AdapterGenreInHomeFragment.OnClickListener {
    Bitmap bitmap;
    private static final String TAG = "HomeFragment";
    List<Book> newbooklist;

    ArrayList<String> GenreList=new ArrayList<>();
    ArrayList<String> Ran5ToDisplay=new ArrayList<>();


    //jj- these are mainly to load the recyclerviews
    List<Book> booklist=new ArrayList<>();
    List<Book> booklist2=new ArrayList<>();
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    AdapterBookMain bookadapter;
    AdapterBookMain bookadapter2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Chris - genre recycler view
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        RecyclerView Genre=(RecyclerView)view.findViewById(R.id.genrelistrecyclerview);
        LinearLayoutManager genrelayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        Genre.setLayoutManager(genrelayout);
        LoadRandom5Genre();
        AdapterGenreInHomeFragment adapterGenreInHomeFragment =
                new AdapterGenreInHomeFragment(this.getContext(),Ran5ToDisplay,this);
        Genre.setAdapter(adapterGenreInHomeFragment);


        booklist=loadAllBooks();
        //load main popularbooks recyclerview
        RecyclerView popularbooks = (RecyclerView)view.findViewById(R.id.popularbookrecyclerview);

        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        popularbooks.setLayoutManager(llm);
        loadBookurlsfav();
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        bookadapter  = new AdapterBookMain(booklist,this.getContext());
        //jj- set the recyclerview object to its adapter
        popularbooks.setAdapter(bookadapter);



        AsyncTask<String, Void, ArrayList<Book>> task = new  APIaccessBookList(getContext()).execute("9780980200447");
        try {
            booklist2 = task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



        //load recommended books recyclerview
        //do the same for another recycler view recommendedbooks
        RecyclerView recommended = (RecyclerView) view.findViewById(R.id.recommendbookrecyclerview);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recommended.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        loadBookurlsreco();
        bookadapter2 = new AdapterBookMain(booklist2, this.getContext());

        //jj- set the recyclerview object to its adapter
        recommended.setAdapter(bookadapter2);


        return view;
    }

    //Chris - load all books into a listnewbooklist
    public List<Book> loadAllBooks()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> booklist = DBaccess.loadallbooklist();
        DBaccess.close();
        return booklist;
    }

    //Chris - get all the genre in the database
    public void LoadRandom5Genre()
    {
        //Chris - the 5 genres to display are different every time
        Ran5ToDisplay.clear();
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> bookList = DBaccess.loadallbooklist();
        DBaccess.close();
        //Chris - To seperate the long genre string into individual genre
        for(int i = 0; i<bookList.size(); i++)
        {
            String GenreListFormATable= bookList.get(i).getBookgenre();
            String[] ToGetIndividualGenre =GenreListFormATable.split(",");
            for (int a=0;a<ToGetIndividualGenre.length;a++)
            {
                if (!GenreList.contains(ToGetIndividualGenre[a])) {
                    GenreList.add(ToGetIndividualGenre[a]);
                }
            }
        }
        //Chris - Randomised the genre to be display
        for(int x=1;Ran5ToDisplay.size()<6;x++)
        {
            String Genre=GenreList.get(new Random().nextInt(GenreList.size()));
            if (!Ran5ToDisplay.contains(Genre))
                Ran5ToDisplay.add(Genre);
        }

    }



    //Chris - if user click on the genre on the recycler view
    @Override
    public void OnClick(int postion) {
        String Genre=Ran5ToDisplay.get(postion);
        Log.v(TAG,"Going to display books based on "+Genre);
        Bundle bundle = new Bundle();
        bundle.putString("Genre", Genre);  // Key, value
        Book_ByGenre nextFragment = new Book_ByGenre();  //will go the fragment where it will display all the books of that genre
        nextFragment.setArguments(bundle);
        MainActivity.addFragment(nextFragment,getActivity(),"BookByGenre");
    }


    //jj -  loads the url into book objects for recommended books
    public void loadBookurlsreco() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Book book : booklist2){
                        Log.v(TAG,"Bookloop="+book.getIsbn());
                        for(DocumentSnapshot doc : data){
                            Log.v(TAG,"Docloop="+doc.getReference().getId());
                            if(doc.getReference().getId().equals(book.getIsbn())){
                                book.setimglink(doc.getString("coverurl"));
                            }
                        }
                    }
                    bookadapter2.mBooklist=booklist2;
                    bookadapter2.notifyDataSetChanged();
                }
            }
        });
    }
    //jj -  loads the url into book objects for favouritebooks
    public void loadBookurlsfav() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Book book : booklist){
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
}
