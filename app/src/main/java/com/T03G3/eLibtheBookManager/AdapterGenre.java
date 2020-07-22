package com.T03G3.eLibtheBookManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
//Chris - book info genre adaptor
public class AdapterGenre extends RecyclerView.Adapter<AdapterGenre.ViewHolder> {
    ArrayList<String> data;
    private OnClickListener clickListener;
    public AdapterGenre(ArrayList<String> input,OnClickListener onClickListener) {
        data = input;
        clickListener=onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.genre_view, parent, false);
            return new ViewHolder(item,clickListener);
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

    public interface OnClickListener
    {
        void OnClick(int postion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt;
        OnClickListener onClickListener;
        public ViewHolder(View itemView,OnClickListener onClickListener){
            super(itemView);
            txt = itemView.findViewById(R.id.genrestext);
            this.onClickListener=onClickListener;
            itemView.setOnClickListener(this);
        }
        public void onClick(View v) {
            onClickListener.OnClick (getAdapterPosition());
        }

    }
}
