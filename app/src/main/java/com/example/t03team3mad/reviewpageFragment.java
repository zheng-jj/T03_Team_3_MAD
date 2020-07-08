package com.example.t03team3mad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.Reviews;
import com.example.t03team3mad.model.User;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class reviewpageFragment extends Fragment {
    private static final String TAG = "authorprofileFragment";
    Map<Integer, Reviews> data = new HashMap<Integer, Reviews>();
    String name;
    Fragment f;
    String isbn;
    String Title;
    Reviews model1;
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Book");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo - receive bundle from another fragment
        f = this;
        Bundle bundle = this.getArguments();
        final Book book = bundle.getParcelable("book");
        isbn = book.getIsbn();
        // jo - display layout
        View view = inflater.inflate(R.layout.fragment_reviewpage,container,false);

        // jo - findviewbyids
        TextView booktitle = view.findViewById(R.id.rtitle);
        ImageView bookimage = view.findViewById(R.id.bookimg);
        String filename = "book" + book.getIsbn() +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        bookimage.setImageBitmap(bmImg);
        booktitle.setText(book.getBooktitle());
        // jo - recyclerview
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.rRecyclerView);

        Query query = firebaseFirestore.collection("Book").document(isbn).collection("Reviews").orderBy("vote", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Reviews> options = new FirestoreRecyclerOptions.Builder<Reviews>().setQuery(query,Reviews.class).build();

        adapter = new FirestoreRecyclerAdapter<Reviews, ReviewViewHolder>(options) {
            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewcardview,parent,false);
                return new ReviewViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull final Reviews model) {
                holder.uName.setText(model.getUname());
                holder.uReview.setText(model.getReview());
                String filename = "user" + (model.getIsbn())+".jpg";
                Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
                holder.uPic.setImageBitmap(bmImg);
                holder.points.setText(String.valueOf(model.getVote()));
                holder.uName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String idu = String.valueOf(model.getUid());
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
                holder.upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.v("Test","Upvote CLicked");

                        mCollectionRef.document(isbn).collection("Reviews").document(model.getRid()).update("vote", FieldValue.increment(1));

                    }
                });

            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mFirestoreList.setAdapter(adapter);

        return view;
    }
    private class ReviewViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView uName;
        TextView uReview;
        ImageView uPic;
        Button upvote;
        TextView points;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.rcview);
            uName = (TextView)itemView.findViewById(R.id.uname);
            uReview = (TextView)itemView.findViewById(R.id.ureview);
            uPic = (ImageView)itemView.findViewById(R.id.uimg);

            points = itemView.findViewById(R.id.points);
            upvote = itemView.findViewById(R.id.upvote);


        }




    };

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}


