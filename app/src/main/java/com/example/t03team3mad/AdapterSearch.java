package com.example.t03team3mad;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;

public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    List<String> des = new ArrayList<String>(){};
    List<SearchClass> searchlist = new ArrayList<SearchClass>(){};
    private OnSearchListener mOnSearchListener;
    //QH- THIS IS IMPORTANT TO SET IMAGE FROM STRING
    private Context context;

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

    public AdapterSearch(List<SearchClass> searchList, OnSearchListener onSearchListener, Context context) {
        this.mOnSearchListener = onSearchListener;
        this.searchlist = searchList;
        this.context = context;
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

        //SET IMAGE BASED ON CLASS
        //qh -- if object clicked is a book
        //if (searchlist.get(position).getSearchClass() == "Book"){
            //DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
            //databaseAccess.open();
            //Book currentbook = databaseAccess.searchbookbyisbn(searchlist.get(position).getId());
            //databaseAccess.close();

            //QH = SETS IMAGE FROM STRING
            //String filename = "book" + currentbook.getIsbn()+".jpg";
            //Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
            //iewHolder.searchpic.setImageBitmap(bmImg);


        //}

        //qh -- if object clicked is a author
        if (searchlist.get(position).getSearchClass() == "Author"){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
            databaseAccess.open();
            Author currentauthor = databaseAccess.searchauthorbyida(searchlist.get(position).getId());
            databaseAccess.close();

            //QH = SETS IMAGE FROM STRING
            String filename = "author" + currentauthor.getAuthorid()+".jpg";
            Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
            viewHolder.searchpic.setImageBitmap(bmImg);
        }

        //qh -- if object clicked is a user
        if (searchlist.get(position).getSearchClass() == "User"){
            DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
            databaseAccess.open();
            User currentuser = databaseAccess.searchuserbyid(searchlist.get(position).getId());
            databaseAccess.close();

            //QH = SETS IMAGE FROM STRING
            String filename = "user" + currentuser.getUseridu()+".jpg";
            Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
            viewHolder.searchpic.setImageBitmap(bmImg);
        }

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
