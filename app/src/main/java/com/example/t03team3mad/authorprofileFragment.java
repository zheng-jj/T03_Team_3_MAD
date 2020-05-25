package com.example.t03team3mad;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class authorprofileFragment extends Fragment implements AdapterAuthor.OnSearchListener{

    List<Book> booklist = new ArrayList<Book>(){};
    private static final String TAG = "authorprofileFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_authorprofile,container,false);
        TextView name = view.findViewById(R.id.profilename);
        TextView about = view.findViewById(R.id.about);
        ImageView image = view.findViewById(R.id.profilepicture);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            Author receivedauthor = bundle.getParcelable("currentauthor"); // Key
            System.out.println(receivedauthor.getAuthorname());
            name.setText(receivedauthor.getAuthorname());
            about.setText(receivedauthor.getAuthorabout());
            image.setImageResource(R.drawable.demo_user_profile_pic);

            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            databaseAccess.open();
            booklist = databaseAccess.searchauthorbook(String.valueOf(receivedauthor.getAuthorid()));
            databaseAccess.close();

            //shows author's published books
            RecyclerView searchresults = (RecyclerView)view.findViewById(R.id.published);
            LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
            searchresults.setLayoutManager(searchlayout);
            AdapterAuthor searchadapter  = new AdapterAuthor(booklist,this);
            searchresults.setAdapter(searchadapter);
        }





        return view;
    }


    @Override
    public void onSearchClick(int position) {
        Book currentbook = booklist.get(position);

        bookinfoFragment nextFrag= new bookinfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentbook", currentbook);  // Key, value
        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();



    }
}
