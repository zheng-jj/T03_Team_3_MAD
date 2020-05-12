package com.example.t03team3mad;

import android.content.Intent;
import android.os.Bundle;
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

public class LoginPage extends AppCompatActivity {
    EditText EnterEmail,EnterPassword;
    Button LoginButton;
    TextView RegisterButton;
    FirebaseAuth Auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        EnterEmail=findViewById(R.id.EnterEmail);
        EnterPassword=findViewById(R.id.EnterPassword);
        LoginButton=findViewById(R.id.LoginButton);
        RegisterButton=findViewById(R.id.Register);
        Auth = FirebaseAuth.getInstance();
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = EnterEmail.getText().toString().trim();
                password = EnterPassword.getText().toString().trim();
                //Check for empty Inputs
                if (email.isEmpty()) {
                    Toast.makeText(LoginPage.this, "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty())//check for empty input
                {
                    Toast.makeText(LoginPage.this, "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6)//for exception to not appear
                {
                    Toast.makeText(LoginPage.this, "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPage.this, "Successfully Logged In", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(LoginPage.this, "Login Failed", Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Register=new Intent(LoginPage.this, RegisterPage.class);
                startActivity(Register);
                finish();
            }
        });
    }
}