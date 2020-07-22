package com.T03G3.eLibtheBookManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.T03G3.eLibtheBookManager.model.Book;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class Book_ByGenre extends Fragment implements AdaptorToViewBookBasedOnGenre.onclickListener {
    private static final String TAG = "SearchByGenreFragment";
    ArrayList<Book> BookByGenre = new ArrayList<>();
    Bundle mBundle;
    //layout
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_book__by_genre, container, false);
        //Chris - get the bundle
        mBundle = getArguments();
        String genre = mBundle.getString("Genre").trim();
        SearchGenre(genre);

        RecyclerView results = (RecyclerView) view.findViewById(R.id.GenreCardRecyclerView);
        LinearLayoutManager layout = new LinearLayoutManager(getActivity());
        results.setLayoutManager(layout);
        AdaptorToViewBookBasedOnGenre adapter = new AdaptorToViewBookBasedOnGenre(BookByGenre,this , this.getContext());
        results.setAdapter(adapter);

        return view;
    }

    //Chris - method to do the search
    public void SearchGenre(String genre) {
        BookByGenre.clear();

        AsyncTask<String, Void, ArrayList<Book>> tasktogetbook = new APIaccessSearchGenre().execute(genre);

        try {
            BookByGenre=tasktogetbook.get();
            if(BookByGenre!=null) {
                Log.v(TAG, "Added books");
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override //Chris - what will happened if user click the book
    public void onclick(int position) {

        Book CurrentBookObject = BookByGenre.get(position);
        bookinfoFragment nextFrag= new bookinfoFragment();
        Bundle bundle = new Bundle();
        //Chris - send book as object to bookinfo
        bundle.putParcelable("currentbook", CurrentBookObject);  // Key, value
        nextFrag.setArguments(bundle);
        MainActivity.addFragment(nextFrag,getActivity(),"BookInfo");
    }
}








