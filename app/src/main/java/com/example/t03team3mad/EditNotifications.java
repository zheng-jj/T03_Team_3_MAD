package com.example.t03team3mad;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.t03team3mad.model.notifications;

import java.util.ArrayList;

public class EditNotifications extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notifications);
        ArrayList<notifications> notificationsettings = new ArrayList<>();

        SharedPreferences topics = getSharedPreferences("topics", Context.MODE_PRIVATE);

        Boolean reviewnoti = topics.getBoolean("reviewnoti",true);
        Boolean follownoti = topics.getBoolean("follownoti",true);
        Boolean favnoti = topics.getBoolean("favnoti",true);

        notificationsettings.add(new notifications("Notify when users make reviews",reviewnoti,"reviewnoti","review"));
        notificationsettings.add(new notifications("Notify when users follow another user",follownoti,"follownoti","follow"));
        notificationsettings.add(new notifications("Notify when users favourites a book",favnoti,"favnoti","fav"));

        RecyclerView notisettings = findViewById(R.id.notification);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        notisettings.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterNotification adapterNotification = new AdapterNotification(notificationsettings, getApplicationContext());
        //jj- set the recyclerview object to its adapter
        notisettings.setAdapter(adapterNotification);

        Button done = findViewById(R.id.button_done);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
