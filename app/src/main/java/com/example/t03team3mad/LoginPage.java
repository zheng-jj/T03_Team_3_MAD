package com.example.t03team3mad;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends Fragment {
    EditText EnterEmail,EnterPassword;
    Button LoginButton;
    TextView RegisterButton;
    FirebaseAuth Auth;
    private static final String TAG = "LoginFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_page,container,false);
        EnterEmail=view.findViewById(R.id.EnterEmail);
        EnterPassword=view.findViewById(R.id.EnterPassword);
        LoginButton=view.findViewById(R.id.LoginButton);
        RegisterButton=view.findViewById(R.id.Register);
        Auth = FirebaseAuth.getInstance();

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email, password;
                email = EnterEmail.getText().toString().trim();
                password = EnterPassword.getText().toString().trim();
                //Check for empty Inputs
                if (email.isEmpty()) {
                    Toast.makeText(LoginPage.this.getActivity(), "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.isEmpty())//check for empty input
                {
                    Toast.makeText(LoginPage.this.getActivity(), "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (password.length() < 6)//for exception to not appear
                {
                    Toast.makeText(LoginPage.this.getActivity(), "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }

                   Auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {
                           if(task.isSuccessful()) {
                               Toast.makeText(LoginPage.this.getActivity(), "Successfully Logged In", Toast.LENGTH_LONG).show();
                           }
                           else {
                               Toast.makeText(LoginPage.this.getActivity(), "Login Failed", Toast.LENGTH_LONG).show();
                           }

                       }
                   });

                }

        });
        return view;
    }
}