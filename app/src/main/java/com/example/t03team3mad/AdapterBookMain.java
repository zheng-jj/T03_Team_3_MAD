package com.example.t03team3mad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    AdapterBookMain.OnBookMainListener monBookMainListener;
    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView bookName;
        ImageView bookPic;
        AdapterBookMain.OnBookMainListener onBookMainListener;
        ViewHolder(View itemView, OnBookMainListener onBookMainListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.bookCardView);
            //jj-get the widget id and assign them to local variable
            bookName = (TextView)itemView.findViewById(R.id.bookname);
            bookPic = (ImageView)itemView.findViewById(R.id.bookimage);

            this.onBookMainListener = onBookMainListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onBookMainListener.onBookMainClick(getAdapterPosition());
        }
    }
    public AdapterBookMain(List<Book> mBooklist , OnBookMainListener onBookMainListener, Context context) {

        this.mBooklist = mBooklist;
        this.monBookMainListener = onBookMainListener;
        this.context = context;
    }
    @Override
    public AdapterBookMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.bookdisplaycardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, monBookMainListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterBookMain.ViewHolder viewHolder, int position) {
        try{
        viewHolder.bookName.setText(mBooklist.get(position).getBooktitle());
        //jj-this needs to change to the corrosponding user profile picture

        //QH = SETS IMAGE FROM STRING
        String filename = "book" + mBooklist.get(position).getIsbn() +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.bookPic.setImageBitmap(bmImg);}catch (Exception e){}

    }
    @Override
    public int getItemCount() {
        return mBooklist.size();
    }
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    //qh-- interface for click
    public interface OnBookMainListener {
        void onBookMainClick(int position);
    }
}