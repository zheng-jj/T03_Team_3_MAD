package com.example.t03team3mad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;

import java.util.ArrayList;
import java.util.List;

public class AdaptorToViewBookBasedOnGenre extends RecyclerView.Adapter<AdaptorToViewBookBasedOnGenre.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    List<String> des = new ArrayList<String>(){};
    List<SearchClass> searchlist = new ArrayList<SearchClass>(){};

    //set image from string
    private Context context;
    private onclickListener onclickListener;

    //uses onclicklistener to click
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //cardview layout
        CardView cardView;
        TextView searchname;
        ImageView searchpic;
        TextView searchdes;
        onclickListener onclickListener;
        ViewHolder(View itemView,onclickListener onclickListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.searchCardView);
            searchname = (TextView)itemView.findViewById(R.id.titleview);
            searchdes = (TextView)itemView.findViewById(R.id.descriptionview);
            searchpic = (ImageView)itemView.findViewById(R.id.searchImage);
            this.onclickListener=onclickListener;
            itemView.setOnClickListener(this);

        }
        @Override//get position of the recycler view when clicked
        public void onClick(View v) {
         onclickListener.onclick(getAdapterPosition());
        }
    }


    public AdaptorToViewBookBasedOnGenre(List<SearchClass> searchList, onclickListener onclickListener,Context context) {
        this.searchlist = searchList;
        this.context = context;
        this.onclickListener=onclickListener;
    }
    //cardview view
    @Override
    public AdaptorToViewBookBasedOnGenre.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.searchcardview, parent, false);
        AdaptorToViewBookBasedOnGenre.ViewHolder viewHolder = new AdaptorToViewBookBasedOnGenre.ViewHolder(contactView,onclickListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdaptorToViewBookBasedOnGenre.ViewHolder viewHolder, int position) {
        viewHolder.searchname.setText(searchlist.get(position).getSearchName());
        viewHolder.searchdes.setText(searchlist.get(position).getSearchDes());
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        databaseAccess.open();
        Book currentbook = databaseAccess.searchbookbyisbn(searchlist.get(position).getId());
        databaseAccess.close();

        //SETS IMAGE FROM STRING
        String filename = "book" + currentbook.getIsbn()+".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.searchpic.setImageBitmap(bmImg);
    }
    @Override
    public int getItemCount() {
        return searchlist.size();
    }
    public interface onclickListener{
        void onclick(int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

