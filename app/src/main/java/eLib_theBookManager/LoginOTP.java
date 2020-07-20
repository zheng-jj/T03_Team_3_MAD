package eLib_theBookManager;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import eLib_theBookManager.R;

import java.util.Random;

public class LoginOTP extends AppCompatActivity {
    EditText Entercode;
    Button sendcode;
    int random;
    SharedPreferences Auto_login;
    private static final String TAG = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_o_t_p);
        Entercode = findViewById(R.id.EnterOTP);
        sendcode = findViewById(R.id.SubmitCode);
        Auto_login=getSharedPreferences("LoginButton",MODE_PRIVATE);
        Intent intent = getIntent();
        final Bundle uid = intent.getExtras().getBundle("User_UID");
        Bundle Number = intent.getExtras().getBundle("PhoneNo");
        String loggedinuserID = Number.getString("PhoneNo");
        //Chris- get random number for otp
        random = new Random().nextInt(10000) + 1000;
        //Chris-send sms and get permission
        ActivityCompat.requestPermissions(LoginOTP.this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS}, PackageManager.PERMISSION_GRANTED);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(loggedinuserID, null, String.valueOf(random), null, null);
//chris- check otp
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
                 Auto_login.edit().putBoolean("logged",true).apply();
                 Log.v(TAG,"Successfully Logged In");
                 Intent MainActivity = new Intent(LoginOTP.this, eLib_theBookManager.MainActivity.class);
                 MainActivity.putExtra("User_UID", uid);
                 startActivity(MainActivity);
                 finish();
             }
         }
     });
    }

}

