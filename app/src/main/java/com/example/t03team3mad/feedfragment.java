package com.example.t03team3mad;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class feedfragment extends Fragment {
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    String idu;
    Book book;
    Context c;

    String text;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo -display fragment
        View view = inflater.inflate(R.layout.subfragment_writereview, container, false);
        c = this.getActivity();



        idu = String.valueOf(MainActivity.loggedinuser.getUseridu());
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList =view.findViewById(R.id.feedrecycler);
        Log.d("Test",idu);
        Query query = firebaseFirestore.collection("User").document(idu).collection("Activity").orderBy("position", Query.Direction.DESCENDING).limit(10);


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
                    text = model.getUname() +" has made a review for the book "+model.getTitle() + System.lineSeparator() +"Rating: "+model.getRating();
                    text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
                    holder.content.setText(text);

                }
                else if(model.getActivity().equals("Upvote")){
                    text = model.getUname() + " has upvoted a review for book "+model.getTitle();
                    text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());
                    holder.content.setText(text);
                }
                holder.content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id= model.getIsbn();
                        AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(id);
                        try {
                            book = tasktogetbook.get();



                            if(book!=null) {
                                mCollectionRef.document(model.getIsbn()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        book.setimglink(documentSnapshot.getString("coverurl"));
                                        Bundle bundle = new Bundle();
                                        bundle.putParcelable("book", book);
                                        bookinfoFragment bpage = new bookinfoFragment();
                                        bpage.setArguments(bundle);
                                        //jj-updated the way we add fragments into the view
                                        MainActivity.addFragment(bpage,getActivity(),"bookinfopage");
                                    }
                                });


                            };
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                });



            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(c));
        mFirestoreList.setAdapter(adapter);
        return view;





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
    @Override
    public void onStop() {
        super.onStop();

        adapter.stopListening();
    }

    

}
