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
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        Button follow = view.findViewById(R.id.follow1);
        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Follow button clicked");
            }
        });
        RecyclerView users = (RecyclerView)view.findViewById(R.id.userreviewprofile);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        users.setLayoutManager(llm);
        List<User> mUserlist = loadAllusers();
        for (User x : mUserlist){
            Log.v(TAG, x.getUsername());
        }
        AdapterUserMain useradapter  = new AdapterUserMain(loadAllusers());
        users.setAdapter(useradapter);
        return view;
    }
    public List<User> loadAllusers()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<User> mUserlist = DBaccess.loadalluserlist();
        DBaccess.close();
        return mUserlist;
    }
}
