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

public class AdapterUserMain extends RecyclerView.Adapter<AdapterUserMain.ViewHolder>
{
    List<User> mUserlist = new List<User>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
    public AdapterUserMain(List<User> mUserlist) {
    }
    @Override
    public AdapterUserMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //Complete the card view for users
        //View contactView = inflater.inflate(R.layout.Cardviewuser, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterUserMain.ViewHolder viewHolder, int position) {
    }
    @Override
    public int getItemCount() {
        return mUserlist.size();
    }
}