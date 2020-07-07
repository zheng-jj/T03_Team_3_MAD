package com.example.t03team3mad;

import android.content.Context;
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
import com.example.t03team3mad.model.User;

public class AdapterUserMain extends RecyclerView.Adapter<AdapterUserMain.ViewHolder>
{
    private static final String TAG = "AdapterUserMain";
    List<User> mUserlist = new ArrayList<User>(){};
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView userName;
        TextView userDes;
        ImageView userPic;

        ViewHolder(View itemView) {
            //takes the cardview from parameters
            super(itemView);
            //jj-finds the cardview from the layout
            cardView = (CardView)itemView.findViewById(R.id.userCardView);
            //jj-get the widget id and assign them to local variable
            userName = (TextView)itemView.findViewById(R.id.username);
            userDes = (TextView)itemView.findViewById(R.id.userdes);
            userPic = (ImageView)itemView.findViewById(R.id.userpic);
        }
    }
    public AdapterUserMain(List<User> mUserlist) {
        this.mUserlist = mUserlist;
    }
    @Override
    public AdapterUserMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //jj-Complete the card view for users
        View contactView = inflater.inflate(R.layout.usercardview, parent, false);
        //jj-calls the function VIEWHOLDER with the cardview view in the parameters
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final AdapterUserMain.ViewHolder viewHolder, int position) {
        //jj-sets text to the different widgets in the cardviews
        viewHolder.userName.setText(mUserlist.get(position).getUsername());
        viewHolder.userDes.setText(mUserlist.get(position).getUserabout());
        String filename = "user" + mUserlist.get(position).getUseridu()+".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.userPic.setImageBitmap(bmImg);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User chosen = mUserlist.get(viewHolder.getAdapterPosition());
                Log.v(TAG,"chosen ="+chosen.getUseridu());
                fragment_user nextFrag= new fragment_user();
                Bundle bundle = new Bundle();
                bundle.putParcelable("searchuser", chosen);  // Key, value
                nextFrag.setArguments(bundle);
                MainActivity.addFragment(nextFrag,viewHolder.userName.getContext(),"UserFragmentFromFollow");
            }
        });
    }
    @Override
    public int getItemCount() {
        return mUserlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}