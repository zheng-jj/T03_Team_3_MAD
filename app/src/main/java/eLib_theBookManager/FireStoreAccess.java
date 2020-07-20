package eLib_theBookManager;

import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import eLib_theBookManager.model.User;

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
                            found[0].setfollowingstring(document.getString("following"));
                            found[0].setAdmin(document.getString("role"));
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
    //jj-this is the one used for search bar
    public static class AccessUser2 extends AsyncTask<String,Void, User>{
        private static final String TAG = "AccessUser2";
        final User[] found = {null};

        public AccessUser2(){}

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
                            found[0].setfollowingstring(document.getString("following"));
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            MainActivity.updateuserview(found[0]);
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


    //jj-for list of users(this takes time)
    public static class AccessUserList extends AsyncTask<String,Void, ArrayList<User>> {
        private static final String TAG = "AccessUser";
        final ArrayList<User> found = new ArrayList<>();

        public AccessUserList() {
        }

        public ArrayList<User> searchusers(final String UIDs) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String[] ListoFIDS = UIDs.split(";");
            Log.v(TAG,"list of following size="+ListoFIDS.length);
            for (final String UID : ListoFIDS) {
                final DocumentReference docRef = db.collection("User").document(UID);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                User toadd = new User(Integer.valueOf(UID), document.get("name").toString(), document.get("isbn").toString(), document.get("desc").toString());
                                toadd.setfollowingstring(document.getString("following"));
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                found.add(toadd);
                                Log.v(TAG,"Added user "+toadd.getUseridu());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
            return found;
        }

        @Override
        protected ArrayList<User> doInBackground(final String... strings) {
            try {
                Log.v(TAG,"following Task complete");
                return searchusers(strings[0]);
            } catch (Exception e) {
            }
            return null;
        }
    }
}
