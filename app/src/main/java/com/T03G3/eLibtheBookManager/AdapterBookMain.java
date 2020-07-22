package com.T03G3.eLibtheBookManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.T03G3.eLibtheBookManager.model.Book;
import com.squareup.picasso.Picasso;

//jj- all the same format as adapterusermain
public class AdapterBookMain extends RecyclerView.Adapter<AdapterBookMain.ViewHolder>
{
    private static final String TAG = "AdapterBookMain";
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
            //jj-set image from url
            if(mBooklist.get(position).getimglink()== null||mBooklist.get(position).getimglink()==""){
                viewHolder.bookPic.setImageResource(R.drawable.empty);
            }
            else {
                Picasso.with(context).load(mBooklist.get(position).getimglink()).into(viewHolder.bookPic);
            }
            Log.v(TAG,"loading image from "+mBooklist.get(position).getimglink());


        }catch (Exception e){}
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
        if(mBooklist!=null) {
            return mBooklist.size();
        }
        return 0;
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}