package com.T03G3.eLibtheBookManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

public class ResetPasswordPage extends AppCompatActivity {
    private EditText resetemail, code,password,Confirmpassword,accountKEY;
    private Button reset, proceed,confirmReset,FinalConfirm;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth Auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String otp;
    String uid,oldpassword,useremail;
    String NEWPASSWORD;
    Boolean True= true;
    String Key;
    long maxid = 0;
    private static final String TAG = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);

        Auth=FirebaseAuth.getInstance();
        resetemail = (EditText) findViewById(R.id.ResetPasswordEmail);
        code = (EditText) findViewById(R.id.OTP);
        accountKEY=(EditText) findViewById(R.id.AccountKey);
        reset = (Button) findViewById(R.id.resetbutton);
        proceed = (Button) findViewById(R.id.Proceed);
        FinalConfirm = (Button) findViewById(R.id.LastConfirm);
        password = (EditText) findViewById(R.id.password1);
        Confirmpassword = (EditText) findViewById(R.id.confirmPassword);
        confirmReset= (Button) findViewById(R.id.ConfirmPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        user=Auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");
        //Chris- first layer (get email)
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useremail = resetemail.getText().toString().trim();

                if (useremail.equals("")) {
                    Toast.makeText(ResetPasswordPage.this, "Enter email to reset", Toast.LENGTH_SHORT).show();
                }
                else {
                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int i=1;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.child("email").getValue().toString().equals(useremail)) {

                                    uid = snapshot.getKey();
                                    oldpassword = snapshot.child("password").getValue().toString();
                                    Key = snapshot.child("uniKey").getValue().toString();


                                    resetemail.setVisibility(View.GONE);
                                    reset.setVisibility(View.GONE);
                                    proceed.setVisibility(View.VISIBLE);
                                    accountKEY.setVisibility(View.VISIBLE);
                                    break;
                                }
                                //Chris - if email does not exist in database
                                if (i == (int) dataSnapshot.getChildrenCount()&& !snapshot.child("email").getValue().toString().equals(useremail) ){
                                    Toast.makeText(ResetPasswordPage.this, "Email does not exist", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                                else{
                                    i+=1;
                                }

                            }



                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });
                }
            }
        });
        //Chris - 2nd layer (check with user's input of key with the key in database)
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Check = accountKEY.getText().toString().trim();
                if (Check.equals("")) {
                    Toast.makeText(ResetPasswordPage.this, "Enter Key to proceed", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Enter OTP to proceed");
                }
                if (!Check.equals(Key)) {
                    Toast.makeText(ResetPasswordPage.this, "Incorrect Key", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Incorrect OTP");
                }
                else {
                    accountKEY.setVisibility(View.GONE);
                    proceed.setVisibility(View.GONE);
                    Confirmpassword.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    confirmReset.setVisibility(View.VISIBLE);

                }
            }
        });
        //Chris- third layer(requesting new password)
        confirmReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Password = password.getText().toString().trim();
                final String ConfirmPassword = Confirmpassword.getText().toString().trim();
                //Chris- check inputs
                if (Password.equals("")) {
                    Toast.makeText(ResetPasswordPage.this, "Must enter a password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (ConfirmPassword.equals("")) {
                    Toast.makeText(ResetPasswordPage.this, "Must Confirm the password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Password.length() < 6)//Chris - to check password hit the minimal characters of the password requirement
                {
                    Log.v(TAG, "Password is must be at least contain 6 characters");
                    Toast.makeText(ResetPasswordPage.this, "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!ConfirmPassword.equals(Password))//Chris - To Confirm password
                {
                    Log.v(TAG, "Password Do Not Match");
                    Toast.makeText(ResetPasswordPage.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    NEWPASSWORD=ConfirmPassword;
                    //Chris- get random number as otp
                    int random = new Random().nextInt(10000) + 1000;
                    otp = String.valueOf(random);

                    //Chris- send otp to user's email
                    MailApi fd= new MailApi(ResetPasswordPage.this,useremail,"Reset eLibTheBookManager Password","Dear Sir/Mdm\n\nYour OTP To Reset Password is " +otp+"\n\n Regards,\n Admins");
                    fd.execute();
                    FinalConfirm.setVisibility(View.VISIBLE);
                    code.setVisibility(View.VISIBLE);
                    password.setVisibility(View.GONE);
                    Confirmpassword.setVisibility(View.GONE);
                    confirmReset.setVisibility(View.GONE);

                }
            }
        });
        //Chris- last layer - get otp that is send to the email.
        FinalConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Chris- check otp input
                String Code = code.getText().toString().trim();

                if (Code.equals("")) {
                    Toast.makeText(ResetPasswordPage.this, "Enter OTP to proceed", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"Enter OTP to proceed");
                }

                if (!Code.equals(otp)) {
                    Toast.makeText(ResetPasswordPage.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    Log.v(TAG,"Incorrect OTP");

                }

                else {

                    //Chris- login using user's past password
                    AuthCredential credential = EmailAuthProvider.getCredential(useremail, oldpassword);
                    //Chris - Prompt the user to re-provide their sign-in credentials
                    Auth.signInWithEmailAndPassword(useremail, oldpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //Chris - find user id for the login user
                                databaseReference.orderByChild("email").equalTo(useremail).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                            if (!user.equals(null)) {
                                                //Chris - if login is successful
                                                user.updatePassword(NEWPASSWORD).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            //chris - to make it oni email once even if it lagged
                                                            if (True == true) {
                                                                MailApi confirmation = new MailApi(ResetPasswordPage.this, useremail, "eLibTheBookManager Recent Change of Password", "Dear Sir/Mdm\n\nYour password has recently been reset\n\n Regards,\n Admins");
                                                                confirmation.execute();
                                                                True = false;
                                                            }
                                                            Log.d(TAG, "Your password has been updated");
                                                            Toast.makeText(ResetPasswordPage.this, "Your password has been updated", Toast.LENGTH_SHORT).show();
                                                            databaseReference.child(uid).child("password").setValue(NEWPASSWORD);
                                                            Intent MainActivity = new Intent(ResetPasswordPage.this, LoginPage.class);
                                                            startActivity(MainActivity);


                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Log.d(TAG, "Please try again later");
                                                Toast.makeText(ResetPasswordPage.this, "Please try again later", Toast.LENGTH_SHORT).show();
                                                Intent MainActivity = new Intent(ResetPasswordPage.this, LoginPage.class);
                                                startActivity(MainActivity);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            } else {
                                Log.v(TAG, "Reset Failed");
                                Toast.makeText(ResetPasswordPage.this, "Reset Failed", Toast.LENGTH_LONG).show();
                                return;
                            }

                        }
                    });
                }
            }
        });



    }
}