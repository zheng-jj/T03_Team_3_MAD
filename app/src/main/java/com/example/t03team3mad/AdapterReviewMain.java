package com.example.t03team3mad;

import android.content.Context;
import android.widget.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdapterReviewMain extends RecyclerView.Adapter<AdapterReviewMain.ViewHolder>
{
    List<Review> mReviewlist = new List<Review>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
    public AdapterReviewMain(List<Review> mReviewlist) {
    }
    @Override
    public AdapterReviewMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reviewcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterReviewMain.ViewHolder viewHolder, int position) {
    }
    @Override
    public int getItemCount() {
        return mReviewlist.size();
    }
}