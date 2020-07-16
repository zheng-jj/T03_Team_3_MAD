package com.example.t03team3mad;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adminhomepagefragment extends Fragment {
    private static final String TAG = "AdminHome";
    Button verify;
    Button ban;
    Button reviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.adminhomepage,container,false);

        //qh - verify button
        verify = view.findViewById(R.id.verifyoption);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    verfiybooksfragment nextFragment = new verfiybooksfragment();  //will go the fragment where it will display all the books of that genre
                    MainActivity.addFragment(nextFragment,getActivity(),"Verify Books");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can verify books!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ban = view.findViewById(R.id.deleteuser);
        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    banusersfragment nextFragment = new banusersfragment();  //will go the fragment where it will display all the books of that genre
                    MainActivity.addFragment(nextFragment,getActivity(),"Ban Users");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can ban users!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }



}
