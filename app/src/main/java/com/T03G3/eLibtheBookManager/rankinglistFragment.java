package com.T03G3.eLibtheBookManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class rankinglistFragment extends Fragment {
    private static final String TAG = "rankinglistFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rankinglist,container,false);
        return view;
    }
}
