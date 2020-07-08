
package com.example.t03team3mad;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragment_addreview extends Fragment {
    private static final String TAG = "authorprofileFragment";
    TextView rtitle2;
    Button enter;
    EditText editreview;
    String idu;
    String ISBN;
    int idr;
    float ratevalue;
    RatingBar ratings;
    String review;
    String name;


    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Reviews");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionRefbooks = FirebaseFirestore.getInstance().collection("Book");

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
        rtitle2 = view.findViewById(R.id.rtitle2);
        rtitle2.setText(book.getBooktitle());
        ISBN = book.getIsbn();

        ratings = view.findViewById(R.id.ratingBar);


        // onclick listener for adding review + storing review into database
        enter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.v("Click","Button clicked");
                idu = Integer.toString(user.getUseridu());
                review = editreview.getText().toString();
                name = user.getUsername();
                ratevalue = ratings.getRating();
                Log.v("RateValue", String.valueOf(ratevalue));
                getidr();

                getFragmentManager().popBackStack("HomeFragment",0);
            }
        });

        return view;
    }
    // get the latest id of reviews so it can be used to +1 to add another review since it is a primary key
    public void getidr(){

        mCollectionRef.document(ISBN).collection("Reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> data =queryDocumentSnapshots.getDocuments();

                    idr = data.size()+1;
                    Log.v("idr", String.valueOf(idr));
                    addreview(String.valueOf(idr),idu,review,ISBN,name);
                    compilerating();

                }
                else{
                    idr = 1;
                    addreview(String.valueOf(idr),idu,review,ISBN,name);
                    compilerating();
                }




            }
        });




    }


    public void addreview(String idr,String idu,String review,String ISBN,String name){
        // Add document data  with id staffid using a hashmap
        Map<String, Object> data = new HashMap<String,Object>();

        data.put("uid", idu);
        data.put("vote", 0);
        data.put("review", review);
        data.put("uname",name);
        data.put("isbn",ISBN);
        data.put("rid",idr);
        Map<String, Object> data2= new HashMap<String,Object>();


        data.put("review", review);
        data.put("isbn",ISBN);
        data.put("rid",idr);
        mCollectionRefbooks.document(ISBN).collection("Reviews").document(idr).set(data);
        mCollectionRefuser.document(idu).collection("Reviews").document(idr).set(data2);



    }
    public void compilerating(){
        mCollectionRefbooks.document(ISBN).update("ratecount", FieldValue.increment(1));
        mCollectionRefbooks.document(ISBN).update("TotalRating", FieldValue.increment(ratevalue));
    }
}


