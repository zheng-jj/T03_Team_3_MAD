package com.example.t03team3mad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        starthomefragment();
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
    private void authorprofilefragment(){
        Log.v(TAG, "authorprofile fragment launched");
        authorprofileFragment authorprofile = new authorprofileFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,authorprofile,"authorprofileFragment");
        transaction.addToBackStack("authorprofileFragment");
        transaction.commit();
    }
    private void bookdisplayfragment(){
        Log.v(TAG, "bookdisplay fragment launched");
        bookdisplayFragment bookdisplay = new bookdisplayFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,bookdisplay,"bookdisplayFragment");
        transaction.addToBackStack("bookdisplayFragment");
        transaction.commit();
    }
    private void reviewpagefragment(){
        Log.v(TAG, "reviewpage fragment launched");
        reviewpageFragment reviewpage = new reviewpageFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.mainactivitycontainer,reviewpage,"reviewpageFragment");
        transaction.addToBackStack("reviewpageFragment");
        transaction.commit();
    }
}
