
package com.example.t03team3mad;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.t03team3mad.model.Member;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {
    EditText EnterEmail,EnterPassword,EnterName,ConfirmPassword;
    Button RegisterButton;
    FirebaseAuth Auth;
    DatabaseReference databaseReference;
    Member member;
    User user;
    ProgressBar progressBar;
    long maxid=0;
    private static final String TAG = "RegisterPage";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        EnterEmail=findViewById(R.id.EnterEmail);
        EnterName=findViewById(R.id.EnterName);
        EnterPassword=findViewById(R.id.EnterPassword);
        ConfirmPassword=findViewById(R.id.ConfirmPassword);
        RegisterButton=findViewById(R.id.RegisterButton);
        progressBar =  findViewById(R.id.progressBar);
        Auth = FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member");
        member = new Member();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    //Chris - To get the current number of users in the database.
                    maxid=dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ShowToast")
            @Override
            public void onClick(View v) {
                String email, password, name, confirmPassword;
                email = EnterEmail.getText().toString();
                password = EnterPassword.getText().toString();
                name = EnterName.getText().toString();
                confirmPassword = ConfirmPassword.getText().toString();
                //Chris - Verification for inputs
                //Chris - Check for empty Inputs
                if (name.equals("")) {
                    Log.v(TAG, "Name Required");
                    Toast.makeText(RegisterPage.this, "Name Required", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.equals("")) {
                    Log.v(TAG, "Email Required");//Chris - Check for empty Inputs
                    Toast.makeText(RegisterPage.this, "Email Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.equals(""))//Chris - Check for empty Inputs
                {
                    Log.v(TAG, "Password Required");
                    Toast.makeText(RegisterPage.this, "Password Required", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6)//Chris - to check password hit the minimal characters of the password requirement
                {
                    Log.v(TAG, "Password is must be at least contain 6 characters");
                    Toast.makeText(RegisterPage.this, "Password is must be at least contain 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirmPassword.equals(""))//Chris - Check for empty Inputs
                {
                    Log.v(TAG, "Confirm Password");
                    Toast.makeText(RegisterPage.this, "Confirm Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!confirmPassword.equals(password))//Chris - To Confirm password
                {
                    Log.v(TAG, "Password Do Not Match");
                    Toast.makeText(RegisterPage.this, "Password Do Not Match", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    progressBar.setVisibility(View.VISIBLE);//Chris - For user to know that the data is being processed
                    //Authentication
                    Auth.createUserWithEmailAndPassword(email, confirmPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //Chris - check whether register of user is successful or not
                            if (!task.isSuccessful()) {
                                progressBar.setVisibility(View.INVISIBLE);
                                //Custom message if email is invaild
                                Toast.makeText(RegisterPage.this, "The email is invaild", Toast.LENGTH_SHORT).show();
                                Log.v(TAG, "The email is invaild");
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                //Chris - Register is successful,saving user details to firebase database
                                member.setName(EnterName.getText().toString());
                                member.setEmail(EnterEmail.getText().toString());
                                member.setDeviceID("0");
                                //Chris -  Customised user id,make sure no two users have the same user id
                                String id = String.valueOf(maxid + 1);
                                //Chris - Add the user to firebase database
                                databaseReference.child(id).setValue(member);
                                user = new User();
                                user.setUsername(EnterName.getText().toString());
                                user.setUserabout("");
                                user.setUserisbn("");
                                user.setUseridu(Integer.parseInt(id));
                                user.setfollowingstring("");
                                //Chris - Add the user to firebase database
                                databaseReference.child(id).setValue(member);
                                FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                final DocumentReference docRef = firebaseFirestore.collection("User").document(String.valueOf(user.getUseridu()));
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            Map<String, Object> data = new HashMap<>();
                                            data.put("desc", user.getUserabout());
                                            data.put("name", user.getUsername());
                                            data.put("isbn", user.getUserisbn());
                                            data.put("following", user.getfollowingstring());
                                            data.put("role","User");
                                            docRef.set(data);
                                            //Go to login page after user successfully registered
                                            Intent login = new Intent(RegisterPage.this, LoginPage.class);
                                            startActivity(login);
                                            finish();
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