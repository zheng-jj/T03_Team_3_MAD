package com.example.t03team3mad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Feed;
import com.example.t03team3mad.model.Reviews;
import com.example.t03team3mad.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class feedActivity extends AppCompatActivity {
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    String idu;
    Context c;
    User user;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedfragment);
        c = this;
        Intent receivingEnd = getIntent();
        user =receivingEnd.getParcelableExtra("user");

        idu = String.valueOf(user.getUseridu());

        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList =findViewById(R.id.feedrecycler);
        Log.d("Test",idu);
        Query query = firebaseFirestore.collection("User").document(idu).collection("Activity").orderBy(FieldPath.documentId(), Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Feed> options = new FirestoreRecyclerOptions.Builder<Feed>().setQuery(query,Feed.class).build();
        adapter = new FirestoreRecyclerAdapter<Feed,FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.feedcardview,parent,false);
                return new FeedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FeedViewHolder holder, int position, @NonNull final Feed model) {

                holder.activity.setText(model.getActivity());
                if(model.getActivity().equals("Review")){
                    holder.content.setText(model.getUname() +" has made a review with a rating of " +model.getRating()+" for book ~" +model.getTitle());
                }
                else if(model.getActivity().equals("Upvote")){
                    holder.content.setText(model.getUname() + " has upvoted a review for book ~"+model.getTitle());
                }


            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);
        adapter.startListening();

    }
    private class FeedViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView activity;


        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            content=itemView.findViewById(R.id.content);
            activity=itemView.findViewById(R.id.activity);


        }





    };
    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
    public void onStop(){
        super.onStop();
        adapter.stopListening();    
    }

}
