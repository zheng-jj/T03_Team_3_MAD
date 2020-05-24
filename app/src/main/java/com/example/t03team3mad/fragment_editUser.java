package com.example.t03team3mad;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class fragment_editUser extends Fragment{
    private static final String TAG = "userEditFragment";
    List<Book> userBooklist = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_editprofile,container,false);
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }
        //jj - loads user into layout
        if(usertoEdit!=null) {
            loaduserintoview(view, usertoEdit);
            this.getArguments().clear();
        }

        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favBooksRecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter = new AdapterBookMain(loaduserbooks(usertoEdit));
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(bookadapter);


        return view;
    }
//    public List<User> loadAllusers()
//    {
//        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
//        DBaccess.open();
//        List<User> mUserlist = DBaccess.loadalluserlist();
//        DBaccess.close();
//        return mUserlist;
//    }
    //jj - gets the user's favourite books
    public List<Book> loaduserbooks(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> userbooklist = DBaccess.loaduserbooklist(user);
        DBaccess.close();
        Log.v(TAG,"list is loaded");
        return userbooklist;
    }
    //jj - Loads the user information into the layout
    public void loaduserintoview(View view, User user){
        ImageButton Pic = view.findViewById(R.id.newprofileimg);
        EditText Name = view.findViewById(R.id.editName);
        EditText Desc = view.findViewById(R.id.editDes);
        Log.v(TAG,"current user name= "+user.getUsername());
        Log.v(TAG,"current user dec= "+user.getUserabout());
        Name.setHint(user.getUsername());
        Desc.setHint(user.getUserabout());
        Pic.setImageResource(R.drawable.demo_user_profile_pic);
    }
    //qh -- the user object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
