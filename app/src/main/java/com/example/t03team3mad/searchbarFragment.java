package com.example.t03team3mad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class searchbarFragment extends Fragment {
    private static final String TAG = "searchbarFragment";
    ListView listviewitem;

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
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<String> descriptionList = new ArrayList<>();
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
            arrayList.add(var.getBooktitle()+" (Book)");
            descriptionList.add(var.getBookabout());
            System.out.println(var.getBooktitle());
        }
        for (Author var : searchauthor)
        {
            arrayList.add(var.getAuthorname()+" (Author)");
            descriptionList.add(var.getAuthorabout());
            System.out.println(var.getAuthorname());
        }
        for (User var : searchUser)
        {
            arrayList.add(var.getUsername()+" (User)");
            descriptionList.add(var.getUserabout());
            System.out.println(var.getUsername());
        }
        //qh - puts all the results into recycler view to display
        RecyclerView searchresults = (RecyclerView)view.findViewById(R.id.searchrecycler);
        LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
        searchresults.setLayoutManager(searchlayout);
        AdapterSearch searchadapter  = new AdapterSearch(arrayList,descriptionList);
        searchresults.setAdapter(searchadapter);
    }
}







