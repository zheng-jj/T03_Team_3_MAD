package eLib_theBookManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import eLib_theBookManager.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import eLib_theBookManager.model.Feed;
import eLib_theBookManager.model.User;

public class feedfragment extends AppCompatActivity {
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
        //intent to get user
        c = this;
        Intent receivingEnd = getIntent();
        user =receivingEnd.getParcelableExtra("user");
        //recyclerview
        idu = String.valueOf(user.getUseridu());
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList =findViewById(R.id.feedrecycler);
        Log.d("Test",idu);
        //query to get latest 10 feed
        Query query = firebaseFirestore.collection("User").document(idu).collection("Activity").orderBy("position", Query.Direction.DESCENDING).limit(10);

        //puts extracted data into feed class automatically
        FirestoreRecyclerOptions<Feed> options = new FirestoreRecyclerOptions.Builder<Feed>().setQuery(query, Feed.class).build();
        //custom firestore adapter
        adapter = new FirestoreRecyclerAdapter<Feed,FeedViewHolder>(options) {
            @NonNull
            @Override
            public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                //viewholder feedcardview
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.feedcardview,parent,false);
                return new FeedViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull final FeedViewHolder holder, int position, @NonNull final Feed model) {
                //bind data to holder

                holder.activity.setText(model.getActivity());
                //check activity set different text
                if(model.getActivity().equals("Review")){

                    holder.content.setText(model.getUname() +" has made a review with a rating of " +model.getRating()+" for book ~" +model.getTitle());
                }
                else if(model.getActivity().equals("Upvote")){
                    holder.content.setText(model.getUname() + " has upvoted a review for book "+model.getTitle());
                }


            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this));
        mFirestoreList.setAdapter(adapter);





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
    protected void onStart() {
        super.onStart();

        adapter.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
    }



}