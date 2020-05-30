package com.example.t03team3mad;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

//jj- all the same format as adapterusermain
public class AdapterFavBooksList extends RecyclerView.Adapter<AdapterFavBooksList.ViewHolder>
{
    private static final String TAG = "AdapterFavBooksList";
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
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.favbookscardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final AdapterFavBooksList.ViewHolder viewHolder, int position) {
        viewHolder.bookName.setText(mBooklist.get(position).getBooktitle());
        viewHolder.bookDec.setText(mBooklist.get(position).getBookabout());
        viewHolder.like.setText("Unlike");

        //jj-this needs to change to the corrosponding user profile picture
        viewHolder.bookPic.setImageResource(R.drawable.demo_book_pic);
        viewHolder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book chosen = mBooklist.get(viewHolder.getAdapterPosition());
                Log.v(TAG,"clicked on " + chosen.getBooktitle());
                Log.v(TAG,viewHolder.like.getText().toString()) ;
                Book[] mBooklistAfterChange;
                if(viewHolder.like.getText().toString()=="Unlike"){
                    viewHolder.like.setText("Like");
                    mBooklistToBeRemoved.add(chosen);
                }
                else if(viewHolder.like.getText().toString()=="Like"){
                    viewHolder.like.setText("Unlike");
                    mBooklistToBeRemoved.remove(chosen);
                }
                String temp = "";
                for (Book x :mBooklistToBeRemoved){
                    temp=temp+(x.getBooktitle());
                }
                Log.v(TAG,"all books in mBooklist to be removed: "+temp);
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