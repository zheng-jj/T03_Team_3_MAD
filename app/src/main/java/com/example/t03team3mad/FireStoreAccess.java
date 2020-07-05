package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.bind.JsonTreeReader;

import org.json.JSONException;

import java.io.IOException;

public class FireStoreAccess {
    public static class AccessUser extends AsyncTask<String,Void, User>{
        private static final String TAG = "AccessUser";
        final User[] found = {null};

        public AccessUser(){}

        public User searchuser(final String UID){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("User").document(UID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            found[0]=new User(Integer.valueOf(UID),document.get("name").toString(),document.get("isbn").toString(),document.get("desc").toString());
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            MainActivity.updateUserLogged(found[0]);
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            if(found[0] == null){
                return null;
            }
            else {
                Log.v(TAG,"returning correct user");
                return found[0];
            }
        }
        @Override
        protected User doInBackground(final String... strings) {
            try {
                return searchuser(strings[0]);
            }catch (Exception e){
            return null;}

        }

        @Override
        protected void onPostExecute(User user) {
        }
    }
}
