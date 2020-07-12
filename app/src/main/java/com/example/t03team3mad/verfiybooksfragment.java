package com.example.t03team3mad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class verfiybooksfragment extends Fragment implements AdapterVerify.OnVerifyListener{
    private static final String TAG = "verifybooks";
    private CollectionReference mCollectionBooksNotVerified = FirebaseFirestore.getInstance().collection("BooksNotVerified");
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    List<Book> tobeVerified = new ArrayList<>();
    AdapterVerify verifyadapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.verifybooks,container,false);

        getallnonverifiedbooks();
        RecyclerView verifybooks = (RecyclerView)view.findViewById(R.id.verifyrecycler);
        LinearLayoutManager verifylayout = new LinearLayoutManager(getActivity());
        verifybooks.setLayoutManager(verifylayout);
        verifyadapter  = new AdapterVerify(tobeVerified,this, this.getContext());
        //qh - gets users
        verifybooks.setAdapter(verifyadapter);

        return view;
    }


    public void getallnonverifiedbooks () {
        mCollectionBooksNotVerified
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Book newbook = new Book(document.getString("booktitle"),document.getString("bookauthor"),document.getString("bookabout"),document.getString("bookgenre"),document.getString("bookpdate"),document.getId());
                                tobeVerified.add(newbook);
                            }
                            verifyadapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onVerifyClick(final int position) throws InterruptedException {
        //qh - alert to confirm whether to verify the book
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Verify");
        builder.setMessage("Delete or Authorize book?");
        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Book clickedbook = tobeVerified.get(position);
                Map<String, Object> book = new HashMap<>();
                book.put("booktitle", tobeVerified.get(position).getBooktitle());
                book.put("bookauthor", tobeVerified.get(position).getBookauthor());
                book.put("bookabout", tobeVerified.get(position).getBookabout());
                book.put("bookpdate", tobeVerified.get(position).getPdate());
                book.put("bookgenre", tobeVerified.get(position).getBookgenre());

                book.put("TotalRating", 0);
                book.put("avail", "no");
                book.put("coverurl", "");
                book.put("ratecount", 0);
                book.put("viewcount", 0);
                book.put("uploaded", true);

                String isbn1 = tobeVerified.get(position).getIsbn();
                //qh - adding the book to firebase
                mCollectionBook.document(isbn1).set(book);
                mCollectionBooksNotVerified.document(tobeVerified.get(position).getIsbn()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        tobeVerified.remove(position);
                        verifyadapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                mCollectionBooksNotVerified.document(tobeVerified.get(position).getIsbn()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        tobeVerified.remove(position);
                        verifyadapter.notifyDataSetChanged();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
                return;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }
}
