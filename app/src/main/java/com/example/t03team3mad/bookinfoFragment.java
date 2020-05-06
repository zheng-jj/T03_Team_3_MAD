package com.example.t03team3mad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class bookinfoFragment extends Fragment {
    private static final String TAG = "bookinfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookinfo,container,false);
        TextView name = view.findViewById(R.id.textView4);
        // EXAMPLE TO EXTRACT INFORMATION
        // SELECT (column) FROM (table) WHERE (condition_column) = (condition)
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        name.setText(databaseAccess.getCount("Title","Book"));
        databaseAccess.close();
        return view;
    }
}
