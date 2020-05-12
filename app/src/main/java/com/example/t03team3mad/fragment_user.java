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

import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class fragment_user extends Fragment{
    private static final String TAG = "userFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //jj- inflates the fragment into the container for the fragment
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        //jj- follow buttom(NOT YET IMPLEMENTED)
        Button follow = view.findViewById(R.id.follow1);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Follow button clicked");
            }
        });
        //jj-gets the recyclerview object
        RecyclerView users = (RecyclerView)view.findViewById(R.id.userreviewprofile);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        //jj-set the recyclerview's manager to the previously created manager
        users.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterUserMain useradapter  = new AdapterUserMain(loadAllusers());
        //set the recyclerview object to its adapter
        users.setAdapter(useradapter);
        return view;
    }
    //jj
    public List<User> loadAllusers()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<User> mUserlist = DBaccess.loadalluserlist();
        DBaccess.close();
        return mUserlist;
    }
}
