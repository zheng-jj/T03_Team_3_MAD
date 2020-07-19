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
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class unbanusersfragment extends Fragment implements AdapterBan.OnBanListener {
    private static final String TAG = "BanUsers";
    private CollectionReference mCollectionUsers = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionBanned = FirebaseFirestore.getInstance().collection("BannedUsers");
    private CollectionReference mCollectionReviews = FirebaseFirestore.getInstance().collection("Reviews");
    List<User> userList = new ArrayList<>();
    AdapterBan adapterBan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.unbanusers,container,false);
        getbannedusers();

        RecyclerView banusers = (RecyclerView)view.findViewById(R.id.unbanrecycler);
        LinearLayoutManager banlayout = new LinearLayoutManager(getActivity());
        banusers.setLayoutManager(banlayout);
        adapterBan  = new AdapterBan(userList,this, this.getContext());
        //qh - gets users
        banusers.setAdapter(adapterBan);


        return view;
    }

    //qh - get all banned users
    public void getbannedusers () {
        mCollectionBanned
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                User newuser = new User(Integer.parseInt(document.getId()),document.getString("name"),document.getString("desc"));
                                userList.add(newuser);
                            }
                            adapterBan.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    @Override
    public void onBanClick(final int position) throws InterruptedException {
        Log.d(TAG, "BanClick");
        //qh - alert to confirm whether to verify the book
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Restore User");
        builder.setMessage("Restore User?");
        builder.setCancelable(false);
        builder.setPositiveButton("Restore", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                User clickeduser = userList.get(position);
                final String userid = Integer.toString(userList.get(position).getUseridu());
                addbacktofirestore(userid, position);
                sendNotification(userid);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static String getimagesearch(User user) throws ExecutionException, InterruptedException {
        String filename = "user" + user.getUseridu() +".jpg";
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
    }

    //qh - adds back user to User firestore collection
    public void addbacktofirestore(final String userid, final int position){
        DocumentReference docRef = mCollectionBanned.document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> user = new HashMap<>();
                        user.put("desc", document.getString("desc"));
                        user.put("following", document.getString("following"));
                        user.put("isbn", document.getString("isbn"));
                        user.put("name", document.getString("name"));
                        user.put("role", "User");
                        mCollectionUsers.document(document.getId()).set(user);
                        deleteuser(userid, position);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
    public void deleteuser(final String userid, final int position){
        mCollectionBanned.document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                userList.remove(position);
                adapterBan.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }


    //jj- the following is used for notifications
    //jj- when admin decides to unban user, notification is sent
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification(final String unbanuid) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body","You have been unbanned by the admins!");
                    dataJson.put("title","Check it out!");
                    json.put("notification",dataJson);
                    json.put("to","/topics/User"+unbanuid);
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
