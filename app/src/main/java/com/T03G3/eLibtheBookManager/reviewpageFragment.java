package com.T03G3.eLibtheBookManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.T03G3.eLibtheBookManager.model.Book;
import com.T03G3.eLibtheBookManager.model.Review;
import com.T03G3.eLibtheBookManager.model.Reviews;
import com.T03G3.eLibtheBookManager.model.User;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class reviewpageFragment extends Fragment {
    private static final String TAG = "authorprofileFragment";
    Map<Integer, Reviews> data = new HashMap<Integer, Reviews>();
    String name;
    Fragment f;
    String isbn;
    String Title;
    int uid;
    int aid;
    List<String> uids = new ArrayList<String>();
    Reviews model1;
    String title;
    private RecyclerView mFirestoreList;
    private FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter adapter;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    List<String> followingid = new ArrayList<String>();
    //all comments by jo


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo - receive bundle from another fragment
        f = this;
        Bundle bundle = this.getArguments();
        final Book book = bundle.getParcelable("book");

        isbn = book.getIsbn();
        uid = MainActivity.loggedinuser.getUseridu();
        title= book.getBooktitle();
        name = MainActivity.loggedinuser.getUsername();
        // jo - display layout
        View view = inflater.inflate(R.layout.fragment_reviewpage,container,false);

        // jo - findviewbyids
        TextView booktitle = view.findViewById(R.id.rtitle);
        ImageView bookimage = view.findViewById(R.id.bookimg);
        if(book.getimglink() == null || book.getimglink() == ""){
            bookimage.setImageResource(R.drawable.empty);

        }
        else {
            // set book image using link
            Picasso.with(this.getActivity()).load(book.getimglink()).into(bookimage);
        }
        booktitle.setText(book.getBooktitle());
        // jo - recyclerview
        firebaseFirestore = FirebaseFirestore.getInstance();
        mFirestoreList = view.findViewById(R.id.rRecyclerView);
        // query for reviews for the book
        Query query = firebaseFirestore.collection("Book").document(isbn).collection("Reviews").orderBy("vote", Query.Direction.DESCENDING);
        //Get result and auto put into class form

        FirestoreRecyclerOptions<Reviews> options = new FirestoreRecyclerOptions.Builder<Reviews>().setQuery(query,Reviews.class).build();
        //Custom firestore adapter that updates realtime

        adapter = new FirestoreRecyclerAdapter<Reviews, ReviewViewHolder>(options) {
            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                // inflate cardview
                View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.reviewcardview,parent,false);
                return new ReviewViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull final Reviews model) {
                // set text for holders
                holder.uName.setText(model.getUname());
                holder.uReview.setText(model.getReview());
                Log.d("Review",model.getReview());

                String filename = "user" + model.getUid()+".jpg";
                AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
                String path = null;
                try {
                    path = task.get();
                    File check = new File(path);
                    int count = 2;
                    while(count>0){
                        Log.v(TAG,"user image is not saved yet");
                        if(check.exists()) {
                            holder.uPic.setImageBitmap(BitmapFactory.decodeFile(path));
                            holder.uPic.invalidate();
                            if(holder.uPic.getDrawable() != null){
                                try {
                                    TimeUnit.MILLISECONDS.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                holder.uPic.setImageBitmap(BitmapFactory.decodeFile(path));
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
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }




                // clicking name directs to user page
                linktouser(holder.uName, model);
                upvote(model,holder.upvote);



            }
        };
        mFirestoreList.setHasFixedSize(true);
        mFirestoreList.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        mFirestoreList.setAdapter(adapter);

        return view;
    }
    // view holder
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

    //get id of activity used to storing data
    public void getaid(final String id, final Map<String, Object> data3){
        mCollectionRefuser.document(id).collection("Activity").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> data =queryDocumentSnapshots.getDocuments();

                    aid = data.size()+1;
                    data3.put("position",aid);
                    mCollectionRefuser.document(id).collection("Activity").document(String.valueOf(aid)).set(data3);



                }
                else{
                    aid = 1;
                    data3.put("position",aid);
                    mCollectionRefuser.document(id).collection("Activity").document(String.valueOf(aid)).set(data3);


                }




            }
        });
    }
    // update activity of everyone who is following the reviewer
    public void getfollowing(final Reviews model){
        mCollectionRefuser.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                final Map<String, Object> data3 = new HashMap<String,Object>();
                data3.put("Activity","Upvote");
                data3.put("Rating",0);
                data3.put("Review", "");
                data3.put("isbn",isbn);
                data3.put("rid",model.getRid());
                data3.put("title",title);
                data3.put("uname",name);

                for(QueryDocumentSnapshot i: queryDocumentSnapshots){
                    String temp = i.getString("following");
                    String[] follwings = temp.split(";");
                    for(String z : follwings){
                        if(z.equals(String.valueOf(uid))){
                            Log.d("Test",uid +"z:" + z + "pass");
                            Log.d("Test","Passed");
                            getaid(i.getId(),data3);
                        }
                        else{
                            Log.d("Test","Fail");
                            Log.d("Test",uid +"z:" + z);
                            continue;
                        }
                    }
                }
                mCollectionRef.document(isbn).collection("Reviews").document(model.getRid()).update("vote", FieldValue.increment(1));


            }
        });

    }
    // set upvote button
    public void upvote(final Reviews model, final Button button){
        mCollectionRefuser.document(String.valueOf(uid)).collection("Upvote").whereEqualTo("rid", model.getRid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v("Test","Upvote CLicked");
                            final Map<String, Object> data4 = new HashMap<String,Object>();

                            data4.put("isbn",isbn);
                            data4.put("uid", uid);
                            data4.put("rid",model.getRid());
                            mCollectionRefuser.document(String.valueOf(uid)).collection("Upvote").add(data4);

                            getfollowing(model);


                        }
                    });
                }
                else{
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getActivity().getApplicationContext(),"You have already upvoted this comment ",Toast.LENGTH_LONG).show();


                        }
                    });
                }
            }
        });

    }
    public void linktouser(final TextView b, Reviews review){
        mCollectionRefuser.document(review.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(final DocumentSnapshot documentSnapshot) {
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = new User(Integer.parseInt(documentSnapshot.getId()),documentSnapshot.getString("name"),documentSnapshot.getString("isbn"),documentSnapshot.getString("desc"));
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("searchuser", user);
                        fragment_user upage = new fragment_user();
                        upage.setArguments(bundle);
                        //jj-updated the way we add fragments into the view
                        MainActivity.addFragment(upage,f.getActivity(),"userpage");
                    }
                });
            }
        });
    }

}


