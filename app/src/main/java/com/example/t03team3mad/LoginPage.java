package com.example.t03team3mad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginPage extends AppCompatActivity {
    EditText EnterEmail,EnterPassword;
    Button LoginButton;
    TextView RegisterButton;
    FirebaseAuth Auth;
    SharedPreferences Auto_login;
    DatabaseReference databaseReference;
    private static final String TAG = "LoginPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        EnterEmail=findViewById(R.id.EnterEmail);
        EnterPassword=findViewById(R.id.EnterPassword);
        LoginButton=findViewById(R.id.LoginButton);
        RegisterButton=findViewById(R.id.Register);
        Auto_login=getSharedPreferences("LoginButton",MODE_PRIVATE);
        //Auto_login.edit().putBoolean("logged",false).apply();
        //^ if wan test again
        if(Auto_login.getBoolean("logged",false)){
            Intent MainActivity= new Intent(LoginPage.this,MainActivity.class);
            startActivity(MainActivity);
        }

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = EnterEmail.getText().toString().trim();
                password = EnterPassword.getText().toString().trim();
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

                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.v(TAG,"Successfully Logged In");
                            Toast.makeText(LoginPage.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                            Bundle UserDetails=new Bundle();
                            Auto_login.edit().putBoolean("logged",true).apply(); //User is Logged in until he log out
                            Intent MainActivity= new Intent(LoginPage.this,MainActivity.class);
                            startActivity(MainActivity);
                            finish();
                        }
                        else {

                            Log.v(TAG,"Login Failed");
                            Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        //if press "Register"
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v(TAG,"Going to Register Page");
                Intent Register=new Intent(LoginPage.this, RegisterPage.class);
                startActivity(Register);
                finish();
            }
        });
    }
}