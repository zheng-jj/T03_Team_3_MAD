package com.example.t03team3mad;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AdapterGenre extends RecyclerView.Adapter<AdapterGenre.ViewHolder> {
    ArrayList<String> data;

    public AdapterGenre(ArrayList<String> input) {
        data = input;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_view, parent, false);
            return new ViewHolder(item);
        }


        @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            String s = data.get(position);
            holder.txt.setText(s);

    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txt;

        public ViewHolder(View itemView){
            super(itemView);
            txt = itemView.findViewById(R.id.genrestext);
        }

    }
}
