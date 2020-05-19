package com.example.t03team3mad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;

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
    //qh -- the book object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Book receivedbook = bundle.getParcelable("currentbook"); // Key
            System.out.println(receivedbook.getBooktitle());
        }
    }
}

