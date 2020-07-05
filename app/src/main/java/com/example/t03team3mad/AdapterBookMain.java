package com.example.t03team3mad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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
    private Context context;
    //qh - implemented clicking
    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView bookName;
        ImageView bookPic;
        Book book;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.bookCardView);
            //jj-get the widget id and assign them to local variable
            bookName = (TextView)itemView.findViewById(R.id.bookname);
            bookPic = (ImageView)itemView.findViewById(R.id.bookimage);
        }
    }
    public AdapterBookMain(List<Book> mBooklist , Context context) {

        this.mBooklist = mBooklist;
        this.context = context;
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
    public void onBindViewHolder(final AdapterBookMain.ViewHolder viewHolder, final int position) {
        try{
        viewHolder.bookName.setText(mBooklist.get(position).getBooktitle());
        //jj-this needs to change to the corrosponding user profile picture

        //QH = SETS IMAGE FROM STRING
        String filename = "book" + mBooklist.get(position).getIsbn() +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.bookPic.setImageBitmap(bmImg);}catch (Exception e){}
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book bookchosen = mBooklist.get(viewHolder.getAdapterPosition());
                bookinfoFragment nextFrag= new bookinfoFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("currentbook", bookchosen);  // Key, value
                nextFrag.setArguments(bundle);
                MainActivity.addFragment(nextFrag,viewHolder.bookName.getContext(),"findThisFragment");
            }
        });
    }
    @Override
    public int getItemCount() {
        return mBooklist.size();
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}