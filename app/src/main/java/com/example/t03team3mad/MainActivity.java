package com.example.t03team3mad;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.roughike.bottombar.*;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starthomefragment();

        //this is the code to navigate using the bottom navigation bar
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
                startuserfragment();
            }
            if (tabId == R.id.tab_profile) {
                startauthorprofilefragment();
            }
            }
        });
    }
    private void starthomefragment(){
        Log.v(TAG, "home fragment launched");
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,homeFragment,"HomeFragment");
        transaction.addToBackStack("HomeFragment");
        transaction.commit();
    }
    private void startuserfragment(){
        Log.v(TAG, "user fragment launched");
        fragment_user fragment = new fragment_user();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,fragment,"HomeFragment");
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

    private void startsearchbarfragment(){
        Log.v(TAG, "Search Bar launched");
        Intent searchbar= new Intent(getApplicationContext(),SearchPage.class);
        startActivity(searchbar);
    }
    private void startsLoginfragment(){
        Log.v(TAG, "Login Page launched");
        Intent Login= new Intent(getApplicationContext(),LoginPage.class);
        startActivity(Login);
    }
    private void startRegisterfragment(){

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
    private void starteditprofilefragment(){
        Log.v(TAG, "edit profile fragment launched");
        editprofileFragment editFragment= new editprofileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,editFragment,"editprofileFragment");
        transaction.addToBackStack("editprofileFragment");
        transaction.commit();
    }
}
