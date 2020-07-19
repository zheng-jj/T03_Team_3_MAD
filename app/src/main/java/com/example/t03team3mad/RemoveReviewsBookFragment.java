package com.example.t03team3mad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class RemoveReviewsBookFragment extends Fragment implements AdapterDeleteReview.OnReviewListener {
    private static final String TAG = "RemoveReviewsBook";
    private CollectionReference mCollectionBooksReviews = FirebaseFirestore.getInstance().collection("Reviews");
    private CollectionReference mCollectionBooks = FirebaseFirestore.getInstance().collection("Book");
    List<Review> reviewsList = new ArrayList<>();
    AdapterDeleteReview adapterdeletereview;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_removereviewsbook,container,false);
        getReviewBooks();
        //qh - sets the recycler view
        RecyclerView removereviews = (RecyclerView)view.findViewById(R.id.removereviewbookrecycler);
        LinearLayoutManager removelayout = new LinearLayoutManager(getActivity());
        removereviews.setLayoutManager(removelayout);
        adapterdeletereview  = new AdapterDeleteReview(reviewsList,this, this.getContext());
        //qh - gets users
        removereviews.setAdapter(adapterdeletereview);

        return view;
    }

    //qh - gets all the reviews made
    public void getReviewBooks() {
        mCollectionBooksReviews.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                    Review review1 = new Review(Integer.parseInt(i.getString("uid")),i.getString("review"),i.getString("title"),i.getString("isbn"),Integer.parseInt(i.getString("rid")));
                    review1.setSpecialstring(i.getId());
                    Log.d(TAG, review1.getReviewisbn());
                    reviewsList.add(review1);
                }
                adapterdeletereview.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onReviewClick(final int position) throws InterruptedException {
        String userid = Integer.toString(reviewsList.get(position).getReviewidu());
        String reviewid = Integer.toString(reviewsList.get(position).getReviewidu());
        String isbn = reviewsList.get(position).getReviewisbn();

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Delete Review");
        builder.setMessage("Delete Review? (Deleted reviews cannot be restored)");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                String reviewid = Integer.toString(reviewsList.get(position).getReviewidr());
                String isbn = reviewsList.get(position).getReviewisbn();
                Log.d("CHECKING", reviewsList.get(position).getReviewisbn());
                Log.d("CHECKING", reviewid);
                String special = reviewsList.get(position).getSpecialstring();
                deletereview(special);
                deletereviewsub(isbn, reviewid, position);


            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deletereview(final String specialstring){
        mCollectionBooksReviews.document(specialstring).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("CHECKING", "Deleted First");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }

    public void deletereviewsub(final String isbn, final String rid, final int position){
        mCollectionBooks.document(isbn).collection("Reviews").document(rid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reviewsList.remove(position);
                adapterdeletereview.notifyDataSetChanged();
                Log.d("CHECKING", "Deleted Second");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }
}