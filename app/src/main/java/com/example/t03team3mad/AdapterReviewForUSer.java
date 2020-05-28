package com.example.t03team3mad;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Review;

import java.util.ArrayList;
import java.util.List;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class AdapterReviewForUSer extends RecyclerView.Adapter<AdapterReviewForUSer.ViewHolder>

{
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reviewcardviewuser, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterReviewForUSer.ViewHolder viewHolder, int position) {
        viewHolder.uName.setText(mReviewlist.get(position).getBookName());
        viewHolder.uReview.setText(mReviewlist.get(position).getReviewtext());
        viewHolder.uPic.setImageResource(R.drawable.demo_book_pic);
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