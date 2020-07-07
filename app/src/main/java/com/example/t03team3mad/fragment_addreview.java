
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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.WriteResult;

import java.util.HashMap;
import java.util.Map;

public class fragment_addreview extends Fragment {
    private static final String TAG = "authorprofileFragment";
    Button enter;
    EditText editreview;
    String idu;
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
        // onclick listener for adding review + storing review into database
        enter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Log.v("Click","Button clicked");
                Integer idrcount = Integer.parseInt(getidr());
                String idr = String.valueOf(idrcount + 1);
                String ISBN = book.getIsbn();
                idu = Integer.toString(user.getUseridu());
                String review = editreview.getText().toString();
                boolean test = insertreview(idr,idu,review,ISBN);
                Log.d(TAG,Boolean.toString(test));
                Log.d(TAG,getidr());


            }
        });

        return view;
    }
    // get the latest id of reviews so it can be used to +1 to add another review since it is a primary key
    public String getidr(){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        String idrcount = DBaccess.getCount("IDR","Reviews");
        DBaccess.close();
        return idrcount;
    }
    // add review into the database
    public boolean insertreview(String idr,String idu,String review,String ISBN){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        boolean success = DBaccess.addData(idr,idu,review,ISBN);
        DBaccess.close();
        return success;

    }
    public void addreview(String idr,String idu,String review,String ISBN){
        // Add document data  with id staffid using a hashmap
        Map<String, Object> data = new HashMap<String,Object>();
        data.put("ISBN", ISBN);
        data.put("userID", idu);
        data.put("points", 0);
        data.put("Review", review);
        mCollectionRef.add(data);
    }
}


