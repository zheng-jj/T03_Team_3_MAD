package com.example.t03team3mad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class banusersfragment extends Fragment implements AdapterBan.OnBanListener {
    private static final String TAG = "BanUsers";
    private CollectionReference mCollectionUsers = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionBanned = FirebaseFirestore.getInstance().collection("BannedUsers");
    private CollectionReference mCollectionReviews = FirebaseFirestore.getInstance().collection("Reviews");
    List<User> userList = new ArrayList<>();
    AdapterBan adapterBan;
    String email;
    String password;
    String To;
    String Subject;
    String msg;
    Fragment f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.banusers,container,false);
        getusers();
        f= this;

        RecyclerView banusers = (RecyclerView)view.findViewById(R.id.banrecycler);
        LinearLayoutManager banlayout = new LinearLayoutManager(getActivity());
        banusers.setLayoutManager(banlayout);
        adapterBan  = new AdapterBan(userList,this, this.getContext());
        //qh - gets users
        banusers.setAdapter(adapterBan);


        return view;
    }

    //qh - get all users
    public void getusers () {
        mCollectionUsers
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
        builder.setTitle("Delete User");
        builder.setMessage("Delete User? (All reviews made by the user will be deleted)");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                User clickeduser = userList.get(position);

                final String userid = Integer.toString(userList.get(position).getUseridu());
                addtofirestorebanned(userid, position);

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

    public void deletereviews(final String userid) {
        mCollectionReviews.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                    if (i.getString("uid").equals(userid)){
                        mCollectionReviews.document(i.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });
                    }
                }
            }
        });
    }
    public void sendemail(){
        email = "bookapp1234@gmail.com";
        password="bookapppassword";
        Subject = "Banned from elib";
        To = "swah_jian_oon@hotmail.com";
        msg="Dear Sir/Madam," + System.lineSeparator() +System.lineSeparator()+"You have violated our rules and we have decided to take action and have banned your account."+System.lineSeparator()+ System.lineSeparator()+"If you have any issues regarding this ban, please reply to this email."+System.lineSeparator()+ System.lineSeparator()+ "Regards,"+System.lineSeparator()+ System.lineSeparator()+"Admins";
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO,InternetAddress.parse(To));
            message.setSubject(Subject);
            message.setText(msg);
            new SendMail().execute(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }


    }

    private class SendMail extends AsyncTask<Message,String,String>{
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(f.getContext(),"Please Wait","Sending Email...",true,false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return"Success";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s.equals("Success")){
                AlertDialog.Builder builder = new AlertDialog.Builder(f.getContext());
                builder.setCancelable(false);
                builder.setTitle(Html.fromHtml("<font color='#509324'>Success</font>"));
                builder.setMessage("Mail send successfully.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                builder.show();

            }

        }
    }

    //qh - adds banned user to banned firestore collection so that they can be reinstated
    public void addtofirestorebanned(final String userid, final int position){
        DocumentReference docRef = mCollectionUsers.document(userid);
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
                        mCollectionBanned.document(document.getId()).set(user);
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
        mCollectionUsers.document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                deletereviews(userid);
                userList.remove(position);
                adapterBan.notifyDataSetChanged();
                sendemail();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }
}
