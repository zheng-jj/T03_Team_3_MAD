package com.example.t03team3mad;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationPage extends Fragment {
    EditText EnterEmail,EnterPassword,EnterName,ConfirmPassword;
    Button RegisterButton;
    FirebaseAuth Auth;
    DatabaseReference databaseReference;
    member Member;
    private static final String TAG = "RegisterFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registration_page,container,false);

        EnterEmail=view.findViewById(R.id.EnterEmail);
        EnterName=view.findViewById(R.id.EnterName);
        EnterPassword=view.findViewById(R.id.EnterPassword);
        ConfirmPassword=view.findViewById(R.id.ConfirmPassword);
        RegisterButton=view.findViewById(R.id.RegisterButton);
        Auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("member");
        Member = new member();
        RegisterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String email, password, name, confirmPassword;
                    email = EnterEmail.getText().toString().trim();
                    password = EnterPassword.getText().toString().trim();
                    name = EnterName.getText().toString();
                    confirmPassword = ConfirmPassword.getText().toString().trim();
                    //Check for empty Inputs

                    if (name.equals("")) {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Name Required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (email.isEmpty()) {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Email Required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (password.isEmpty())//check for empty input
                    {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Password Required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (password.length() < 6)//for exception to not appear
                    {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (confirmPassword.isEmpty())//Check for empty Input
                    {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Confirm Password", Toast.LENGTH_SHORT).show();
                        return;

                    }
                    if (!confirmPassword.equals(password)) {
                        Toast.makeText(RegistrationPage.this.getActivity(), "Password Do Not Match", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //Create user into the firebase
                    Auth.createUserWithEmailAndPassword(email, confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //check whether register of user is successful or not
                            if (task.isSuccessful()) {

                                Member.setName(EnterName.getText().toString());
                                Member.setEmail(EnterEmail.getText().toString());
                                Member.setPassword(EnterPassword.getText().toString());
                                databaseReference.push().setValue(Member);
                                Toast.makeText(RegistrationPage.this.getActivity(), "Registration Succesfully", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(RegistrationPage.this.getActivity(), "Registration Failed" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });


        return view;
    }

}