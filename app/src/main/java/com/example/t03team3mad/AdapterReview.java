package com.example.t03team3mad;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Adapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdapterReview extends RecyclerView.Adapter<AdapterReview.ViewHolder>

{
    Fragment f ;
    // jo - empty list in place of data
    List<Review> mReviewlist = new ArrayList<Review>(){};
    // jo - viewholder
    private CollectionReference mCollectionRefbooks = FirebaseFirestore.getInstance().collection("Reviews");
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView uName;
        TextView uReview;
        ImageView uPic;
        Button upvote;
        TextView points;
        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.rcview);
            uName = (TextView)itemView.findViewById(R.id.uname);
            uReview = (TextView)itemView.findViewById(R.id.ureview);
            uPic = (ImageView)itemView.findViewById(R.id.uimg);
            uName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String idu = String.valueOf(mReviewlist.get(getAdapterPosition()).getReviewidu());
                    DatabaseAccess DBaccess = DatabaseAccess.getInstance(f.getActivity().getApplicationContext());
                    DBaccess.open();
                    User user = DBaccess.searchuserbyid(idu);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("searchuser", user);
                    fragment_user upage = new fragment_user();
                    upage.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(upage,f.getActivity(),"userpage");
                }
            });
            points = itemView.findViewById(R.id.points);
            upvote = itemView.findViewById(R.id.upvote);
            upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCollectionRefbooks.document(String.valueOf(mReviewlist.get(getAdapterPosition()).getReviewidr())).update("vote", FieldValue.increment(1));
                }
            });
        }
    }
    // jo- constructor
    public AdapterReview(List<Review> mReviewlist, Fragment f) {
        this.mReviewlist = mReviewlist;
        this.f = f;
    }
    @Override
    public AdapterReview.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.reviewcardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }
    // jo - onbindviewholder to bind data from list to ids from layout
    @Override
    public void onBindViewHolder(AdapterReview.ViewHolder viewHolder, int position) {

        viewHolder.uName.setText(mReviewlist.get(position).getUname());
        viewHolder.uReview.setText(mReviewlist.get(position).getReviewtext());
        String filename = "user" + (mReviewlist.get(position).getReviewidu())+".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        viewHolder.uPic.setImageBitmap(bmImg);
        viewHolder.points.setText(String.valueOf(mReviewlist.get(position).getReviewpoints()));

    }

    @Override
    public int getItemCount() {
        return mReviewlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}