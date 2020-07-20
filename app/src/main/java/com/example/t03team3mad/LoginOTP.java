package com.example.t03team3mad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Random;

public class LoginOTP extends AppCompatActivity {
    EditText Entercode;
    Button sendcode;
    Bundle mBundle;
    int random;
    private static final String TAG = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_o_t_p);
        Entercode = findViewById(R.id.EnterOTP);
        sendcode = findViewById(R.id.SubmitCode);

        Intent intent = getIntent();
        final Bundle uid = intent.getExtras().getBundle("User_UID");
        Bundle Number = intent.getExtras().getBundle("PhoneNo");
        String loggedinuserID = Number.getString("PhoneNo");
        random = new Random().nextInt(10000) + 1000;
        ActivityCompat.requestPermissions(LoginOTP.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(loggedinuserID, null, String.valueOf(random), null, null);

     sendcode.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             String otp=Entercode.getText().toString().trim();
             if(otp.equals("")){
                 Toast.makeText(LoginOTP.this, "Enter OTP", Toast.LENGTH_SHORT).show();
                 Log.v(TAG,"Enter OTP to proceed");
                 return;
             }
             if (!otp.equals(otp)) {
                 Toast.makeText(LoginOTP.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                 Log.v(TAG,"Incorrect OTP");
                 return;
             }
             else{
                 Log.v(TAG,"Successfully Logged In");
                 Intent MainActivity = new Intent(LoginOTP.this,MainActivity.class);
                 MainActivity.putExtra("User_UID", uid);
                 startActivity(MainActivity);
                 finish();
             }
         }
     });
    }

}

