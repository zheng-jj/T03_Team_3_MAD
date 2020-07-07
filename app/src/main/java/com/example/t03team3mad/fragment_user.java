package com.example.t03team3mad;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TaskInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.google.cloud.datastore.core.number.IndexNumberDecoder;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import dalvik.system.InMemoryDexClassLoader;

public class fragment_user extends Fragment {
    private static final String TAG = "userFragment";
    List<Book> newbooklist;
    SharedPreferences Auto_login;
    User usertoView = null;
    View v;
    List<User> currentlyfollow = new ArrayList<>();
    ImageView Pic;
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
            Log.v(TAG,"currently viewing ="+ String.valueOf(usertoView.getUseridu()));
            Log.v(TAG, "currently logged in ="+String.valueOf(MainActivity.loggedinuser.getUseridu()));
            //checks if user is viewing himself
            if(usertoView.getUseridu()==MainActivity.loggedinuser.getUseridu()){
                this.getArguments().putParcelable("searchuser",null);
                fragment_user fragment = new fragment_user();
                //jj- bundle to be moved to fragment
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("loggedin", MainActivity.loggedinuser);
                fragment.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment,getActivity(),"UserFragment");
            }


            //gets user object from database
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
                    MainActivity.loggedinuser.setfollowingstring(followid);
                    AsyncTask<User,Void,Void> tasktoupdateUser = new updateFireStoreUser.AccessUser().execute(MainActivity.loggedinuser);
                    try {
                        tasktoupdateUser.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
        else {
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
            //gets user object from database
//            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
//            DBaccess.open();
//            usertoView = DBaccess.searchuserbyid(Integer.toString(usertoView.getUseridu()));
//            DBaccess.close();

            usertoView = MainActivity.loggedinuser;
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
                    //creates alert to log out
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Log Out");
                    builder.setMessage("Are you sure you want to log out?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        //jj-stops auto login
                        editor.putBoolean("logged", false);
                        editor.commit();
                        //external library used to restart application
                        ProcessPhoenix.triggerRebirth(logout.getRootView().getContext());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.v(TAG,"user chose not to log out");
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });

        }

//        else{
//            //jj- inflates the fragment into the container for the fragment
//            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
//            Log.v(TAG,"No user received, creating demo user object");
//            //jj - this variable is temporary
//            usertoView = new User(1,"JIONG JIE","9780439362139;9780747591061","hey this is jj");
//        }
        v=view;
        Log.v(TAG, "user view: username: "+ usertoView.getUsername());
        int userid = usertoView.getUseridu();
        //jj - loads user into layout
        try {
            Pic = view.findViewById(R.id.userPic);
            String path = getimage(usertoView);
            loaduserintoview(view,usertoView,path);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //user favourite books loaded from firebase
        ArrayList<Book> userfav = new ArrayList<>();
        try {
            userfav = loaduserbooks(usertoView);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(userfav!= null) {
            //jj - load favourite user books recyclerview
            RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favbookslist);
            //jj-layout manager linear layout manager manages the position of the recyclerview items
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //jj-set the recyclerview's manager to the previously created manager
            favouritebooks.setLayoutManager(llm);
            //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
            AdapterBookMain bookadapter = new AdapterBookMain(userfav, this.getContext());
            //jj- set the recyclerview object to its adapter
            favouritebooks.setAdapter(bookadapter);
        }
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
    public ArrayList<Book> loaduserbooks(User user) throws ExecutionException, InterruptedException {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        //DBaccess.open();
        ArrayList<Book> userbooklist = new ArrayList<>();
//        try {
//            //foreach book in user's local db favourited books, get it from the api
//            for(Book book:DBaccess.loaduserbooklist(DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu())))) {
//                AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(book.getIsbn());
//                try {
//                    Book temp = tasktogetbook.get();
//                    if (temp != null) {
//                        Log.v(TAG, "Book created = " + temp.getBooktitle());
//                        Log.v(TAG, "Book isbn = " + temp.getIsbn());
//                        Log.v(TAG, "Book about = " + temp.getBookabout());
//                        Log.v(TAG, "Book date = " + temp.getPdate());
//                        Log.v(TAG, "Book genre = " + temp.getBookgenre());
//                        Log.v(TAG, "Book author = " + temp.getBookauthor());
//                        userbooklist.add(temp);
//                    }
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e){}
//        DBaccess.close();

        AsyncTask<String, Void,ArrayList<Book>> task = new APIaccessBookList(getContext()).execute(user.getUserisbn());

        userbooklist=task.get();
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
    public void loaduserintoview(View view, User user, String path) throws InterruptedException, ExecutionException {
        TextView Name = view.findViewById(R.id.userName);
        TextView Desc = view.findViewById(R.id.userDescription);
        Name.setText(user.getUsername());
        Desc.setText(user.getUserabout());

        Log.v(TAG,"image id being used = user+"+ Integer.toString(user.getUseridu()));
        //String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        //jj gets image from firebase and saves to local storage
        //sets the profile image
        File check = new File(path);
        int count = 20;
        while(count>0){
            Log.v(TAG,"user image is not saved yet");
            if(check.exists()) {
                Pic.setImageBitmap(BitmapFactory.decodeFile(path));
                Pic.invalidate();
                if(Pic.getDrawable() != null){
                    TimeUnit.MILLISECONDS.sleep(100);
                    Pic.setImageBitmap(BitmapFactory.decodeFile(path));
                    break;
                }
                else {
                    continue;
                }
            }
            else{
                Thread.sleep(100);
            }
            count=count-1;
        }
    }
    //jj-method that gets the image and saves to internal storage
    public String getimage(User user) throws ExecutionException, InterruptedException {
        String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        //jj gets image from firebase and saves to local storage
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
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
}
