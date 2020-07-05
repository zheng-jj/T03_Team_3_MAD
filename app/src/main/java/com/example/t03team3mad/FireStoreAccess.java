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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class FireStoreAccess {
    public static class AccessUser extends AsyncTask<String,Void, User>{
        private static final String TAG = "AccessUser";
        final User[] found = {null};
        @Override
        protected User doInBackground(final String... strings) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("User").document(strings[0]);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            found[0] = new User(Integer.valueOf(strings[0]),document.get("name").toString(),document.get("isbn").toString(),document.get("desc").toString());
                            document.get("name");
                            MainActivity.loggedinuser=found[0];
                            Log.v(TAG,found[0].getUsername());
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
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
                return found[0];
            }

        }
    }
}
