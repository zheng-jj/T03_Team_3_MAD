package com.example.t03team3mad;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    List<String> des = new ArrayList<String>(){};

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView searchname;
        ImageView searchpic;
        TextView searchdes;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.searchCardView);

            searchname = (TextView)itemView.findViewById(R.id.titleview);
            searchdes = (TextView)itemView.findViewById(R.id.descriptionview);
            searchpic = (ImageView)itemView.findViewById(R.id.searchImage);
        }
    }
    public AdapterSearch(List<String> searchauthor,List<String> searchdes) {

        this.search = searchauthor;
        this.des = searchdes;
    }
    @Override
    public AdapterSearch.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.searchcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterSearch.ViewHolder viewHolder, int position) {
        //jj-sets text to the different widgets in the cardviews
        viewHolder.searchname.setText(search.get(position));
        viewHolder.searchdes.setText(des.get(position));
        //jj-this needs to change to the corrosponding user profile picture
        viewHolder.searchpic.setImageResource(R.drawable.demo_user_profile_pic);
    }
    @Override
    public int getItemCount() {
        return search.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
