package com.example.t03team3mad;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import dalvik.system.InMemoryDexClassLoader;

public class fragment_user extends Fragment implements AdapterBookMain.OnBookMainListener {
    private static final String TAG = "userFragment";
    List<Book> newbooklist;
    SharedPreferences Auto_login;
    User usertoView = null;
    View v;
    List<User> currentlyfollow = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        //jj-gets the user currently following list
        try {
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();
            currentlyfollow = DBaccess.getUserFollowing(Integer.toString(MainActivity.loggedinuser.getUseridu()));
            DBaccess.close();
        }catch (Exception e){}
        if (bundle.getParcelable("searchuser") != null) {
            Log.v(TAG,"showing search user profile");
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_user,container,false);

            usertoView = bundle.getParcelable("searchuser");
            //gets user object from database
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();
            usertoView = DBaccess.searchuserbyid(Integer.toString(usertoView.getUseridu()));
            DBaccess.close();

            final Button followthisuser = view.findViewById(R.id.follow1);
            //jj-checks if logged in user follows this user
            for(User check : currentlyfollow){
                if(check.getUseridu()==usertoView.getUseridu()){
                    followthisuser.setText("Followed");
                    break;
                }
                else{
                    followthisuser.setText("Follow");
                }
            }
            //jj-sets up button which allows currently logged in user to follow the user
            followthisuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean follow = false;
                    for(User user : currentlyfollow){
                        //if user is currently following this user, update the boolean
                        if(user.getUseridu()==usertoView.getUseridu()){
                            follow=true;
                            break;
                        }
                    }
                    //if user is currently following this user and wishes to unfollow, update the list
                    Iterator<User> iter = currentlyfollow.iterator();
                    if(follow==true){
                        while (iter.hasNext()) {
                            User user = iter.next();
                            if (user.getUseridu()==usertoView.getUseridu())
                                iter.remove();
                        }
                    }
                    else {
                        currentlyfollow.add(usertoView);
                    }
                    //creates the string to be entered into database
                    String followid = "";
                    for(User followed : currentlyfollow){
                        followid=followid+Integer.toString(followed.getUseridu())+";";
                    }
                    if(followid.equals("")){
                    }
                    else {
                        //removes the final ";"
                        followid = followid.substring(0, followid.length() - 1);
                    }
                    Log.v(TAG,"Following list "+followid);
                    DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                    DBaccess.open();
                    DBaccess.updateUserFollowing(MainActivity.loggedinuser,followid);
                    DBaccess.close();

                    //updates text on button
                    if(currentlyfollow.contains(usertoView)){
                        followthisuser.setText("Followed");
                    }
                    else{
                        followthisuser.setText("Follow");
                    }
                }
            });
            //jj-removes the arguements so that i will get the reason why this page is loaded
            this.getArguments().putParcelable("searchuser",null);
        }

        //checks if the user is viewing his own profile
        else if (bundle.getParcelable("loggedin") != null){
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
            usertoView = bundle.getParcelable("loggedin");
            //gets user object from database
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();
            usertoView = DBaccess.searchuserbyid(Integer.toString(usertoView.getUseridu()));
            DBaccess.close();
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
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(fragment_editUser,getActivity(),"EditUser");
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
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(fragment_userFollowing,getActivity(),"UserFollowing");
                }
            });
            //jj-button to logout
            LoginPage temp =  new LoginPage(getActivity());
            Auto_login = temp.getLogincontext().getSharedPreferences("LoginButton",Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = Auto_login.edit();
            final Button logout = view.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //jj-stops auto login
                    editor.putBoolean("logged", false);
                    editor.commit();
                    //external library used to restart application
                    ProcessPhoenix.triggerRebirth(logout.getRootView().getContext());
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
        v=view;
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
        AdapterBookMain bookadapter = new AdapterBookMain(loaduserbooks(usertoView),this,this.getContext());
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
        this.newbooklist=userbooklist;
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

        Log.v(TAG,"image id being used = user+"+ Integer.toString(user.getUseridu()));
        //QH = SETS IMAGE FROM STRING
        String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        Pic.setImageBitmap(bmImg);
    }
    //qh -- the user object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v(TAG,"USER FRAGMENT RECREATED");
        super.onAttach(context);
    }

    @Override
    public void onBookMainClick(int position) {
        Book currentbook = newbooklist.get(position);

        bookinfoFragment nextFrag= new bookinfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentbook", currentbook);  // Key, value
        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }
}
