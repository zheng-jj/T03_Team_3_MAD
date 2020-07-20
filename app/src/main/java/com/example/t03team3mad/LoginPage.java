package com.example.t03team3mad;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class LoginPage extends AppCompatActivity {
    public Context logincontext;
    EditText EnterEmail,EnterPassword;
    Button LoginButton;
    TextView RegisterButton;
    TextView ResetPassword;
    FirebaseAuth Auth;
    SharedPreferences Auto_login;
    FirebaseUser user;
    String uid;
    DatabaseReference databaseReference;
    ProgressBar progressBar;
    //jj-constructors to get the context of the login page from other fragments
    public LoginPage(Context context)
    {
        logincontext=context;
    }
    public Context getLogincontext(){
        return logincontext;
    }
    public  Context getContext(){
        Context mContext = LoginPage.this;
        return mContext;
    }
    public LoginPage(){}
    private static final String TAG = "LoginPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Auto_login=getSharedPreferences("LoginButton",MODE_PRIVATE);

        setContentView(R.layout.activity_login_page);
        EnterEmail=findViewById(R.id.EnterEmail);
        EnterPassword=findViewById(R.id.EnterPassword);
        LoginButton=findViewById(R.id.LoginButton);
        Auth=FirebaseAuth.getInstance();
        RegisterButton=findViewById(R.id.Register);
        ResetPassword=findViewById(R.id.forgotPassword);
        progressBar =  findViewById(R.id.progressBar);
        user=Auth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member");
        final String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        //Auto_login.edit().putBoolean("logged",false).apply();
        //Chris - User is already logged in
        if(Auto_login.getBoolean("logged",false)){
            databaseReference.orderByChild("email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        //Chris - get uid from shared preferences
                        Log.v(TAG, androidId);
                        uid = Auto_login.getString("UserID", null);
                        Log.v(TAG, "the user id sent= " + uid);
                        Log.v(TAG, "the device id sent= " + snapshot.child("deviceID").getValue().toString());
                        if (snapshot.child("deviceID").getValue().toString().equals(androidId)) {

                            Bundle bundle = new Bundle();
                            bundle.putString("User_UID", uid);
                            Intent MainActivity = new Intent(LoginPage.this, MainActivity.class);
                            MainActivity.putExtra("User_UID", bundle);
                            Log.v(TAG, "sending this uid to main activity " + uid);
                            startActivity(MainActivity);
                            finish();
                        }
                        else {
                            Log.v(TAG, "Account has been logged in another device.Please login again");
                            Toast.makeText(LoginPage.this, "Account has been logged in another device.Please login again", Toast.LENGTH_SHORT).show();
                            Auto_login.edit().putBoolean("logged", false).apply();
                            Intent MainActivity = new Intent(LoginPage.this, LoginPage.class);
                            startActivity(MainActivity);
                        }
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //Chris -Login button listener
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = EnterEmail.getText().toString().trim();
                password = EnterPassword.getText().toString().trim();
                //Chris - Validation
                //Check for empty Inputs
                if (email.isEmpty()) {
                    Log.v(TAG,"Email Required");
                    Toast.makeText(LoginPage.this, "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty())//check for empty input
                {
                    Log.v(TAG,"Password Required");
                    Toast.makeText(LoginPage.this, "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6)//for exception to not appear
                {
                    Log.v(TAG,"Password is must be at least contain 6 characters");
                    Toast.makeText(LoginPage.this, "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Chris - Loading circle for improving user experience
                progressBar.setVisibility(View.VISIBLE);
                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Chris - find user id for the login user
                            databaseReference.orderByChild("email").equalTo(user.getEmail()).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if(snapshot.child("banned").getValue().toString().equals("false")) {
                                            uid = snapshot.getKey();
                                            String name = snapshot.child("name").getValue().toString();
                                            String number = snapshot.child("phonenumber").getValue().toString();

                                            //Chris - show that it works
                                            Log.v(TAG, "the user id sent= " + uid);

                                            //Chris -  save user id as bundle
                                            Bundle bundle = new Bundle();
                                            bundle.putString("User_UID", uid);
                                            Bundle bundle2 = new Bundle();
                                            bundle2.putString("PhoneNo", number);
                                            //Chris -  save user id in shared preference
                                            SharedPreferences.Editor editor = Auto_login.edit();
                                            editor.putString("UserID", uid).apply();
                                            //Chris - Check if user record is already recorded in the local database or not
                                            Boolean CheckIfRecordExisted = CheckRecord(uid);
                                            if (!CheckIfRecordExisted) {
                                                insertUser(uid, name, "About", null, null);
                                                Log.v(TAG, "Inserted Successfully");
                                            } else {
                                                Log.v(TAG, "User Record Is Already Inserted");
                                            }
                                            databaseReference.child(uid).child("deviceID").setValue(androidId);
                                            //Chris - if login is successful
                                            progressBar.setVisibility(View.INVISIBLE);
                                            //Chris - User is Logged in until he log out
                                            Auto_login.edit().putBoolean("logged", true).apply();
                                            Log.v(TAG, "Requesting OTP");
                                            Toast.makeText(LoginPage.this, "Requesting OTP", Toast.LENGTH_LONG).show();

                                            //Chris - Intent to homepage and pass user id to it
                                            Intent MainActivity = new Intent(LoginPage.this, LoginOTP.class);
                                            MainActivity.putExtra("User_UID", bundle);
                                            MainActivity.putExtra("PhoneNo", bundle2);

                                            Log.v(TAG, "sending this uid to main activity " + uid);
                                            startActivity(MainActivity);
                                            finish();
                                        }
                                        else {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            Toast.makeText(LoginPage.this, "User is banned", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                }
                            });


                        }
                        else {
                            //Chris - if login failed
                            progressBar.setVisibility(View.INVISIBLE);
                            Log.v(TAG,"Login Failed");
                            Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        //Chris -if User press "Register" text
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Going to Register Page");
                Intent Register=new Intent(LoginPage.this, RegisterPage.class);
                startActivity(Register);
            }
        });
        ResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Going to Reset Password Page");
                Intent Reset=new Intent(LoginPage.this, ResetPasswordPage.class);
                startActivity(Reset);
            }
        });
    }


    //Chris - Method to insert to local database
    public boolean insertUser(String idu,String name,String about,String favouriteb,String following) {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(LoginPage.this.getApplicationContext());
        DBaccess.open();
        boolean success = DBaccess.addData(idu, name, about, favouriteb, following);
        DBaccess.close();
        return success;
    }
    //Chris -  check user record if exist in local database
    public boolean CheckRecord(String idu) {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(LoginPage.this.getApplicationContext());
        DBaccess.open();
        boolean success = DBaccess.CheckExistingRecordByUserId(idu);
        DBaccess.close();
        return success;
    }
}

