package eLib_theBookManager;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

import eLib_theBookManager.model.User;

public class updateFireStoreUser {
    public static class AccessUser extends AsyncTask<User, Void, Void> {
        private static final String TAG = "AccessUser";

        public AccessUser(){}

        public User updateUser(final User user){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            final DocumentReference docRef = db.collection("User").document(String.valueOf(user.getUseridu()));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Map<String, Object> data = new HashMap<>();
                        data.put("desc", user.getUserabout());
                        data.put("name", user.getUsername());
                        data.put("isbn", user.getUserisbn());
                        data.put("following", user.getfollowingstring());
                        docRef.set(data, SetOptions.merge());
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });
            return user;
        }
        @Override
        protected Void doInBackground(final User... user) {
            try {
                Log.v(TAG, "updateing user =" + String.valueOf(updateUser(user[0]).getUseridu()));
            } catch (Exception e) {
            }
            return null;
        }

    }
}
