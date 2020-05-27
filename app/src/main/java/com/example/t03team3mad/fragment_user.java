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
import android.widget.Button;
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
        View view;
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        User usertoView = null;
        if (bundle.getParcelable("searchuser") != null) {
            Log.v(TAG,"showing search user profile");
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_user,container,false);
            usertoView = bundle.getParcelable("searchuser");
            //jj-removes the arguements so that i will get the reason why this page is loaded
            this.getArguments().putParcelable("searchuser",null);
        }

        //checks if the user is viewing his own profile
        else if (bundle.getParcelable("loggedin") != null){
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
            usertoView = bundle.getParcelable("loggedin");
            //onclick listener to edit profile
            Button editprofile = view.findViewById(R.id.edit);
            final User finalUsertoView = usertoView;
            Log.v(TAG,"showing current user profile");
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("UserToEdit", finalUsertoView);
                    Log.v(TAG,"user sending to edit = "+finalUsertoView.getUsername());
                    fragment_editUser fragment_editUser = new fragment_editUser();
                    fragment_editUser.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainactivitycontainer, fragment_editUser, "editUser")
                            .addToBackStack(null)
                            .commit();
                    }
            });
            Button following = view.findViewById(R.id.viewfollowing);
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("UserToEdit", finalUsertoView);
                    Log.v(TAG,"user sending to edit = "+finalUsertoView.getUsername());
                    fragment_userfollowing fragment_userFollowing = new fragment_userfollowing();
                    fragment_userFollowing.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.mainactivitycontainer, fragment_userFollowing, "viewFollowing")
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        else{
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
            Log.v(TAG,"No user received, creating demo user object");
            //jj - this variable is temporary
            usertoView = new User(1,"JIONG JIE","9780439362139;9780747591061","hey this is jj");
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

        //list of reviews made by this user
        List<Review> reviewsByUser = loaduserreviews(usertoView);

        //jj - load user reviews recyclerview
        RecyclerView pastReviews = (RecyclerView) view.findViewById(R.id.userreviewprofile);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        pastReviews.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterReviewForUSer reviewadapter = new AdapterReviewForUSer(reviewsByUser);
        //jj- set the recyclerview object to its adapter
        pastReviews.setAdapter(reviewadapter);

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

        //jj-removes the arguements so that i will get the reason why this page is loaded
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
        Log.v(TAG,"fav book list is loaded");
        return userbooklist;
    }
    //jj- get user reviews made
    public List<Review> loaduserreviews(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Review> userreviewlist = DBaccess.loaduserreviews(user);
        DBaccess.close();
        Log.v(TAG,"review list is loaded");
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

        //QH = SETS IMAGE FROM STRING
        String filename = "user" + user.getUseridu();
        int id = getResources().getIdentifier(filename, "drawable", getActivity().getPackageName());
        System.out.println(id);
        Pic.setImageResource(id);
    }
    //qh -- the user object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
