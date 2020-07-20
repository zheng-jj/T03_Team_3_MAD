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
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

import java.util.ArrayList;
import java.util.List;
//qh - code to remove reviews
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class RemoveReviewsBookFragment extends Fragment implements AdapterDeleteReview.OnReviewListener {
    private static final String TAG = "RemoveReviewsBook";
    private CollectionReference mCollectionBooksReviews = FirebaseFirestore.getInstance().collection("Reviews");
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
    String title;
    String review;
    private CollectionReference mCollectionRefusers = FirebaseFirestore.getInstance().collection("User");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.fragment_removereviewsbook,container,false);
        f = this;
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
                    Review review1 = new Review(Integer.parseInt(i.getString("uid")),i.getString("review"),i.getString("title"),i.getString("isbn"),Integer.parseInt(i.getString("rid")));
                    review1.setSpecialstring(i.getId());
                    Log.d(TAG, review1.getReviewisbn());
                    reviewsList.add(review1);
                }
                adapterdeletereview.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onReviewClick(final int position) throws InterruptedException {
        final String userid = Integer.toString(reviewsList.get(position).getReviewidu());
        uid = userid;
        String reviewid = Integer.toString(reviewsList.get(position).getReviewidu());
        String isbn = reviewsList.get(position).getReviewisbn();
        title = reviewsList.get(position).getReviewTitle();
        review = reviewsList.get(position).getReviewtext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Delete Review");
        builder.setMessage("Delete Review? (Deleted reviews cannot be restored)");
        builder.setCancelable(false);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                String reviewid = Integer.toString(reviewsList.get(position).getReviewidr());
                String isbn = reviewsList.get(position).getReviewisbn();
                Log.d("CHECKING", reviewsList.get(position).getReviewisbn());
                Log.d("CHECKING", reviewid);
                String special = reviewsList.get(position).getSpecialstring();
                deletereview(special);
                deletereviewsub(isbn, reviewid, position);
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

    public void deletereview(final String specialstring){
        mCollectionBooksReviews.document(specialstring).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getEmail();
                Log.d("CHECKING", "Deleted First");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }

    public void deletereviewsub(final String isbn, final String rid, final int position){
        mCollectionBooks.document(isbn).collection("Reviews").document(rid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                reviewsList.remove(position);
                adapterdeletereview.notifyDataSetChanged();
                Log.d("CHECKING", "Deleted Second");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error deleting document", e);
            }
        });
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
    //jo -email send
    public void sendemail(){
        email = "bookapp1234@gmail.com";
        password="bookapppassword";
        Subject = "Removed Reviews";
        msg="Dear Sir/Madam," + System.lineSeparator() +System.lineSeparator()+
                "One of your review has violated our rules and we have made the decision to take it down."+System.lineSeparator()+ System.lineSeparator()+
                "Details of the reviews are: "+System.lineSeparator()+ System.lineSeparator()+
                "Book: "+ title +System.lineSeparator()+ System.lineSeparator()+
                "Review: "+review+System.lineSeparator()+ System.lineSeparator()+
                "If you have any issues regarding this issue, please reply to this email."+System.lineSeparator()+ System.lineSeparator()+
                "Regards,"+System.lineSeparator()+
                "Admins";
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
    //get email of user to send
    public void getEmail(){

        mCollectionRefusers.document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                To = documentSnapshot.getString("email");
                Log.d("Test","Email: " + To);
                sendemail();
            }
        });


    }

}