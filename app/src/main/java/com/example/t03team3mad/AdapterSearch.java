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
import com.example.t03team3mad.model.SearchClass;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    List<String> des = new ArrayList<String>(){};
    List<SearchClass> searchlist = new ArrayList<SearchClass>(){};
    private OnSearchListener mOnSearchListener;
    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView searchname;
        ImageView searchpic;
        TextView searchdes;
        OnSearchListener onSearchListener;
        ViewHolder(View itemView, OnSearchListener onSearchListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.searchCardView);

            searchname = (TextView)itemView.findViewById(R.id.titleview);
            searchdes = (TextView)itemView.findViewById(R.id.descriptionview);
            searchpic = (ImageView)itemView.findViewById(R.id.searchImage);
            this.onSearchListener = onSearchListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onSearchListener.onSearchClick(getAdapterPosition());
        }
    }
    public interface OnSearchListener {
        void onSearchClick(int position);
    }

    public AdapterSearch(List<SearchClass> searchList, OnSearchListener onSearchListener) {
        this.mOnSearchListener = onSearchListener;
        this.searchlist = searchList;
    }
    @Override
    public AdapterSearch.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.searchcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnSearchListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterSearch.ViewHolder viewHolder, int position) {
        viewHolder.searchname.setText(searchlist.get(position).getSearchName());
        viewHolder.searchdes.setText(searchlist.get(position).getSearchDes());
        viewHolder.searchpic.setImageResource(R.drawable.demo_user_profile_pic);
    }
    @Override
    public int getItemCount() {
        return searchlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
