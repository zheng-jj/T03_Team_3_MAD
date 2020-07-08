package com.example.t03team3mad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;



public class reviewpageFragment extends Fragment {
    private static final String TAG = "authorprofileFragment";
    List<Review> reviewlist = new ArrayList<Review>();
    String name;
    AdapterReview adapterReview;
    String isbncheck;
    String Title;
    private CollectionReference mCollectionRefreview = FirebaseFirestore.getInstance().collection("Reviews");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo - receive bundle from another fragment
        Bundle bundle = this.getArguments();
        final Book book = bundle.getParcelable("book");
        isbncheck = book.getIsbn();
        // jo - display layout
        View view = inflater.inflate(R.layout.fragment_reviewpage,container,false);

        // jo - findviewbyids
        TextView booktitle = view.findViewById(R.id.rtitle);
        ImageView bookimage = view.findViewById(R.id.bookimg);
        String filename = "book" + book.getIsbn() +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        bookimage.setImageBitmap(bmImg);
        booktitle.setText(book.getBooktitle());
        // jo - recyclerview
        RecyclerView reviews = (RecyclerView)view.findViewById(R.id.rRecyclerView);
        // jo- linear layout manager - set layout to vertical
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        reviews.setLayoutManager(llm);

        // jo - load data from list into the viewholder


        adapterReview  = new AdapterReview(reviewlist,this);

        reviews.setAdapter(adapterReview);
        


        return view;
    }






}
