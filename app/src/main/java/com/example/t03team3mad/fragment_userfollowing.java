package com.example.t03team3mad;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class fragment_userfollowing extends Fragment {

    List<User> userFollowing = new ArrayList<>();
    AdapterUserMain UserAdapter;
    private static final String TAG = "userfollowingFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_following,container,false);
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }


        //jj - load favourite user books recyclerview
        RecyclerView followings = (RecyclerView) view.findViewById(R.id.followerrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        followings.setLayoutManager(llm);


        loaduserfollowing(MainActivity.loggedinuser.getfollowingstring());
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        UserAdapter = new AdapterUserMain(userFollowing);
        //jj- set the recyclerview object to its adapter
        followings.setAdapter(UserAdapter);

        return view;
    }
    private CollectionReference mCollectionUsers = FirebaseFirestore.getInstance().collection("User");

    //jj-loads list of users the user is following

    public void loaduserfollowing(String UIDs){
        final List<String> ListoFIDS = Arrays.asList(UIDs.split(";"));
        mCollectionUsers.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot dss : data) {
                        if (ListoFIDS.contains(dss.getReference().getId())) {
                            User toadd = new User(Integer.valueOf(dss.getReference().getId()), dss.get("name").toString(), dss.get("isbn").toString(), dss.get("desc").toString());
                            userFollowing.add(toadd);
                        } else {
                            continue;
                        }
                    }
                    for(User user : userFollowing){
                        loaduserURL(user);
                        Log.v(TAG,"User image urls = "+user.getimgurl());
                    }
                    UserAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    public Void loaduserURL(final User user) {
        storageRef.child("user"+user.getUseridu()+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Log.v(TAG,"uri is ="+uri.toString());
                user.setimgurl(uri.toString());
                for(User x : userFollowing){
                    if (x.getUseridu()==user.getUseridu()){
                        x.setimgurl(user.getimgurl());
                    }
                }
                UserAdapter.notifyDataSetChanged();
                }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                user.setimgurl("");
            }
        });
        return null;
    }
}
