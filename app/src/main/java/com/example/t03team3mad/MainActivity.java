package com.example.t03team3mad;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;
import com.roughike.bottombar.*;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener{
    private static final String TAG = "MainActivity";
    private Integer uid = null;
    public static User loggedinuser = null;
    public List<String> backstacktags = new ArrayList<>();
    BottomBar bottomBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starthomefragment();
        Intent intent = getIntent();
        //jj - receives which user is currently logged in from the login activity
        Bundle receivedloggedin = intent.getExtras().getBundle("User_UID");




        if(receivedloggedin!=null) {
            Log.v(TAG,"logged in user received");
            String loggedinuserID = receivedloggedin.getString("User_UID");
            //jj- gets data from firestore
            FireStoreAccess.AccessUser accessUser = new FireStoreAccess.AccessUser();
            AsyncTask<String, Void, User> task = accessUser.execute(loggedinuserID);
            Log.v(TAG,"Task status ="+task.getStatus().toString());
            try {
                task.get();
                Thread.sleep(300);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
//            DatabaseAccess DBaccess = DatabaseAccess.getInstance(this.getApplicationContext());
//            DBaccess.open();
//            loggedinuser =  DBaccess.searchuserbyid(loggedinuserID);
//            DBaccess.close();
        }
        if(loggedinuser == null)
        {
            loggedinuser = new User(-1,"error","9780439362139;9780747591061","please re-open application with internet access");
            uid = loggedinuser.getUseridu();
            Log.v(TAG,"no logged in user received");
            //startsLoginPage();
        }


        //removes

        //updates local user database to sync data with firestore
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(this.getApplicationContext());
        DBaccess.open();
        DBaccess.editUserData(MainActivity.loggedinuser);
        DBaccess.close();


        //jj - this is the code to navigate using the bottom navigation bar
        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            //enter the fragment function based on what is clicked(after yall done)
            public void onTabSelected(@IdRes int tabId) {
            if (tabId == R.id.tab_home) {
                //jj- if the fragment is reselected, hide the rest and show the selected fragment
                bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                    @Override
                    public void onTabReSelected(int tabId) {
                        getSupportFragmentManager().popBackStack("HomeFragment",0);
                        starthomefragment();
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("HomeFragment")).commit();
                    }
                });
                starthomefragment();
                getSupportFragmentManager().popBackStack("HomeFragment",0);
            }
            if (tabId == R.id.tab_search) {
                bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                    @Override
                    public void onTabReSelected(int tabId) {
                        getSupportFragmentManager().popBackStack("SearchFragment",0);
                        startsearchbarfragment();
                        try {
                        getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("SearchFragment")).commit();
                        }catch (Exception e){}
                    }
                });
                startsearchbarfragment();
                getSupportFragmentManager().popBackStack("SearchFragment",0);
            }
            if (tabId == R.id.tab_feed) {
            }
            if (tabId == R.id.tab_profile) {
                bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
                    @Override
                    public void onTabReSelected(int tabId) {
                        getSupportFragmentManager().popBackStack("UserFragment",0);
                        startuserfragment();
                        try {
                            getSupportFragmentManager().beginTransaction().show(getSupportFragmentManager().findFragmentByTag("UserFragment")).commit();
                        }catch (Exception e){}
                    }
                });
                startuserfragment();
                getSupportFragmentManager().popBackStack("UserFragment",0);
            }
            }
        });
        //attaches listener for whenever backstack changes
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
    }

    public static void updateUserLogged(User user){
        loggedinuser = user;
    }

    private void starthomefragment(){
        Log.v(TAG, "home fragment launched");
        HomeFragment homeFragment = new HomeFragment();
        addFragment(homeFragment,this,"HomeFragment");
    }
    //jj - starts user fragment based on what is to be displayed
    private void startuserfragment(){
        Log.v(TAG, "user fragment launched");
        //fake data generated
        fragment_user fragment = new fragment_user();
        //jj- bundle to be moved to fragment
        Bundle bundle = new Bundle();
        //places the currently logged in user into bundle
//        Log.v(TAG,"Creating user in main activity(in place of recieved user from login)");
//        DatabaseAccess DBaccess = DatabaseAccess.getInstance(this.getApplicationContext());
//        DBaccess.open();
//        loggedinuser =  DBaccess.searchuserbyid("1");
//        DBaccess.close();
        bundle.putParcelable("loggedin", loggedinuser);
        fragment.setArguments(bundle);
        addFragment(fragment,this,"UserFragment");
    }

    private void startsearchbarfragment() {
        Log.v(TAG, "searchbar fragment launched");
        searchbarFragment searchbar = new searchbarFragment();
        addFragment(searchbar, this, "SearchFragment");
    }

    //jj-manage backstack plz dont touchy touchy

    //jj- this overrides the back press button so that we can see which fragments are in the backstack at any given time when backstack is pressed
    @Override
    public void onBackPressed(){
        Integer amount = getSupportFragmentManager().getBackStackEntryCount();
        Log.v(TAG,"Total count in backstack before backbuttonpressed "+ Integer.toString(amount));
        for(Integer x = 0;x<amount;x++){
            Log.v(TAG,"tags from in backstack before backbuttonpressed "+ getSupportFragmentManager().getBackStackEntryAt(x).getName());

        }

        //backstack consists of empty main activity, hence when i set the amount to 2, it will just skip past the empty container and go back
        if(amount<=2){
            finish();
            System.exit(0);
        }
        else{
            super.onBackPressed();
        }
    }
