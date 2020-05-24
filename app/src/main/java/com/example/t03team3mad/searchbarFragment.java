package com.example.t03team3mad;

import android.app.Activity;
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

import java.util.ArrayList;
import java.util.List;

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
        searchClassList.clear();
        System.out.println(query);
        //qh - opens database to create the object lists
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        List<Book> searchbooks = databaseAccess.searchbook(query);
        List<Author> searchauthor = databaseAccess.searchAuthor(query);
        List<User> searchUser = databaseAccess.searchUser(query);
        databaseAccess.close();
        //qh - adds name and description of book, author and user to arraylist
        for (Book var : searchbooks)
        {
            System.out.println(var.getBooktitle());
            SearchClass searchClass = new SearchClass(var.getBooktitle(),var.getBookabout(),"Book",String.valueOf(var.getIsbn()));
            searchClassList.add(searchClass);
        }
        for (Author var : searchauthor)
        {
            System.out.println(var.getAuthorname());
            SearchClass searchClass = new SearchClass(var.getAuthorname(),var.getAuthorabout(),"Author",String.valueOf(var.getAuthorid()));
            searchClassList.add(searchClass);
        }
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
        AdapterSearch searchadapter  = new AdapterSearch(searchClassList,this);
        searchresults.setAdapter(searchadapter);
    }

    //qh - when clicking search item
    @Override
    public void onSearchClick(int position) {
        SearchClass currentsearchobject = searchClassList.get(position);

        //qh -- if object clicked is a book
        if (currentsearchobject.getSearchClass() == "Book"){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            Book currentbook = databaseAccess.searchbookbyisbn(currentsearchobject.getId());
            databaseAccess.close();

            bookinfoFragment nextFrag= new bookinfoFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentbook", currentbook);  // Key, value
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();

        }
        // qh -- if object clicked is author
        if (currentsearchobject.getSearchClass() == "Author"){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            Author currentauthor = databaseAccess.searchauthorbyname(currentsearchobject.getSearchName());
            databaseAccess.close();

            authorprofileFragment nextFrag= new authorprofileFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("currentauthor", currentauthor);  // Key, value
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                    .addToBackStack(null)
                    .commit();
        }
        //qh -- if object clicked is user
        if (currentsearchobject.getSearchClass() == "User"){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            User currentuser = databaseAccess.searchuserbyid(currentsearchobject.getId());
            databaseAccess.close();

            fragment_user nextFrag= new fragment_user();
            Bundle bundle = new Bundle();
            bundle.putParcelable("searchuser", currentuser);  // Key, value
            nextFrag.setArguments(bundle);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainactivitycontainer, nextFrag, "findUser")
                    .addToBackStack(null)
                    .commit();

        }
    }


}







