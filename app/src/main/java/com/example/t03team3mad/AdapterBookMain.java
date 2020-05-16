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

import com.example.t03team3mad.model.Book;
//jj- all the same format as adapterusermain
public class AdapterBookMain extends RecyclerView.Adapter<AdapterBookMain.ViewHolder>
{
    List<Book> mBooklist = new ArrayList<Book>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView bookName;
        ImageView bookPic;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.bookCardView);
            //jj-get the widget id and assign them to local variable
            bookName = (TextView)itemView.findViewById(R.id.bookname);
            bookPic = (ImageView)itemView.findViewById(R.id.bookimage);
        }
    }
    public AdapterBookMain(List<Book> mBooklist) {
        this.mBooklist = mBooklist;
    }
    @Override
    public AdapterBookMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.bookdisplaycardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterBookMain.ViewHolder viewHolder, int position) {
        viewHolder.bookName.setText(mBooklist.get(position).getBooktitle());
        //jj-this needs to change to the corrosponding user profile picture
        viewHolder.bookPic.setImageResource(R.drawable.demo_book_pic);
    }
    @Override
    public int getItemCount() {
        return mBooklist.size();
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}