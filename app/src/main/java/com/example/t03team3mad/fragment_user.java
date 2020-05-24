package com.example.t03team3mad;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class fragment_user extends Fragment{
    private static final String TAG = "userFragment";
    List<Book> userBooklist = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //jj- inflates the fragment into the container for the fragment
        View view = inflater.inflate(R.layout.fragment_user,container,false);

        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        User usertoView = null;
        if (bundle.getParcelable("searchuser") != null) {
            usertoView = bundle.getParcelable("searchuser");
        }

        //checks if the user is viewing his own profile
        if (bundle.getParcelable("loggedin") != null){
            usertoView = bundle.getParcelable("loggedin");
        }

        if (usertoView==null){
            Log.v(TAG,"no user object received");
            //jj - this variable is temporary
            usertoView = new User(2,"JIONG JIE","9780439362139;9780747591061","hey this is jj");
        }

        Log.v(TAG, "user view: username: "+ usertoView.getUsername());
        int userid = usertoView.getUseridu();
        //jj - loads user into layout
        loaduserintoview(view,usertoView);

        //jj - sets list that will hold user's favourite books and all the reviews he made
        List<Review> userReviewlist = new ArrayList<Review>(){};

        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favbookslist);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter = new AdapterBookMain(loaduserbooks(usertoView));
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(bookadapter);



//        //jj- follow buttom(NOT YET IMPLEMENTED)
//        Button follow = view.findViewById(R.id.follow1);
//        follow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.v(TAG,"Follow button clicked");
//            }
//        });
//        //jj-gets the recyclerview object
//        RecyclerView users = (RecyclerView)view.findViewById(R.id.userreviewprofile);
//        //jj-layout manager linear layout manager manages the position of the recyclerview items
//        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
//        //jj-set the recyclerview's manager to the previously created manager
//        users.setLayoutManager(llm);
//        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
//        AdapterUserMain useradapter  = new AdapterUserMain(loadAllusers());
//        //jj- set the recyclerview object to its adapter
//        users.setAdapter(useradapter);
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
    //jj- get user reviews made
    public List<Review> loaduserreviews(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Review> userreviewlist = DBaccess.loaduserreviews(user);
        DBaccess.close();
        Log.v(TAG,"list is loaded");
        return userreviewlist;
    }
    //jj - Loads the user information into the layout
    public void loaduserintoview(View view, User user){
        ImageView Pic = view.findViewById(R.id.userPic);
        TextView Name = view.findViewById(R.id.userName);
        TextView Desc = view.findViewById(R.id.userDescription);
        Name.setText(user.getUsername());
        Desc.setText(user.getUserabout());
        Pic.setImageResource(R.drawable.demo_user_profile_pic);
    }
    //qh -- the user object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
