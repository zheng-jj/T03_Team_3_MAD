package com.T03G3.eLibtheBookManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class bookdisplayFragment extends Fragment {
    private static final String TAG = "bookdisplayFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookdisplay,container,false);
        return view;
    }
}
