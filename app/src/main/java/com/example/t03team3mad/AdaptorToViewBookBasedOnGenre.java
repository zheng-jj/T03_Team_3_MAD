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
import java.util.List;

public class AdaptorToViewBookBasedOnGenre extends RecyclerView.Adapter<AdaptorToViewBookBasedOnGenre.ViewHolder>
{
    List<Book> booksbygenre;
    private Context context;
    private onclickListener onclickListener;

    //Chris - uses onclicklistener to click
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //Chris - cardview layout
        CardView cardView;
        TextView Bookname;
        ImageView imageView;
        TextView Bookdes;
        onclickListener onclickListener;
        ViewHolder(View itemView,onclickListener onclickListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.searchCardView);
            Bookname = (TextView)itemView.findViewById(R.id.titleview);
            Bookdes = (TextView)itemView.findViewById(R.id.descriptionview);
            imageView = (ImageView)itemView.findViewById(R.id.searchImage);
            this.onclickListener=onclickListener;
            itemView.setOnClickListener(this);

        }
        @Override//Chris - get position of the recycler view when clicked
        public void onClick(View v) {
         onclickListener.onclick(getAdapterPosition());
        }
    }


    public AdaptorToViewBookBasedOnGenre(List<Book> booksbygenre, onclickListener onclickListener,Context context) {
        this.booksbygenre = booksbygenre;
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
        viewHolder.Bookname.setText(booksbygenre.get(position).getBooktitle());
        viewHolder.Bookdes.setText(booksbygenre.get(position).getBookabout());
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
        databaseAccess.open();
        Book currentbook = databaseAccess.searchbookbyisbn(booksbygenre.get(position).getIsbn());
        databaseAccess.close();

        //Qh- SETS IMAGE FROM STRING
        String filename = "book" + currentbook.getIsbn()+".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.imageView.setImageBitmap(bmImg);
    }
    @Override
    public int getItemCount() {
        return booksbygenre.size();
    }
    public interface onclickListener{
        void onclick(int position);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

