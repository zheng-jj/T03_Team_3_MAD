package com.T03G3.eLibtheBookManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.T03G3.eLibtheBookManager.model.Book;
import com.T03G3.eLibtheBookManager.model.SearchClass;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
//qh - adapter for search
public class AdapterSearch extends RecyclerView.Adapter<AdapterSearch.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    private static final String TAG = "AdapterSearch";
    List<String> des = new ArrayList<String>(){};
    List<SearchClass> searchlist = new ArrayList<SearchClass>(){};
    private OnSearchListener mOnSearchListener;
    //QH- THIS IS IMPORTANT TO SET IMAGE FROM STRING
    private Context context;
    List<Book> newbooklist=new ArrayList<>();
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");


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
            try {
                onSearchListener.onSearchClick(getAdapterPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnSearchListener {
        void onSearchClick(int position) throws InterruptedException;
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

        if (searchlist.get(position).getSearchClass() == "Book"){
            //DatabaseAccess databaseAccess = DatabaseAccess.getInstance(this.context);
            //databaseAccess.open();
            //Book currentbook = databaseAccess.searchbookbyisbn(searchlist.get(position).getId());
            //databaseAccess.close();

            //QH = SETS IMAGE FROM STRING
            //String filename = "book" + currentbook.getIsbn()+".jpg";
            //Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
            //iewHolder.searchpic.setImageBitmap(bmImg);
            if(searchlist.get(position).getimglink() == null || searchlist.get(position).getimglink() == ""){
                viewHolder.searchpic.setImageResource(R.drawable.empty);
                Log.v(TAG,searchlist.get(position).getimglink() + "IMG LINK");
            }
            else {
                Log.v(TAG,searchlist.get(position).getimglink() + "IMG LINK");
                Picasso.with(context).load(searchlist.get(position).getimglink()).into(viewHolder.searchpic);
            }


        }


        //qh -- if object clicked is a user
        if (searchlist.get(position).getSearchClass() == "User"){

            String path = null;
            try {
                path = searchbarFragment.getimagesearch(searchlist.get(position));
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Log.v(TAG,"image id being used = user+"+ searchlist.get(position).getId());
            //String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
            //jj gets image from firebase and saves to local storage
            //sets the profile image
            File check = new File(path);
            int count = 2;
            while(count>0){
                Log.v(TAG,"user image is not saved yet");
                if(check.exists()) {
                    viewHolder.searchpic.setImageBitmap(BitmapFactory.decodeFile(path));
                    viewHolder.searchpic.invalidate();
                    if(viewHolder.searchpic.getDrawable() != null){
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        viewHolder.searchpic.setImageBitmap(BitmapFactory.decodeFile(path));
                        break;
                    }
                    else {
                        continue;
                    }
                }
                else{
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                count=count-1;
            }

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
