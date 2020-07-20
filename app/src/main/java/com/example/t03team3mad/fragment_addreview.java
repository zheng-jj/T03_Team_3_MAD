package com.example.t03team3mad;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.google.firebase.firestore.WriteBatch;
import com.google.firestore.v1.Document;
import com.google.firestore.v1.WriteResult;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
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
    String title;
    ImageView bookimage;
    int aid;
    int temp = 0;
    List<String> uids = new ArrayList<String>();
    List<String> followingid = new ArrayList<String>();

    Book toview = null;

    private CollectionReference mCollectionRef = FirebaseFirestore.getInstance().collection("Reviews");
    private CollectionReference mCollectionRefuser = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionRefbooks = FirebaseFirestore.getInstance().collection("Book");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // jo -display fragment
        View view = inflater.inflate(R.layout.subfragment_writereview, container, false);
        // jo - get bundle from another fragment
        Bundle bundle = this.getArguments();
        final Book book = bundle.getParcelable("book");

        //jj- this variable is for notification
        toview=book;

        idu = String.valueOf(MainActivity.loggedinuser.getUseridu());
        name = MainActivity.loggedinuser.getUsername();
        title=  book.getBooktitle();
        //jo - find viewbyids
        enter =  view.findViewById(R.id.enter);
        editreview = view.findViewById(R.id.reviewinput);
        rtitle2 = view.findViewById(R.id.rtitle2);
        rtitle2.setText(book.getBooktitle());
        ISBN = book.getIsbn();
        Log.d("Test",ISBN);
        ratings = view.findViewById(R.id.ratingBar);
        bookimage = view.findViewById(R.id.bookimg2);
        if(book.getimglink() == null || book.getimglink() == ""){
            bookimage.setImageResource(R.drawable.empty);

        }
        else {
            Picasso.with(this.getActivity()).load(book.getimglink()).into(bookimage);
        }


        // onclick listener for adding review + storing review into database
        enter.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                review = editreview.getText().toString();
                Log.d("COUNT", String.valueOf(review.length()));
                if(review.length()==0){
                    Toast.makeText(getActivity().getApplicationContext(),"No empty input",Toast.LENGTH_SHORT).show();


                }
                else if(review.length() > 100){
                    Toast.makeText(getActivity().getApplicationContext(),"Max word count 100",Toast.LENGTH_SHORT).show();

                }
                else{
                    sendNotification();

                    ratevalue = ratings.getRating();
                    Log.v("RateValue", String.valueOf(ratevalue));
                    getidr();

                    getFragmentManager().popBackStack("HomeFragment",0);

                }

            }
        });

        return view;
    }
    // get the latest id of reviews so it can be used to +1 to add another review since it is a primary key
    public void getidr(){

        mCollectionRefbooks.document(ISBN).collection("Reviews").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty()){

                    for(QueryDocumentSnapshot i:queryDocumentSnapshots){
                        if(Integer.parseInt(i.getId())>temp){
                            temp  = Integer.parseInt(i.getId());

                        }
                    }
                    idr = temp+ 1;
                    Log.v("idr", String.valueOf(idr));
                    getfollowing(String.valueOf(idr),idu,review,ISBN,name);
                    compilerating();

                }
                else{
                    idr = 1;
                    Log.v("idr", String.valueOf(idr));
                    getfollowing(String.valueOf(idr),idu,review,ISBN,name);
                    compilerating();
                }




            }
        });




    }
    public void getaid(final Map<String, Object> data3, final String id){
        mCollectionRefuser.document(id).collection("Activity").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                if(!queryDocumentSnapshots.isEmpty()){
                    List<DocumentSnapshot> data =queryDocumentSnapshots.getDocuments();
                    aid = data.size()+1;
                    data3.put("position",aid);
                    mCollectionRefuser.document(id).collection("Activity").document(String.valueOf(aid)).set(data3);
                    Log.d("Test", "Load into user?x1");
                    Log.d("Test", id);



                }
                else{
                    aid = 1;
                    data3.put("position",aid);
                    mCollectionRefuser.document(id).collection("Activity").document(String.valueOf(aid)).set(data3);
                    Log.d("Test", "Load into user?x2");
                    Log.d("Test", id);

                }




            }
        });

    }



    public void compilerating(){
        mCollectionRefbooks.document(ISBN).update("ratecount", FieldValue.increment(1));
        mCollectionRefbooks.document(ISBN).update("TotalRating", FieldValue.increment(ratevalue));
    }
    public void getfollowing(final String idr, final String idu, final String review, final String ISBN, final String name){
        // Add document data  with id staffid using a hashmap
        Map<String, Object> data = new HashMap<String,Object>();

        data.put("uid", idu);
        data.put("vote", 0);
        data.put("review", review);
        data.put("uname",name);
        data.put("isbn",ISBN);
        data.put("rid",idr);
        Map<String, Object> data2= new HashMap<String,Object>();
        data2.put("review", review);
        data2.put("isbn",ISBN);
        data2.put("uid", idu);
        data2.put("title",title);
        data2.put("rid",idr);
        final Map<String, Object> data3 = new HashMap<String,Object>();
        data3.put("Activity","Review");
        data3.put("Rating",ratevalue);
        data3.put("Review", review);
        data3.put("isbn",ISBN);
        data3.put("rid",idr);
        data3.put("title",title);
        data3.put("uname",name);
        Log.d("Test",idr);

        mCollectionRefbooks.document(ISBN).collection("Reviews").document(idr).set(data);
        mCollectionRef.add(data2);
        mCollectionRefuser.document(idu).collection("Reviews").add(data2);
        mCollectionRefuser.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                Log.d("Test","123");
                for(QueryDocumentSnapshot i: queryDocumentSnapshots){
                    String temp = i.getString("following");
                    String[] follwings = temp.split(";");
                    for(String z : follwings){
                        if(z.equals(idu)){
                            getaid(data3,i.getId());
                        }
                        else{
                            continue;
                        }
                    }
                }


            }
        });

    }


    //jj- this is for notifications
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification() {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",MainActivity.loggedinuser.getUsername()+" has written a review "+toview.getBooktitle()+"!");
                    dataJson.put("title","Check it out!");
                    json.put("notification",dataJson);
                    json.put("to","/topics/User"+MainActivity.loggedinuser.getUseridu()+"review");
                    Log.v(TAG,json.toString());
                    RequestBody body = RequestBody.create(JSON, json.toString());
                    Request request = new Request.Builder()
                            .header("Authorization","key="+"AAAARRpA2ik:APA91bGMQumkw5FL-Xt_yj_ULIjb91TPQIzfi-ZCM4gEHB47wd-W1jORTJsx3YKiSbv-AMlN1zWJOl6peBAFvWkSZ2QFGRPGcHiHvaYjcQZMwRJfm8wKwUiSpR32-u1ODGte42xYQ9gl")
                            .url("https://fcm.googleapis.com/fcm/send")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    response.isSuccessful();
                    Log.v(TAG,"response ="+response.isSuccessful());
                    String finalResponse = response.body().string();
                    Log.v(TAG, finalResponse);
                }catch (Exception e){
                    //Log.d(TAG,e+"");
                }
                return null;
            }
        }.execute();

    }

}