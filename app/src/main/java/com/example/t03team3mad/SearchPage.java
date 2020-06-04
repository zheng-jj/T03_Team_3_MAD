package com.example.t03team3mad;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.t03team3mad.model.Author;

public class SearchPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SearchView searchbar;
        String searchtext;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_searchbar);
        searchbar = (SearchView) findViewById(R.id.searchView);
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
    }
    public void doMySearch(String query){
        System.out.println(query);
    }



}
