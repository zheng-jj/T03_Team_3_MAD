package com.T03G3.eLibtheBookManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.T03G3.eLibtheBookManager.model.Book;
import com.T03G3.eLibtheBookManager.model.Review;
import com.T03G3.eLibtheBookManager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jakewharton.processphoenix.ProcessPhoenix;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class fragment_user extends Fragment {
    private static final String TAG = "userFragment";
    List<Book> newbooklist;
    SharedPreferences Auto_login;
    User usertoView = null;
    View v;
    RecyclerView favouritebooks;
    User toview = null;
    //list of reviews made by this user

    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionBook2 = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefreview = FirebaseFirestore.getInstance().collection("Reviews");
    AdapterReviewForUSer reviewadapter;
    List<Review> reviewsByUser = new ArrayList<>();
    //list of books
    ArrayList<Book> userfav = new ArrayList<>();
    AdapterBookMain bookadapter;
    View pageview;
    ImageView Pic;
    Button reportuser;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userfav = new ArrayList<>();
        View view;
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        //jj-gets the user currently following list


        if (bundle.getParcelable("searchuser") != null||MainActivity.viewuser != null) {
            Log.v(TAG,"showing search user profile");
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_user,container,false);
            this.pageview = view;
            if(MainActivity.viewuser!=null) {
                usertoView = MainActivity.viewuser;
            }
            else {
                usertoView = bundle.getParcelable("searchuser");
            }
            Log.v(TAG,"currently viewing ="+ String.valueOf(usertoView.getUseridu()));
            Log.v(TAG, "currently logged in ="+String.valueOf(MainActivity.loggedinuser.getUseridu()));
            //checks if user is viewing himself
            if(usertoView.getUseridu()==MainActivity.loggedinuser.getUseridu()){
                this.getArguments().putParcelable("searchuser",null);
                fragment_user fragment = new fragment_user();
                //jj- bundle to be moved to fragment
                Bundle bundle2 = new Bundle();
                bundle2.putParcelable("loggedin", MainActivity.loggedinuser);
                fragment.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment,getActivity(),"UserFragment");
            }
            //report user button
            reportuser = view.findViewById(R.id.reportuser);
            reportuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    reportuserfragment reportfragment = new reportuserfragment();
                    //jj- bundle to be moved to fragment
                    Bundle bundle3 = new Bundle();
                    bundle3.putParcelable("userreport", usertoView);
                    reportfragment.setArguments(bundle3);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(reportfragment,getActivity(),"ReportFragment");
                }
            });

            //gets user object from database
            final Button followthisuser = view.findViewById(R.id.follow1);

            toview = usertoView;

            final List<String> listofid = new ArrayList<String>(Arrays.asList(MainActivity.loggedinuser.getfollowingstring().split(";")));
            Log.v(TAG,"list of user following ="+MainActivity.loggedinuser.getfollowingstring());
            Log.v(TAG,"viewing user ="+usertoView.getUseridu());
            //jj-checks if logged in user follows this user
            for(String id : listofid){
                if(id.equals(Integer.toString(usertoView.getUseridu()))){
                    followthisuser.setText("Followed");
                    break;
                }
                else{
                    followthisuser.setText("Follow");
                }
            }
            //jj-sets up button which allows currently logged in user to follow the user
            followthisuser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Boolean follow = false;
                    for(String id : listofid){
                        //if user is currently following this user, update the boolean
                        if(id.equals(Integer.toString(usertoView.getUseridu()))){
                            follow=true;
                            break;
                        }
                    }
                    //if user is currently following this user and wishes to unfollow, update the list
                    Iterator<String> iter = listofid.iterator();
                    final SharedPreferences unsub = getActivity().getSharedPreferences("Unsub", Context.MODE_PRIVATE);
                    if(follow==true){
                        while (iter.hasNext()) {
                            String user = iter.next();
                            if (user.equals(Integer.toString(usertoView.getUseridu()))) {
                                iter.remove();
                            }
                        }
                        //jj- unsub notifications when user unfollow
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"review",true).commit();
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"noti",true).commit();
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"fav",true).commit();
                    }
                    else {
                        //jj- sub notifications when user follow
                        sendNotification();
                        listofid.add(Integer.toString(usertoView.getUseridu()));
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"review",false).commit();
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"noti",false).commit();
                        unsub.edit().putBoolean("User"+usertoView.getUseridu()+"fav",false).commit();
                    }
                    //creates the string to be entered into database
                    String followid = "";
                    if(listofid!=null) {
                        for (String followed : listofid) {
                            followid = followid + followed + ";";
                        }
                        if (followid.equals("")) {
                        } else {
                            //removes the final ";"
                            followid = followid.substring(0, followid.length() - 1);
                        }
                    }
                    Log.v(TAG,"Following list "+followid);
                    DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                    DBaccess.open();
                    DBaccess.updateUserFollowing(MainActivity.loggedinuser,followid);
                    DBaccess.close();
                    MainActivity.loggedinuser.setfollowingstring(followid);
                    AsyncTask<User,Void,Void> tasktoupdateUser = new updateFireStoreUser.AccessUser().execute(MainActivity.loggedinuser);
                    try {
                        tasktoupdateUser.get();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //updates text on button
                    if(listofid.contains(Integer.toString(usertoView.getUseridu()))){
                        followthisuser.setText("Followed");
                    }
                    else{
                        followthisuser.setText("Follow");
                    }
                }
            });
            //jj - loads user into layout
            Pic = view.findViewById(R.id.userPic);
            String path = null;
            try {
                path = getimage(usertoView);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                loaduserintoview(view,usertoView,path);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            //jj-removes the arguements so that i will get the reason why this page is loaded
            this.getArguments().putParcelable("searchuser",null);
            MainActivity.viewuser = null;
        }

        //checks if the user is viewing his own profile
        else {
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);
            Log.v(TAG,"currently logged in :"+MainActivity.loggedinuser.getUsername());
            usertoView = MainActivity.loggedinuser;
            //onclick listener to edit profile
            Button editprofile = view.findViewById(R.id.edit);

            final User finalUsertoView = usertoView;
            Log.v(TAG,"showing current user profile");
            editprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("UserToEdit", finalUsertoView);
                    Log.v(TAG,"user sending to edit = "+finalUsertoView.getUsername());
                    fragment_editUser fragment_editUser = new fragment_editUser();
                    fragment_editUser.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(fragment_editUser,getActivity(),"EditUser");
                }
            });
            Button following = view.findViewById(R.id.viewfollowing);
            following.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("UserToEdit", finalUsertoView);
                    Log.v(TAG,"user sending to edit = "+finalUsertoView.getUsername());
                    fragment_userfollowing fragment_userFollowing = new fragment_userfollowing();
                    fragment_userFollowing.setArguments(bundle);
                    //jj-updated the way we add fragments into the view
                    MainActivity.addFragment(fragment_userFollowing,getActivity(),"UserFollowing");
                }
            });
            //jj-button to logout
            LoginPage temp =  new LoginPage(getActivity());
            Auto_login = temp.getLogincontext().getSharedPreferences("LoginButton",Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = Auto_login.edit();
            final Button logout = view.findViewById(R.id.logout);
            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //creates alert to log out
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Log Out");
                    builder.setMessage("Are you sure you want to log out?");
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //jj-stops auto login
                            editor.putBoolean("logged", false);
                            editor.commit();
                            //external library used to restart application
                            ProcessPhoenix.triggerRebirth(logout.getRootView().getContext());
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            Log.v(TAG,"user chose not to log out");
                        }
                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            });
            Button editnotifications = view.findViewById(R.id.noti);
            editnotifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent editnoti= new Intent(getContext(),EditNotifications.class);
                    startActivity(editnoti);
                }
            });

            //jj - loads user into layout
            Pic = view.findViewById(R.id.userPic);
            String path = null;
            try {
                path = getimage(MainActivity.loggedinuser);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                loaduserintoview(view,MainActivity.loggedinuser,path);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
        this.getArguments().putParcelable("searchuser",null);
        v=view;
        Log.v(TAG, "user view: username: "+ usertoView.getUsername());



        //jj - load favourite user books recyclerview
        favouritebooks = (RecyclerView) view.findViewById(R.id.favbookslist);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        bookadapter = new AdapterBookMain(userfav, getContext());
        favouritebooks.setAdapter(bookadapter);

        AsyncTask<String, Void, Void> task = new APIaccessBookListUser(getContext()).execute(usertoView.getUserisbn());
        try {
            task.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        loadBookurlsbooks();

        //jj - load user reviews recyclerview
        RecyclerView pastReviews = (RecyclerView) view.findViewById(R.id.userreviewprofile);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        pastReviews.setLayoutManager(llm2);

        //loads data from firestore into list
        loaduserreviews(usertoView);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        reviewadapter = new AdapterReviewForUSer(reviewsByUser);
        //jj- set the recyclerview object to its adapter
        pastReviews.setAdapter(reviewadapter);

        return view;
    }

    //jj- get user reviews made
    public void loaduserreviews(final User user){
        reviewsByUser.clear();
        mCollectionRefreview.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot dss : data) {
                        String userID = dss.getString("uid");
                        if(userID!=null) {
                            if (userID.equals(String.valueOf(user.getUseridu()))) {
                                String review = dss.getString("review");
                                String isbn = dss.getString("isbn");
                                String title = dss.getString("title");
                                int uid = Integer.parseInt(dss.getString("uid"));
                                Review r1 = new Review(uid, review,title,isbn);
                                reviewsByUser.add(r1);
                                Log.v("Test", review);

                            } else {
                                continue;
                            }
                        }
                    }
                    checkforreviewduplicates();
                    loadBookurlsreviews();
                }
            }
        });
    }
    public void checkforreviewduplicates(){
        Set<Review> s= new HashSet<Review>();
        s.addAll(reviewsByUser);
        reviewsByUser = new ArrayList<Review>();
        reviewsByUser.addAll(s);
    }

    //jj - Loads the user information into the layout
    public void loaduserintoview(View view, User user, String path) throws InterruptedException, ExecutionException {
        TextView Name = view.findViewById(R.id.userName);
        TextView Desc = view.findViewById(R.id.userDescription);
        Name.setText(user.getUsername());
        Desc.setText(user.getUserabout());

        Log.v(TAG,"image id being used = user+"+ Integer.toString(user.getUseridu()));
        //String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        //jj gets image from firebase and saves to local storage
        //sets the profile image
        File check = new File(path);
        int count = 20;
        while(count>0){
            Log.v(TAG,"user image is not saved yet");
            if(check.exists()) {
                Pic.setImageBitmap(BitmapFactory.decodeFile(path));
                Pic.invalidate();
                if(Pic.getDrawable() != null){
                    Pic.setBackgroundResource(0);
                    TimeUnit.MILLISECONDS.sleep(100);
                    Pic.setImageBitmap(BitmapFactory.decodeFile(path));
                    break;
                }
                else {
                    continue;
                }
            }
            else{
                Thread.sleep(100);
            }
            count=count-1;
        }
    }
    //jj-method that gets the image and saves to internal storage
    public String getimage(User user) throws ExecutionException, InterruptedException {
        String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        //jj gets image from firebase and saves to local storage
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.v(TAG,"USER FRAGMENT RECREATED");
        super.onAttach(context);
    }
    //jj -  loads the url into book objects for recommended books
    public void loadBookurlsreviews() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Review review : reviewsByUser){
                        for(DocumentSnapshot doc : data){
                            if(doc.getReference().getId().equals(review.getReviewisbn())){
                                review.setImglink(doc.getString("coverurl"));
                            }
                        }
                    }
                    reviewadapter.notifyDataSetChanged();
                }
            }
        });
    }



    //jj- the following is used for notifications
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private void sendNotification() {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    OkHttpClient client = new OkHttpClient();
                    JSONObject json=new JSONObject();
                    JSONObject dataJson=new JSONObject();
                    dataJson.put("body",MainActivity.loggedinuser.getUsername()+" has followed "+toview.getUsername()+"!");
                    dataJson.put("title","Check it out!");
                    json.put("notification",dataJson);
                    json.put("to","/topics/User"+MainActivity.loggedinuser.getUseridu()+"follow");
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


    //jj loads the url into book objc
    public void loadBookurlsbooks() {
        mCollectionBook2.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Book book : userfav){
                        Log.v(TAG,"Bookloop="+book.getIsbn());
                        for(DocumentSnapshot doc : data){
                            Log.v(TAG,"Docloop="+doc.getReference().getId());
                            if(doc.getReference().getId().equals(book.getIsbn())){
                                book.setimglink(doc.getString("coverurl"));
                            }
                        }
                    }
                    bookadapter.notifyDataSetChanged();
                }
            }
        });
    }

    //jj-moved the method here so that there wont be crashes due to slow load times
    public class APIaccessBookListUser  extends AsyncTask<String,Void, Void> {
        ProgressDialog dialog;
        private Context mContext;

        public APIaccessBookListUser (Context context ){
            mContext = context;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(mContext);
            dialog.setIndeterminate(true);
            dialog.setMessage("Please Wait...");
            dialog.setTitle("Loading Messages");
            dialog.setCancelable(true);
            dialog.show();
        }



        //jj- api url
        private String apiurl = "https://www.googleapis.com/";
        private static final String TAG = "APIaccessBookListUser";
        //jj-empty contructor
        public APIaccessBookListUser(){}
        //jj - search book by isbn using API
        public void searchbookbyisbn(String isbn) throws IOException, JSONException {
            BufferedReader reader = null;
            ArrayList<Book> listofresults = new ArrayList<>();
            if(isbn!=null) {
                String[] isbns = isbn.split(";");
                //jj-loops through isbn
                for (String isbntosearch : isbns) {
                    if(isbntosearch.equals(null)||isbntosearch.equals("")) {
                        continue;
                    }
                    final Boolean[] uploaded = {false};
                    Log.v(TAG,"ISBn="+isbntosearch);
                    FirebaseFirestore.getInstance().collection("Book").document(isbntosearch).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                if (documentSnapshot.getBoolean("uploaded")) {

                                    uploaded[0] =true;
                                    Book toadd = new Book();
                                    toadd.setratng(documentSnapshot.getLong("TotalRating").intValue());
                                    toadd.setBookabout(documentSnapshot.getString("bookabout"));
                                    toadd.setBookauthor(documentSnapshot.getString("bookauthor"));
                                    toadd.setBookgenre(documentSnapshot.getString("bookgenre"));
                                    toadd.setPdate(documentSnapshot.getString("bookpdate"));
                                    toadd.setBooktitle(documentSnapshot.getString("booktitle"));
                                    toadd.setIsbn(documentSnapshot.getId());
                                    userfav.add(toadd);
                                } else {

                                }
                            } else {
                            }
                        }
                    });
                    //jj- if book is not uploaded, search api
                    if (!uploaded[0]) {
                        Log.v(TAG, "Searching api isbn =" + isbntosearch);
                        //jj-sets the url to GET data as json
                        URL url = new URL(apiurl + "books/v1/volumes?q=isbn:" + isbntosearch);
                        Log.v(TAG, "url searching =" + url.toString());
                        //jj-opens the connection
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");
                        int responsecode = conn.getResponseCode();
                        //jj-checks if the response is successful
                        if (responsecode != 200)
                            throw new RuntimeException("HttpResponseCode: " + responsecode);
                        else {
                            //jj - gets the string from the url
                            JSONObject bookjsonobj = null;
                            InputStream stream = conn.getInputStream();
                            reader = new BufferedReader(new InputStreamReader(stream));

                            StringBuffer buffer = new StringBuffer();
                            String line = "";
                            while ((line = reader.readLine()) != null) {
                                buffer.append(line + "\n");
                            }
                            String newstring = buffer.toString();
                            bookjsonobj = new JSONObject(newstring);
                            //jj - use this get data from json object to parse into object
                            if (bookjsonobj != null) {
                                String booktitle = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("title");
                                Log.v(TAG, "Creation");
                                Log.v(TAG, booktitle);

                                //qh - added this block of code because some books dont have value
                                String bookauthor;
                                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("authors")) {
                                    bookauthor = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("authors").getString(0);
                                } else {
                                    bookauthor = "Author Data not Available";
                                }
                                Log.v(TAG, bookauthor);
                                //qh - added this block of code because some books dont have value
                                String genrelist = "";
                                String bookgenre;
                                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("categories")) {
                                    JSONArray subjects = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getJSONArray("categories");
                                    //jj-loops through all the subjects in the list of subjects and adds to a string
                                    for (int i = 0; i < subjects.length(); i++) {
                                        genrelist = genrelist + subjects.getString(i) + ";";
                                    }
                                    //jj-removes the last ";" at the end of list of genres
                                    bookgenre = genrelist.substring(0, genrelist.length() - 1);
                                } else {
                                    bookgenre = "Genres not available";
                                }

                                //qh - added this block of code because some books dont have value
                                String bookdes;
                                if (bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").has("description")) {
                                    bookdes = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("description");
                                } else {
                                    bookdes = "No Description";
                                }
                                String bookpdate = bookjsonobj.getJSONArray("items").getJSONObject(0).getJSONObject("volumeInfo").getString("publishedDate");

                                //jj-creates the book object with json data

                                Book x = new Book(booktitle, bookauthor, bookdes, bookgenre, bookpdate, isbntosearch);
                                Log.v(TAG, "Book created =" + x.getIsbn() + "====" + x.getBookgenre() + "====" + x.getBooktitle() + "====" + x.getBookabout());
                                Log.v(TAG, "added+" + x.getIsbn());
                                userfav.add(x);
                            } else {
                                Book x = new Book("Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available", "Book data not available");
                                userfav.add(x);
                            }
                        }
                    }
                }
            }
            loadBookurlsbooks();

            bookadapter.mBooklist=userfav;
            bookadapter.notifyDataSetChanged();

        }
        private Exception exception;

        //jj-method that calls the searchbook method and runs it in background
        @Override
        protected Void doInBackground(String... urls) {
            try {
                searchbookbyisbn(urls[0]);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            dialog.dismiss();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }
}
