package com.T03G3.eLibtheBookManager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.T03G3.eLibtheBookManager.model.Book;

import java.util.ArrayList;
import java.util.List;
// qh -- this adapter is to show author's published books
public class AdapterAuthor extends RecyclerView.Adapter<AdapterAuthor.ViewHolder> {
    List<Book> booklist1 = new ArrayList<Book>(){};
    private AdapterAuthor.OnSearchListener mOnSearchListener;
    private Context context;

    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView authorbookname;
        ImageView authorbookpic;
        TextView authorbookdes;
        AdapterAuthor.OnSearchListener onSearchListener;
        ViewHolder(View itemView, OnSearchListener onSearchListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.authorbookscardview);

            authorbookname = (TextView)itemView.findViewById(R.id.authorbooktitle);
            authorbookdes = (TextView)itemView.findViewById(R.id.authorbookdes);
            authorbookpic = (ImageView)itemView.findViewById(R.id.authorbookimage);

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

    public AdapterAuthor(List<Book> bookList, OnSearchListener onSearchListener, Context context) {
        this.mOnSearchListener = onSearchListener;
        this.booklist1 = bookList;
        this.context = context;
    }
    @Override
    public AdapterAuthor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.authorbookscardview, parent, false);
        AdapterAuthor.ViewHolder viewHolder = new ViewHolder(contactView, mOnSearchListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterAuthor.ViewHolder viewHolder, int position) {
        viewHolder.authorbookname.setText(booklist1.get(position).getBooktitle());
        viewHolder.authorbookdes.setText(booklist1.get(position).getBookabout());

        //QH = SETS IMAGE FROM STRING
        String filename = "book" + booklist1.get(position).getIsbn();
        int id = context.getResources().getIdentifier(filename, "drawable", context.getPackageName());
        System.out.println(id);
        viewHolder.authorbookpic.setImageResource(id);

    }


    @Override
    public int getItemCount() {
        return booklist1.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}
