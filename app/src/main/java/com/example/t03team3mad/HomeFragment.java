package com.example.t03team3mad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;

import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        //load main popularbooks recyclerview
        RecyclerView popularbooks = (RecyclerView)view.findViewById(R.id.popularbookrecyclerview);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        popularbooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter  = new AdapterBookMain(loadAllBooks());
        //jj- set the recyclerview object to its adapter
        popularbooks.setAdapter(bookadapter);

        //load recommended books recyclerview
        //do the same for another recycler view recommendedbooks
        RecyclerView recommended = (RecyclerView)view.findViewById(R.id.recommendbookrecyclerview);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recommended.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter2  = new AdapterBookMain(loadAllBooks());
        //jj- set the recyclerview object to its adapter
        recommended.setAdapter(bookadapter2);
        return view;
    }
    //load all books into a list
    public List<Book> loadAllBooks()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> mBooklist = DBaccess.loadallbooklist();
        DBaccess.close();
        return mBooklist;
    }
}
