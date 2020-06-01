package com.example.t03team3mad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;



public class reviewpageFragment extends Fragment {
    private static final String TAG = "authorprofileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        final Book book = bundle.getParcelable("book");
        View view = inflater.inflate(R.layout.fragment_reviewpage,container,false);
        TextView booktitle = view.findViewById(R.id.rtitle);
        ImageView bookimage = view.findViewById(R.id.bookimg);
        bookimage.setImageResource(R.drawable.demo_book_pic);
        booktitle.setText(book.getBooktitle());
        RecyclerView reviews = (RecyclerView)view.findViewById(R.id.rRecyclerView);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviews.setLayoutManager(llm);
        AdapterReview adapterReview  = new AdapterReview(loadAllReviews(book.getIsbn()));
        reviews.setAdapter(adapterReview);
        return view;
    }
    public List<Review> loadAllReviews(String ISBN)
    {
        Log.v(TAG,"loaded reviews");
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Review> mReviewlist = DBaccess.extractreviewbybook(ISBN);
        DBaccess.close();
        Log.d("list",mReviewlist.toString());

        return mReviewlist;
    }
    public String Title(String ISBN)
    {

        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        String bname = DBaccess.getElement("Title","Book","ISBN",ISBN);
        DBaccess.close();
        return bname;
    }




}
