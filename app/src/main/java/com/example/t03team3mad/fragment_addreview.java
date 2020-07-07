
package com.example.t03team3mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;

import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_addreview extends Fragment {
    private static final String TAG = "authorprofileFragment";
    Button enter;
    EditText editreview;
    String idu;
    String ISBN;
    int idr;
    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Reviews");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo -display fragment
        View view = inflater.inflate(R.layout.subfragment_writereview, container, false);
        // jo - get bundle from another fragment
        Bundle bundle = this.getArguments();
        final User user = bundle.getParcelable("user");
        final Book book = bundle.getParcelable("book");
        //jo - find viewbyids
        enter =  view.findViewById(R.id.enter);
        editreview = view.findViewById(R.id.reviewinput);
        ISBN = book.getIsbn();
        getidr(ISBN);
        // onclick listener for adding review + storing review into database
        enter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.v("Click","Button clicked");


                idu = Integer.toString(user.getUseridu());
                String review = editreview.getText().toString();
                addreview(String.valueOf(idr),idu,review,ISBN,user.getUsername());
                Log.v("idr", String.valueOf(idr));
                getidr(ISBN);

            }
        });

        return view;
    }
    // get the latest id of reviews so it can be used to +1 to add another review since it is a primary key
    public void getidr(String ISBN){

        mCollectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> data =queryDocumentSnapshots.getDocuments();

                    idr = data.size()+1;
                    Log.v("idr", String.valueOf(idr));

                }
                else{
                    idr = 1;
                }




            }
        });




    }


    public void addreview(String idr,String idu,String review,String ISBN,String name){
        // Add document data  with id staffid using a hashmap
        Map<String, Object> data = new HashMap<String,Object>();

        data.put("uid", idu);
        data.put("vote", "0");
        data.put("review", review);
        data.put("uname",name);
        data.put("isbn",ISBN);
        mCollectionRef.document(idr).set(data);
    }
}


