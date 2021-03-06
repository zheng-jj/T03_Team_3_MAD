package com.T03G3.eLibtheBookManager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Login_OTP extends AppCompatActivity {
    EditText Entercode;
    Button sendcode;
    SharedPreferences Auto_login;
    DatabaseReference databaseReference;
    int random;
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_o_t_p);
        Entercode = findViewById(R.id.EnterOTP);
        sendcode = findViewById(R.id.SubmitCode);
        Auto_login=getSharedPreferences("LoginButton",MODE_PRIVATE);
        databaseReference= FirebaseDatabase.getInstance().getReference().child("Member");
        //Chris- get device id that is logging in
        final String androidId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Intent intent = getIntent();
        final Bundle bundle = intent.getExtras().getBundle("User_UID");
        final String UID = bundle.getString("User_UID");
        final String Email = bundle.getString("email");
        random = new Random().nextInt(10000) + 1000;

        //chris - make sure only one email is being sent
        MailApi fd = new MailApi(Login_OTP.this, Email, "eLibTheBookManager Login OTP", "Dear Sir/Mdm\n\nYour OTP to login is " + random + "\n\n Regards,\n Admins");
        fd.execute();

        //chris- check otp
        sendcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = Entercode.getText().toString().trim();
                if (otp.equals("")) {
                    Toast.makeText(Login_OTP.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Enter OTP to proceed");
                    return;
                }
                if (!otp.equals(String.valueOf(random))) {
                    Toast.makeText(Login_OTP.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    Log.v(TAG, "Incorrect OTP");
                    return;
                }
                else{
                    Log.v(TAG, "Successfully Logged In");
                    databaseReference.child(UID).child("deviceID").setValue(androidId);
                    Auto_login.edit().putBoolean("logged", true).apply();
                    Intent MainActivity = new Intent(Login_OTP.this, MainActivity.class);
                    MainActivity.putExtra("User_UID", bundle);
                    startActivity(MainActivity);
                    finish();
                }

            }
        });
    }

}