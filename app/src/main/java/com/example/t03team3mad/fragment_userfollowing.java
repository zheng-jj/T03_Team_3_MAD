package com.example.t03team3mad;

import android.os.Bundle;
import android.provider.ContactsContract;
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

public class fragment_userFollowing extends Fragment {
    private static final String TAG = "userfollowingFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following,container,false);
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }
        List<User> userFollowing = loaduserfollowing(Integer.toString(usertoEdit.getUseridu()));
        for(User user : userFollowing){
            Log.v(TAG,"following user "+user.getUsername());
        }
        //jj - load favourite user books recyclerview
        RecyclerView followings = (RecyclerView) view.findViewById(R.id.followerrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        followings.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterUserMain UserAdapter = new AdapterUserMain(userFollowing);
        //jj- set the recyclerview object to its adapter
        followings.setAdapter(UserAdapter);
        return view;
    }
    //jj-loads list of users the user is following
    public List<User> loaduserfollowing(String UID){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<User> userfollowinglist = DBaccess.getUserFollowing(UID);
        DBaccess.close();
        Log.v(TAG,"list is loaded" + Integer.toString(userfollowinglist.size()));
        return userfollowinglist;
    }
}
