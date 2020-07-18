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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
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

public class verfiybooksfragment extends Fragment implements AdapterVerify.OnVerifyListener{
    private static final String TAG = "verifybooks";
    private CollectionReference mCollectionBooksNotVerified = FirebaseFirestore.getInstance().collection("BooksNotVerified");
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    List<Book> tobeVerified = new ArrayList<>();
    AdapterVerify verifyadapter;
    String email;
    String password;
    String To;
    String Subject;
    String msg;
    Fragment f;
    String title;
    String about;
    String genre;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.verifybooks,container,false);
        f= this;
        getallnonverifiedbooks();
        //qh - setting the adapter
        RecyclerView verifybooks = (RecyclerView)view.findViewById(R.id.verifyrecycler);
        LinearLayoutManager verifylayout = new LinearLayoutManager(getActivity());
        verifybooks.setLayoutManager(verifylayout);
        verifyadapter  = new AdapterVerify(tobeVerified,this, this.getContext());
        verifybooks.setAdapter(verifyadapter);

        return view;
    }

    //qh - goes to the BooksNotVerified collection and retrieves all the documents there
    public void getallnonverifiedbooks () {
        mCollectionBooksNotVerified
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Book newbook = new Book(document.getString("booktitle"),document.getString("bookauthor"),document.getString("bookabout"),document.getString("bookgenre"),document.getString("bookpdate"),document.getId());
                                tobeVerified.add(newbook);
                            }
                            verifyadapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    @Override
    public void onVerifyClick(final int position) throws InterruptedException {
        //qh - alert to confirm whether to verify the book
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Verify");
        builder.setMessage("Delete or Authorize book?");
        builder.setCancelable(false);
        builder.setPositiveButton("Verify", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                Book clickedbook = tobeVerified.get(position);
                title = tobeVerified.get(position).getBooktitle();
                about =tobeVerified.get(position).getBookabout();
                genre = tobeVerified.get(position).getBookgenre();
                //qh - uploaded books are different from books retrieved from the api in that
                //the data is stored directly in firestore. Books retrieved from api dont have any information stored in firestore.
                Map<String, Object> book = new HashMap<>();
                book.put("booktitle", tobeVerified.get(position).getBooktitle());
                book.put("bookauthor", tobeVerified.get(position).getBookauthor());
                book.put("bookabout", tobeVerified.get(position).getBookabout());
                book.put("bookpdate", tobeVerified.get(position).getPdate());
                book.put("bookgenre", tobeVerified.get(position).getBookgenre());

                book.put("TotalRating", 0);
                book.put("avail", "no");
                book.put("coverurl", "");
                book.put("ratecount", 0);
                book.put("viewcount", 0);
                book.put("uploaded", true);

                String isbn1 = tobeVerified.get(position).getIsbn();
                //qh - adding the book to firebase
                mCollectionBook.document(isbn1).set(book);
                mCollectionBooksNotVerified.document(tobeVerified.get(position).getIsbn()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        //qh - this removes the item from the recycler view

                        tobeVerified.remove(position);
                        verifyadapter.notifyDataSetChanged();
                        sendemail(position);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
            }
        });
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                mCollectionBooksNotVerified.document(tobeVerified.get(position).getIsbn()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        //qh - this removes the item from the recycler view
                        tobeVerified.remove(position);
                        verifyadapter.notifyDataSetChanged();
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
    public void sendemail(int position){
        email = "bookapp1234@gmail.com";
        password="bookapppassword";
        Subject = "Book verification";
        To = "swah_jian_oon@hotmail.com";
        msg="Dear Sir/Madam," + System.lineSeparator() +System.lineSeparator()+
            "The book you have submitted has been verified and added into the app."+System.lineSeparator()+ System.lineSeparator()+
            "Details of the book are: "+System.lineSeparator()+ System.lineSeparator()+
                "Book Title: " +title +System.lineSeparator()+ System.lineSeparator()+
                "Book About: " + about+System.lineSeparator()+ System.lineSeparator()+
                "Book Genre: " + genre+System.lineSeparator()+ System.lineSeparator()+

            "If you have any issues regarding this ban, please reply to this email."+System.lineSeparator()+ System.lineSeparator()+
            "Regards,"+System.lineSeparator()+
            "Admins";
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
}
