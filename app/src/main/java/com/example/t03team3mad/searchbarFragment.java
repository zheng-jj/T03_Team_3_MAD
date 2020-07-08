package com.example.t03team3mad;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class searchbarFragment extends Fragment implements AdapterSearch.OnSearchListener {
    private static final String TAG = "searchbarFragment";
    ListView listviewitem;
    List<SearchClass> searchClassList = new ArrayList<SearchClass>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_searchbar,container,false);
        SearchView searchbar = view.findViewById(R.id.searchView);
        //qh - simple search
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query, view);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
        }
    //qh - takes the search query and displays them (can search for author , books and users)
    public void doMySearch(String query, View view){
        if (searchClassList.isEmpty()){

        }
        else{
            searchClassList.clear();
        }
        System.out.println(query);
        //qh - opens database to create the object lists
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        //List<Book> searchbooks = databaseAccess.searchbook(query);
        List<Author> searchauthor = databaseAccess.searchAuthor(query);
        List<User> searchUser = databaseAccess.searchUser(query);
        databaseAccess.close();
        //qh - adds name and description of book, author and user to arraylist
        //qh - i converted them into searchclass because i wanted to show 3 types of objects. search class has the object type, id, name and description.
        //for (Book var : searchbooks)
        //{
           // System.out.println(var.getBooktitle());
          //  SearchClass searchClass = new SearchClass(var.getBooktitle(),var.getBookabout(),"Book",String.valueOf(var.getIsbn()));
          //  searchClassList.add(searchClass);
      //  }

        //IMPORTANT: THIS IS HOW TO USE THE API CREATED BOOKS
        AsyncTask<String, Void, List<SearchClass>> searchapiforbooks = new APIaccessSearchBookTitle().execute(query);
        try {
            searchClassList = searchapiforbooks.get();
            if(searchClassList!=null) {
                Log.v(TAG, "Added Searches");
            };
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        
        List<SearchClass> toremove = new ArrayList<>();
        //qh- to prevent books from showing up that dont have information at all
        for (SearchClass i : searchClassList){
            Book newbook = null;
            AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(i.getId());
            try {
                newbook = tasktogetbook.get();
                if(newbook!=null) {
                    Log.v(TAG, "Book created = " + newbook.getBooktitle());
                    Log.v(TAG, "Book isbn = " + newbook.getIsbn());
                    Log.v(TAG, "Book about = " + newbook.getBookabout());
                    Log.v(TAG, "Book date = " + newbook.getPdate());
                    Log.v(TAG, "Book genre = " + newbook.getBookgenre());
                    Log.v(TAG, "Book author = " + newbook.getBookauthor());
                }
                else{
                    toremove.add(i);
                };
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        searchClassList.removeAll(toremove);

        //for (Author var : searchauthor)
        //{
           // System.out.println(var.getAuthorname());
           // SearchClass searchClass = new SearchClass(var.getAuthorname(),var.getAuthorabout(),"Author",String.valueOf(var.getAuthorid()));
           // searchClassList.add(searchClass);
        //}
        for (User var : searchUser)
        {
            System.out.println(var.getUsername());
            SearchClass searchClass = new SearchClass(var.getUsername(),var.getUserabout(),"User",String.valueOf(var.getUseridu()));
            searchClassList.add(searchClass);
        }
        //qh - puts all the results into recycler view to display
        RecyclerView searchresults = (RecyclerView)view.findViewById(R.id.searchrecycler);
        LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
        searchresults.setLayoutManager(searchlayout);
        AdapterSearch searchadapter  = new AdapterSearch(searchClassList,this, this.getContext());
        searchresults.setAdapter(searchadapter);
    }

    //qh - when clicking search item
    @Override
    public void onSearchClick(int position) {
        SearchClass currentsearchobject = searchClassList.get(position);

        //qh -- if object clicked is a book
        //qh - added jjs code to transfer info
        Book currentbook = null;
        if (currentsearchobject.getSearchClass().equals("Book")){
            AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(currentsearchobject.getId());
            final ArrayList<Book> booklist2=new ArrayList<>();
            try {
                currentbook = tasktogetbook.get();
                if(currentbook!=null) {
                    Log.v(TAG, "Book created = " + currentbook.getBooktitle());
                    Log.v(TAG, "Book isbn = " + currentbook.getIsbn());
                    Log.v(TAG, "Book about = " + currentbook.getBookabout());
                    Log.v(TAG, "Book date = " + currentbook.getPdate());
                    Log.v(TAG, "Book genre = " + currentbook.getBookgenre());
                    Log.v(TAG, "Book author = " + currentbook.getBookauthor());
                };
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bookinfoFragment nextFrag= new bookinfoFragment();
            Bundle bundle = new Bundle();
            //qh - bring the object into book
            bundle.putParcelable("currentbook", currentbook);  // Key, value
            nextFrag.setArguments(bundle);
            MainActivity.addFragment(nextFrag,getActivity(),"findThisFragment"+currentsearchobject.getSearchName());

        }
        // qh -- if object clicked is author
        //if (currentsearchobject.getSearchClass().equals("Author")){
           // DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
           // databaseAccess.open();
           // Author currentauthor = databaseAccess.searchauthorbyida(currentsearchobject.getId());
           // databaseAccess.close();

           // authorprofileFragment nextFrag= new authorprofileFragment();
           // Bundle bundle = new Bundle();
           // bundle.putParcelable("currentauthor", currentauthor);  // Key, value
           // nextFrag.setArguments(bundle);
            //jj-updated the way we add fragments into the view
           // MainActivity.addFragment(nextFrag,getActivity(),"findThisFragment"+currentsearchobject.getSearchName());
        //}
        //qh -- if object clicked is user
            if (currentsearchobject.getSearchClass().equals("User")){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            User currentuser = databaseAccess.searchuserbyid(currentsearchobject.getId());
            databaseAccess.close();

            fragment_user nextFrag= new fragment_user();
            Bundle bundle = new Bundle();
            bundle.putParcelable("searchuser", currentuser);  // Key, value

            nextFrag.setArguments(bundle);//jj-updated the way we add fragments into the view
            MainActivity.addFragment(nextFrag,getActivity(),"findUser"+currentsearchobject.getSearchName());
        }
    }
}







