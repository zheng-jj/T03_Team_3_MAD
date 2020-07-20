package eLib_theBookManager;

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
    private EditText resetemail, code,password,Confirmpassword;
    private Button reset, proceed,confirmReset;
    private FirebaseAuth firebaseAuth;
    FirebaseAuth Auth;
    FirebaseUser user;
    DatabaseReference databaseReference;
    String otp;
    String uid,oldpassword,useremail;
    long maxid = 0;
    private static final String TAG = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_page);
        Auth=FirebaseAuth.getInstance();
        resetemail = (EditText) findViewById(R.id.ResetPasswordEmail);
        code = (EditText) findViewById(R.id.OTP);
        reset = (Button) findViewById(R.id.resetbutton);
        proceed = (Button) findViewById(R.id.Proceed);
        password = (EditText) findViewById(R.id.password1);
        Confirmpassword = (EditText) findViewById(R.id.confirmPassword);
        confirmReset= (Button) findViewById(R.id.ConfirmPassword);
        firebaseAuth = FirebaseAuth.getInstance();
        user=Auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Member");
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
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if(snapshot.child("email").getValue().toString().equals(useremail)) {
                                    uid = snapshot.getKey();
                                    oldpassword=snapshot.child("password").getValue().toString();
                                    //Chris- get random number as otp
                                    int random = new Random().nextInt(10000) + 1000;
                                    otp = String.valueOf(random);
                                    //Chris- send otp to user's email
                                    MailApi email= new MailApi(ResetPasswordPage.this,useremail,"Reset BookApp Password","Hi\nYour OTP To Reset Password is "+otp);
                                    email.execute();
                                    resetemail.setVisibility(View.GONE);
                                    reset.setVisibility(View.GONE);
                                    proceed.setVisibility(View.VISIBLE);
                                    code.setVisibility(View.VISIBLE);

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
        proceed.setOnClickListener(new View.OnClickListener() {
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
                    code.setVisibility(View.GONE);
                    proceed.setVisibility(View.GONE);
                    confirmReset.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    Confirmpassword.setVisibility(View.VISIBLE);
                    confirmReset.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String Password = password.getText().toString().trim();
                            final String ConfirmPassword = Confirmpassword.getText().toString().trim();
                            //Chris- check inputs
                            if(Password.equals("")){
                                Toast.makeText(ResetPasswordPage.this, "Must enter a password", Toast.LENGTH_SHORT).show();
                            }
                            if(ConfirmPassword.equals("")){
                                Toast.makeText(ResetPasswordPage.this, "Must Confirm the password", Toast.LENGTH_SHORT).show();
                            }
                            if (Password.length() < 6)//Chris - to check password hit the minimal characters of the password requirement
                            {
                                Log.v(TAG, "Password is must be at least contain 6 characters");
                                Toast.makeText(ResetPasswordPage.this, "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();

                            }
                            if (!ConfirmPassword.equals(Password))//Chris - To Confirm password
                            {
                                Log.v(TAG, "Password Do Not Match");
                                Toast.makeText(ResetPasswordPage.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();

                            }
                            else{
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
                                                        //Chris - if login is successful
                                                        user.updatePassword(ConfirmPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User password updated.");
                                                                    databaseReference.child(uid).child("password").setValue(ConfirmPassword);
                                                                    MailApi confirmation = new MailApi(ResetPasswordPage.this, useremail, "Reset BookApp Password", "Hi\nYour password has recently be reset");
                                                                    confirmation.execute();
                                                                    Intent MainActivity = new Intent(ResetPasswordPage.this, LoginPage.class);
                                                                    startActivity(MainActivity);

                                                                }
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                        }
                                        else {
                                            Log.v(TAG,"Reset Failed");
                                            Toast.makeText(ResetPasswordPage.this, "Reset Failed", Toast.LENGTH_LONG).show();
                                        }

                                    }
                                });
                            }
                        }
                    });

                }
            }
        });


    }
}
