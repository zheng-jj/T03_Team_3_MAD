package com.example.t03team3mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import java.util.ArrayList;
import java.util.List;

public class Book_ByGenre extends Fragment implements AdaptorToViewBookBasedOnGenre.onclickListener {
    private static final String TAG = "SearchByGenreFragment";
    List<SearchClass> searchClassList = new ArrayList<SearchClass>();
    Bundle mBundle;
    //layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_book__by_genre, container, false);

        mBundle = getArguments();
        String query = mBundle.getString("Genre").trim();
        //Chris - do a search based on the genre pressed
        doMySearch(query);

        RecyclerView searchresults = (RecyclerView) view.findViewById(R.id.GenreCardRecyclerView);
        LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
        searchresults.setLayoutManager(searchlayout);
        AdaptorToViewBookBasedOnGenre searchadapter = new AdaptorToViewBookBasedOnGenre(searchClassList,this , this.getContext());
        searchresults.setAdapter(searchadapter);

        return view;
    }

    //method to do the search
    public void doMySearch(String query) {
        searchClassList.clear();
        System.out.println(query);
        //opens database to create the object lists
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        List<Book> searchbooks = databaseAccess.searchgenre(query);
        databaseAccess.close();
        //adds name and description of book, author and user to arraylist
        for (Book var : searchbooks) {
            System.out.println(var.getBooktitle());
            SearchClass searchClass = new SearchClass(var.getBooktitle(), var.getBookabout(), "Book", String.valueOf(var.getIsbn()));
            searchClassList.add(searchClass);


        }


    }

    @Override //what will happened if user click the book
    public void onclick(int position) {

        SearchClass currentsearchobject = searchClassList.get(position);
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        //qh searching book by isbn
        databaseAccess.open();
        Book currentbook = databaseAccess.searchbookbyisbn(currentsearchobject.getId());
        databaseAccess.close();

        bookinfoFragment nextFrag= new bookinfoFragment();
        Bundle bundle = new Bundle();
        //qh - bring the object into book
        bundle.putParcelable("currentbook", currentbook);  // Key, value
        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                .addToBackStack("bookSearch")
                .commit();
    }
}








