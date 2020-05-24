package com.example.t03team3mad;

import android.content.Context;
import android.widget.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Review;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ViewHolder>

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
    public AdapterReview(List<Review> mReviewlist) {
        this.mReviewlist = mReviewlist;
    }
    @Override
    public AdapterReview.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reviewcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterReview.ViewHolder viewHolder, int position) {

        viewHolder.uName.setText(mReviewlist.get(position).getUname());
        viewHolder.uReview.setText(mReviewlist.get(position).getReviewtext());
        viewHolder.uPic.setImageResource(R.drawable.demo_user_profile_pic);
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