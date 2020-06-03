package com.example.t03team3mad;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;
import com.roughike.bottombar.*;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Integer uid = null;
    public static User loggedinuser = null;
    public List<String> backstacktags = new ArrayList<>();
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
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(this.getApplicationContext());
            DBaccess.open();
            loggedinuser =  DBaccess.searchuserbyid(loggedinuserID);
            DBaccess.close();
        }
        if(loggedinuser == null)
        {
            loggedinuser = new User(2,"JIONG JIE","9780439362139;9780747591061","hey this is jj");
            uid = loggedinuser.getUseridu();
            Log.v(TAG,"no logged in user received");
            //startsLoginPage();
        }

        //jj - this is the code to navigate using the bottom navigation bar
        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            //enter the fragment function based on what is clicked(after yall done)
            public void onTabSelected(@IdRes int tabId) {
            if (tabId == R.id.tab_home) {
                starthomefragment();
            }
            if (tabId == R.id.tab_search) {
                startsearchbarfragment();
            }
            if (tabId == R.id.tab_feed) {
                startreviewpagefragment();
            }
            if (tabId == R.id.tab_profile) {
                startuserfragment();
            }
            }
        });
        //getSupportFragmentManager().addOnBackStackChangedListener(getListener());
    }



    private void starthomefragment(){
        Log.v(TAG, "home fragment launched");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,homeFragment,"HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,fragment,"UserFragment");
        transaction.addToBackStack("UserFragment");
        transaction.commit();
    }
    private void startauthorprofilefragment(){
        Log.v(TAG, "authorprofile fragment launched");
        authorprofileFragment authorprofile = new authorprofileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,authorprofile,"authorprofileFragment");
        transaction.addToBackStack("authorprofileFragment");
        transaction.commit();
    }
    private void startbookdisplayfragment(){
        Log.v(TAG, "bookdisplay fragment launched");
        bookdisplayFragment bookdisplay = new bookdisplayFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,bookdisplay,"bookdisplayFragment");
        transaction.addToBackStack("bookdisplayFragment");
        transaction.commit();
    }
    private void startreviewpagefragment(){
        Log.v(TAG, "reviewpage fragment launched");
        reviewpageFragment reviewpage = new reviewpageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,reviewpage,"reviewpageFragment");
        transaction.addToBackStack("reviewpageFragment");
        transaction.commit();
    }

    private void startsearchbaractivity(){
        Log.v(TAG, "Search Bar launched");
        Intent searchbar= new Intent(getApplicationContext(),SearchPage.class);
        startActivity(searchbar);
    }

    private void startsearchbarfragment(){
        Log.v(TAG, "searchbar fragment launched");
        searchbarFragment searchbar = new searchbarFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,searchbar,"searchbarFragment");
        transaction.addToBackStack("searchbarFragment");
        transaction.commit();
    }
    private void startsLoginPage(){
        Log.v(TAG, "Login Page launched");
        Intent Login= new Intent(getApplicationContext(),LoginPage.class);
        startActivity(Login);
    }
    private void startRegisterPage(){

        Log.v(TAG, "Register Page launched");
        Intent register= new Intent(getApplicationContext(),RegisterPage.class);
        startActivity(register);
    }

    private void startbookinfofragment(){
        Log.v(TAG, "bookinfo fragment launched");
        bookinfoFragment bookinfo= new bookinfoFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,bookinfo,"bookinfoFragment");
        transaction.addToBackStack("bookinfoFragment");
        transaction.commit();
    }
//    private void fragment_addreview(){
//        Log.v(TAG, "add review fragment launched");
//        fragment_addreview addreviewfragment = new fragment_addreview();
//        fragment_user fragment = new fragment_user();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("user",loggedinuser);
//        fragment.setArguments(bundle);
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.mainactivitycontainer,addreviewfragment,"addreviewFragment");
//        transaction.addToBackStack("addreviewFragment");
//        transaction.commit();
//    }
    //jj- this overrides the back press button so that we can see which fragments are in the backstack at any given time when backstack is pressed
    @Override
    public void onBackPressed(){
        Integer amount = getSupportFragmentManager().getBackStackEntryCount();
        Log.v(TAG,"Total count in backstack is "+ Integer.toString(amount));
        for(Integer x = 0;x<amount;x++){
            Log.v(TAG,"tags from in backstack "+ getSupportFragmentManager().getBackStackEntryAt(x).getName());
        }
        //backstack consists of empty main activity, hence when i set the amount to 2, it will just skip past the empty container and go back
        if(amount==2){
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
}
