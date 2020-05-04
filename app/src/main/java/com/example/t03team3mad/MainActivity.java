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
        Button change = findViewById(R.id.change);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startuserfragment();
            }
        });
        Button back = findViewById(R.id.change2);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                starthomefragment();
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
}
