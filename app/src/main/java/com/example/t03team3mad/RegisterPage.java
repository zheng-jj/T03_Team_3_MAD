package com.example.t03team3mad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.t03team3mad.model.Member;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterPage extends AppCompatActivity {
    EditText EnterEmail,EnterPassword,EnterName,ConfirmPassword;
    Button RegisterButton;
    FirebaseAuth Auth;
    DatabaseReference databaseReference;
    Member member;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        EnterEmail=findViewById(R.id.EnterEmail);
        EnterName=findViewById(R.id.EnterName);
        EnterPassword=findViewById(R.id.EnterPassword);
        ConfirmPassword=findViewById(R.id.ConfirmPassword);
        RegisterButton=findViewById(R.id.RegisterButton);
        Auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member");
        member = new Member();
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                String email, password,name,confirmPassword;
                email = EnterEmail.getText().toString();
                password = EnterPassword.getText().toString();
                name = EnterName.getText().toString();
                confirmPassword = ConfirmPassword.getText().toString();
                //Check for empty Inputs
                if (name.equals(""))
                {
                    Toast.makeText(RegisterPage.this, "Name Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.equals(""))
                {
                    Toast.makeText(RegisterPage.this, "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(""))
                {
                    Toast.makeText(RegisterPage.this, "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmPassword.equals(""))//Check for empty Inputs
                {
                    Toast.makeText(RegisterPage.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!confirmPassword.equals(password))
                {
                    Toast.makeText(RegisterPage.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    //Authentication
                    Auth.createUserWithEmailAndPassword(email, confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //check whether register of user is successful or not
                            if (task.isSuccessful()) {
                                member.setName(EnterName.getText().toString());
                                member.setEmail(EnterEmail.getText().toString());
                                member.setPassword(EnterPassword.getText().toString());
                                databaseReference.push().setValue(member);
                                Toast.makeText(RegisterPage.this, "Registration Succesfully", Toast.LENGTH_SHORT).show();
                                Intent login=new Intent(RegisterPage.this, LoginPage.class);
                                startActivity(login);
                                finish();
                            }
                            else {
                                Toast.makeText(RegisterPage.this, "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }

}
