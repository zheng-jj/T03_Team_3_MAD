package com.example.t03team3mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

public class reportuserfragment extends Fragment {
    EditText userid;
    EditText username;
    EditText reasoning;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report,container,false);




        return view;
    }
}
