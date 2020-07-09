package com.example.t03team3mad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//jj- all the same format as adapterusermain
public class AdapterFavBooksList extends RecyclerView.Adapter<AdapterFavBooksList.ViewHolder>
{
    private static final String TAG = "AdapterFavBooksList";
    Context context;
    List<Book> mBooklist = new ArrayList<Book>(){};
    List<Book> mBooklistToBeRemoved = new ArrayList<Book>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView bookName;
        TextView bookDec;
        ImageView bookPic;
        Button like;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.favbook);
            //jj-get the widget id and assign them to local variable
            bookName = (TextView)itemView.findViewById(R.id.title);
            bookDec = (TextView)itemView.findViewById(R.id.bookdes);
            bookPic = (ImageView)itemView.findViewById(R.id.img);
            like =(Button)itemView.findViewById(R.id.like);
        }
    }
    public AdapterFavBooksList(List<Book> mBooklist) {
        this.mBooklist = mBooklist;
    }
    @Override
    public AdapterFavBooksList.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.favbookscardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final AdapterFavBooksList.ViewHolder viewHolder, int position) {
        try {
            viewHolder.bookName.setText(mBooklist.get(position).getBooktitle());
            viewHolder.bookDec.setText(mBooklist.get(position).getBookabout());
            viewHolder.like.setBackgroundResource(R.drawable.unliked);

            //jj-this needs to change to the corrosponding user profile picture
            if (mBooklist.get(position).getimglink() == null || mBooklist.get(position).getimglink() == "") {
                viewHolder.bookPic.setImageResource(R.drawable.empty);
            } else {
                Picasso.with(context).load(mBooklist.get(position).getimglink()).into(viewHolder.bookPic);
            }
            Log.v(TAG, "loading image from " + mBooklist.get(position).getimglink());
            viewHolder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        Book chosen = mBooklist.get(viewHolder.getAdapterPosition());
                        Log.v(TAG, "clicked on " + chosen.getBooktitle());
                        Log.v(TAG, viewHolder.like.getText().toString());
                        Book[] mBooklistAfterChange;
                        if (viewHolder.like.getBackground().getConstantState() == viewHolder.bookPic.getResources().getDrawable(R.drawable.unliked).getConstantState()) {
                            viewHolder.like.setBackgroundResource(R.drawable.liked);
                            mBooklistToBeRemoved.add(chosen);
                        }
                        else if (viewHolder.like.getBackground().getConstantState() == viewHolder.bookPic.getResources().getDrawable(R.drawable.liked).getConstantState()) {
                            viewHolder.like.setBackgroundResource(R.drawable.unliked);
                            mBooklistToBeRemoved.remove(chosen);
                        }
                        String temp = "";
                        for (Book x : mBooklistToBeRemoved) {
                            temp = temp + (x.getBooktitle());
                        }
                        Log.v(TAG, "all books in mBooklist to be removed: " + temp);
                    } catch (Exception e) {
                    }
                }
            });
        } catch (Exception e){
        }
    }
    @Override
    public int getItemCount() {
        return mBooklist.size();
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}