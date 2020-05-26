package com.example.t03team3mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;

import java.util.List;

public class fragment_editUserBooks extends Fragment {
    private static final String TAG = "edituserbooksFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edituserbooks,container,false);
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }
        List<Book> userfav = loaduserbooks(usertoEdit);
        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.edituserbookrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterFavBooksList bookadapter = new AdapterFavBooksList(userfav);
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(bookadapter);
        return view;
    }
    //jj-loads the books user liked
    public List<Book> loaduserbooks(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> userbooklist = DBaccess.loaduserbooklist(user);
        DBaccess.close();
        Log.v(TAG,"list is loaded" + Integer.toString(userbooklist.size()));
        return userbooklist;
    }
}
