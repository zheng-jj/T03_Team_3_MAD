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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
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

public class reportuserfragment extends Fragment {
    EditText report;
    TextView username;
    Button send;
    User user;
    String email;
    String password;
    String To;
    String Subject;
    String msg;
    String reportedname;
    String reason;
    Fragment f;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Reports");
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.report,container,false);

        f = this;
        report = view.findViewById(R.id.reportcontent);
        send =view.findViewById(R.id.report);
        username = view.findViewById(R.id.nameuser);
        Bundle bundle = this.getArguments();
        user = bundle.getParcelable("userreport");
        username.setText(user.getUsername());
        reportedname =user.getUsername();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reason = String.valueOf(report.getText());
                if(reason.length()==0){
                    Toast.makeText(getActivity().getApplicationContext(),"No empty input",Toast.LENGTH_SHORT).show();


                }
                else if(reason.length() > 100){
                    Toast.makeText(getActivity().getApplicationContext(),"Max word count 100",Toast.LENGTH_SHORT).show();

                }
                else{
                    final Map<String, Object> data = new HashMap<String,Object>();
                    data.put("uid",String.valueOf(MainActivity.loggedinuser.getUseridu()));
                    data.put("uname",String.valueOf(MainActivity.loggedinuser.getUsername()));
                    data.put("aid",String.valueOf(user.getUseridu()));
                    data.put("aname",user.getUsername());
                    data.put("reason",String.valueOf(report.getText()));
                    mCollectionRef.add(data).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            sendemail();
                        }
                    });

                }



            }
        });





        return view;
    }
    public void sendemail(){
        email = "bookapp1234@gmail.com";
        password="bookapppassword";
        Subject = "Banned from elib";
        To = "swah_jian_oon@hotmail.com";
        msg="Dear Sir/Madam," + System.lineSeparator() +System.lineSeparator()+
                "You have reported "+ reportedname+ System.lineSeparator()+ System.lineSeparator()+
                "Reason: "+ reason+ System.lineSeparator()+ System.lineSeparator()+
                "If you have any issues regarding this issue, please reply to this email."+System.lineSeparator()+ System.lineSeparator()+
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
    private class SendMail extends AsyncTask<Message,String,String> {
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
