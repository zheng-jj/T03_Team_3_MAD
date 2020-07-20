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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Reports;
import com.example.t03team3mad.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

//qh - this pages shows all the reports
public class viewreportsfragment extends Fragment implements AdapterViewReports.OnViewReportsListener {
    private static final String TAG = "viewreports";
    private CollectionReference mCollectionBooks = FirebaseFirestore.getInstance().collection("Book");
    List<Review> reviewsList = new ArrayList<>();
    AdapterDeleteReview adapterdeletereview;
    String email;
    String password;
    String To;
    String Subject;
    String msg;
    Fragment f;
    String uid;
    private CollectionReference mCollectionUsers = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionBanned = FirebaseFirestore.getInstance().collection("BannedUsers");
    private CollectionReference mCollectionReports = FirebaseFirestore.getInstance().collection("Reports");
    private CollectionReference mCollectionReviews = FirebaseFirestore.getInstance().collection("Reviews");
    List<Reports> reports = new ArrayList<>();
    AdapterViewReports viewreportsadapter;
    DatabaseReference databaseReference;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.viewreports,container,false);
        f = this;



        getallreports();

        //qh - setting the adapter
        RecyclerView viewreports = (RecyclerView)view.findViewById(R.id.reportrecycler);
        LinearLayoutManager viewreportslayout = new LinearLayoutManager(getActivity());
        viewreports.setLayoutManager(viewreportslayout);
        viewreportsadapter  = new AdapterViewReports(reports,this, this.getContext());
        viewreports.setAdapter(viewreportsadapter);

        return view;
    }

    @Override
    public void onViewReportsClick(final int position) throws InterruptedException {
        //qh - alert to confirm whether to verify the book
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Report");
        builder.setMessage("Delete Report or Ban User?");
        builder.setCancelable(false);
        builder.setPositiveButton("Ban User", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                final String userid = reports.get(position).getAid();
                uid = userid;
                addtofirestorebanned(userid, position);

            }
        });
        builder.setNegativeButton("Remove Report", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                mCollectionReports.document(reports.get(position).getSpecialString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        //qh - this removes the item from the recycler view
                        reports.remove(position);
                        viewreportsadapter.notifyDataSetChanged();
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

    //qh- get all reports from the Reports collection on the firestore
    public void getallreports () {
        mCollectionReports
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Reports newreport = new Reports();
                                newreport.setAid(document.getString("aid"));
                                newreport.setUid(document.getString("uid"));
                                newreport.setReason(document.getString("reason"));
                                newreport.setAname(document.getString("aname"));
                                newreport.setUname(document.getString("uname"));
                                newreport.setSpecialString(document.getId());
                                Log.d(TAG, newreport.getSpecialString());
                                Log.d(TAG, newreport.getAid());
                                Log.d(TAG, newreport.getAname());
                                reports.add(newreport);
                            }
                            viewreportsadapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //qh - adds the user to the banned user collection on the firestore
    public void addtofirestorebanned(final String userid, final int position){
        DocumentReference docRef = mCollectionUsers.document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        To = document.getString("email");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        Map<String, Object> user = new HashMap<>();
                        user.put("desc", document.getString("desc"));
                        user.put("following", document.getString("following"));
                        user.put("isbn", document.getString("isbn"));
                        user.put("name", document.getString("name"));
                        user.put("role", "User");
                        user.put("email", document.getString("email"));
                        mCollectionBanned.document(document.getId()).set(user);
                        //qh - removed this because it causes crash
                        sendemail();
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
    //qh - removes users from the User collection on the firestore
    public void deleteuser(final String userid, final int position){
        mCollectionUsers.document(userid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                viewreportsadapter.notifyDataSetChanged();
                mCollectionReports.document(reports.get(position).getSpecialString()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        //qh - this removes the item from the recycler view
                        reports.remove(position);
                        viewreportsadapter.notifyDataSetChanged();
                        deletereviews(userid);
                        setbanned(userid);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
                //sendemail();
                //sendNotification(userid);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }
    //qh - removes all reviews made by the user
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
    //qh - sets banned on the realtime database
    public void setbanned(final String userid){
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(snapshot.getKey().equals(userid)) {
                        databaseReference.child(userid).child("banned").setValue(true);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });
    }
    //jo -email send
    public void sendemail(){
        // assign the string
        email = "bookapp1234@gmail.com";
        password="bookapppassword";
        Subject = "Ban from elib";
        msg="Dear Sir/Madam," + System.lineSeparator() +System.lineSeparator()+"You have violated our rules and we have decided to take action and have banned your account."+System.lineSeparator()+ System.lineSeparator()+"If you have any issues regarding this ban, please reply to this email."+System.lineSeparator()+ System.lineSeparator()+ "Regards,"+System.lineSeparator()+"Admins";

        // jo-set properties for email
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");
        // jo-login to the bookapp1234@gmail.com
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });

        try {
            // attach message , destination email and subject
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
    //jo- For display purposes
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
                builder.setMessage("User has been notified.");
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
    //jo- not used anymmore
    public void getEmail(){

        mCollectionUsers.document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                To = documentSnapshot.getString("email");
                Log.d("Test","Email: " + To);
                sendemail();
            }
        });


    }

}