//    //jj- listener which will check if backstack changes
//    private FragmentManager.OnBackStackChangedListener getListener() {
//        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
//            public void onBackStackChanged() {
//                FragmentManager manager = getSupportFragmentManager();
//                if (manager != null) {
//                    int backStackEntryCount = manager.getBackStackEntryCount();
//                    if (backStackEntryCount == 2) {
//                        finish();
//                    }
//                    //updates local list for backstacks
//                    backstacktags.clear();
//                    for(Integer count=0 ; count<manager.getBackStackEntryCount();count++){
//                        //if the tag does not already exist in the backstack
//
//                        backstacktags.add(manager.getBackStackEntryAt(count).getName());
//                    }
//                    for(String x:backstacktags){
//                        Log.v(TAG,"Backstacktag: "+x);
//                    }
//                    Fragment fragment = manager.findFragmentByTag(backstacktags.get(backstacktags.size()-1));
//                    fragment.onResume();
//                }
//            }
//        };
//        return result;
//    }

    //jj-method which is called whenever the backstack changes in the fragment manager
    @Override
    public void onBackStackChanged() {
        //jj-list to store duplicated fragments in backstack to be removed from manager
        HashMap<String,Integer> fragmentsAndTheirCountsinBackStack = new HashMap<String, Integer>();
        Integer amount = getSupportFragmentManager().getBackStackEntryCount();
        Log.v(TAG,"Total count in backstack is "+ Integer.toString(amount));
        //clears local list for backstacktags
        backstacktags.clear();

        for(Integer x = 0;x<amount;x++){
            //prints out all the fragments in the backstackmanager at this given point
            Log.v(TAG,"tags from in backstack "+ getSupportFragmentManager().getBackStackEntryAt(x).getName());
            backstacktags.add(getSupportFragmentManager().getBackStackEntryAt(x).getName());
        }

        FragmentManager manager = ((AppCompatActivity)this).getSupportFragmentManager ();
        FragmentTransaction ft = manager.beginTransaction ();
        //jj-hides all other fragments
        for(Fragment allhide : manager.getFragments()){
            ft.hide(allhide);
        }
        try {
            ft.show(manager.getFragments().get(manager.getFragments().size()-1));
        }catch (Exception e){}

        ft.commit();
    }
    //jj- method that can be used for all other fragments to show fragment
    public static void addFragment (Fragment fragment, Context context, String tag ) {
        FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction ();
        //jj-hides all other fragments
        // if there are no fragments in the backstack with the same tag
        if (manager.findFragmentByTag(tag) == null) {
            //jj- only adds to backstack if it doesnt even exist
            ft.add(R.id.mainactivitycontainer, fragment, tag);
            ft.addToBackStack(tag);
            ft.commit();
        }
        else {
            for(Fragment allhide : manager.getFragments()){
                Log.v(TAG,"backstack consists of "+allhide.getTag());
                ft.hide(allhide);
            }
            //jj- i need to user fragment to always refresh since it gets updated rather than showing
            if(fragment instanceof fragment_user){
                ft.detach(manager.findFragmentByTag(tag));
                ft.attach(manager.findFragmentByTag(tag));
                ft.show(manager.findFragmentByTag(tag));
            }
            else {
                //only shows the fragment chosen
                ft.show(manager.findFragmentByTag(tag));
            }
            ft.commit();
        }
    }
}
