package com.example.t03team3mad;

import android.content.DialogInterface;
import android.os.AsyncTask;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

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
                    Review review = new Review(Integer.parseInt(i.getString("uid")),i.getString("review"),i.getString("title"),i.getString("isbn"));
                    reviewsList.add(review);
                }
                adapterdeletereview.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onReviewClick(int position) throws InterruptedException {

    }


    //jj- the following is used for notifications
    //jj- when admin decides to clear user reviews, notification is sent
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification(final String clearuid) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body","All your reviews have been banned by the admins!");
                    dataJson.put("title","Warning!");
                    json.put("notification",dataJson);
                    json.put("to","/topics/User"+clearuid);
                    Log.v(TAG,json.toString());
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+"AAAARRpA2ik:APA91bGMQumkw5FL-Xt_yj_ULIjb91TPQIzfi-ZCM4gEHB47wd-W1jORTJsx3YKiSbv-AMlN1zWJOl6peBAFvWkSZ2QFGRPGcHiHvaYjcQZMwRJfm8wKwUiSpR32-u1ODGte42xYQ9gl")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    response.isSuccessful();
                    Log.v(TAG,"response ="+response.isSuccessful());
                    String finalResponse = response.body().string();
                    Log.v(TAG, finalResponse);
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }
}