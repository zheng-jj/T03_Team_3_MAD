package com.example.t03team3mad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Review;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterReviewForUSer extends RecyclerView.Adapter<AdapterReviewForUSer.ViewHolder>

{
    Context context;
    String Isbn;

    //jj- adapter for review recycler view in user page
    List<Review> mReviewlist = new ArrayList<Review>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView uName;
        TextView uReview;
        ImageView uPic;


        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.rcview);
            uName = (TextView)itemView.findViewById(R.id.uname);
            uReview = (TextView)itemView.findViewById(R.id.ureview);
            uPic = (ImageView)itemView.findViewById(R.id.uimg);

        }
    }
    public AdapterReviewForUSer(List<Review> mReviewlist) {
        this.mReviewlist = mReviewlist;

    }
    @Override
    public AdapterReviewForUSer.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reviewcardviewuser, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterReviewForUSer.ViewHolder viewHolder, int position) {
        viewHolder.uName.setText(mReviewlist.get(position).getReviewTitle());
        viewHolder.uReview.setText(mReviewlist.get(position).getReviewtext());
        if(mReviewlist.get(position).getimglink()== null||mReviewlist.get(position).getimglink()=="") {
            viewHolder.uPic.setImageResource(R.drawable.empty);
        }
        else{
            Picasso.with(context).load(mReviewlist.get(position).getimglink()).into(viewHolder.uPic);
        }
        Log.v(TAG,"Review for :"+mReviewlist.get(position).getBookName());
    }

    @Override
    public int getItemCount() {
        return mReviewlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}