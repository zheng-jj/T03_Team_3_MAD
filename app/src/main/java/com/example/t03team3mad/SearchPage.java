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

                DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
                databaseAccess.open();
                String authorname = databaseAccess.getElement("NAME","Author","Name",query);
                if (authorname != null){
                    String authorid = databaseAccess.getElement("ID","Author","Name",query);
                    int aid = Integer.parseInt(authorid);
                    String authorabout = databaseAccess.getElement("ABOUT","Author","Name",query);
                    Author author1 = new Author(aid, authorname, authorabout);
                    System.out.println("works");
                }

                databaseAccess.close();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }
}
