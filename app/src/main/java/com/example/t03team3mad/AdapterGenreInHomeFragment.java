package com.example.t03team3mad;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Context;
import android.widget.Toast;

import java.util.ArrayList;

public class AdapterGenreInHomeFragment extends RecyclerView.Adapter<AdapterGenreInHomeFragment.ViewHolder>{
    ArrayList<String> data;
    private Context context;
    private OnClickListener clickListener;
    public AdapterGenreInHomeFragment(Context context,ArrayList<String>data,OnClickListener onClickListener)
    {
        this.context=context;
        this.data=data;
        this.clickListener=onClickListener;
    }
    @NonNull
    @Override
    public AdapterGenreInHomeFragment.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View item =inflater.inflate(R.layout.genredisplaycardview,null);
        return new AdapterGenreInHomeFragment.ViewHolder(item,clickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterGenreInHomeFragment.ViewHolder holder, int position) {
        String s = data.get(position);
        holder.logo.setText(Character.toString(s.trim().charAt(0)));
        holder.txt.setText(s);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);}

    public interface OnClickListener {
        void OnClick(int postion);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView txt;
        TextView logo;
        OnClickListener onClickListener;
        public ViewHolder(View itemView,OnClickListener onClickListener){
            super(itemView);
            cardView=(CardView)itemView.findViewById(R.id.GenreCardView);
            txt = itemView.findViewById(R.id.genrename);
            logo=itemView.findViewById(R.id.GenreLogo);
            this.onClickListener=onClickListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onClickListener.OnClick (getAdapterPosition());
        }
    }
}