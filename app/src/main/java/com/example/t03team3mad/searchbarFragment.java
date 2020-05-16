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

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;

import java.util.ArrayList;
import java.util.List;

public class searchbarFragment extends Fragment {
    private static final String TAG = "searchbarFragment";
    ListView listviewitem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_searchbar,container,false);
        SearchView searchbar = view.findViewById(R.id.searchView);
        listviewitem = view.findViewById(R.id.listView);
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
        }
    //qh - takes the search query and displays them (currently only display results for books
    public void doMySearch(String query){
        ArrayList<String> arrayList = new ArrayList<>();
        System.out.println(query);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        List<Book> searchbooks = databaseAccess.searchbook(query);
        databaseAccess.close();
        for (Book var : searchbooks)
        {
            arrayList.add(var.getBooktitle()+" \n(Book)");
            System.out.println(var.getBooktitle());
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,arrayList);
        listviewitem.setAdapter(arrayAdapter);
    }


}







