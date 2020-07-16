package com.example.t03team3mad;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
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

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class AdapterBan extends RecyclerView.Adapter<AdapterBan.ViewHolder>
{
    List<User> userlist = new ArrayList<User>(){};
    private static final String TAG = "AdapterSearch";
    private OnBanListener mOnBanListener;
    private Context context;
    private CollectionReference mCollectionUser = FirebaseFirestore.getInstance().collection("User");


    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textdescription;
        TextView texttitle;
        ImageView imageView;
        OnBanListener onBanListener;
        ViewHolder(View itemView, OnBanListener onBanListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.searchCardView);
            textdescription = (TextView)itemView.findViewById(R.id.descriptionview);
            texttitle = (TextView)itemView.findViewById(R.id.titleview);
            imageView = (ImageView)itemView.findViewById(R.id.searchImage);
            this.onBanListener = onBanListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {

                onBanListener.onBanClick(getAdapterPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnBanListener {
        void onBanClick(int position) throws InterruptedException;
    }

    public AdapterBan(List<User> userList, OnBanListener onBanListener, Context context) {
        this.mOnBanListener = onBanListener;
        this.userlist = userList;
        this.context = context;
    }
    @Override
    public AdapterBan.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.searchcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnBanListener);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterBan.ViewHolder viewHolder, int position) {
        if (userlist.get(position).getUsername() == null) {
            viewHolder.texttitle.setText("no username");
        }
        else{
            viewHolder.texttitle.setText(userlist.get(position).getUsername());
        }

        if (userlist.get(position).getUserabout() == null){
            viewHolder.textdescription.setText("no description");
        }
        else {
            viewHolder.textdescription.setText(userlist.get(position).getUserabout());
        }

        String path = null;
        try {
            path = banusersfragment.getimagesearch(userlist.get(position));
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.v(TAG,"image id being used = user+"+ userlist.get(position).getUseridu());
        //String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        //jj gets image from firebase and saves to local storage
        //sets the profile image
        File check = new File(path);
        int count = 2;
        while(count>0){
            Log.v(TAG,"user image is not saved yet");
            if(check.exists()) {
                viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(path));
                viewHolder.imageView.invalidate();
                if(viewHolder.imageView.getDrawable() != null){
                    try {
                        TimeUnit.MILLISECONDS.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    viewHolder.imageView.setImageBitmap(BitmapFactory.decodeFile(path));
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
    @Override
    public int getItemCount() {
        return userlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
