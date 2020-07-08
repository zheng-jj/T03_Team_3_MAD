package com.example.t03team3mad;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.jakewharton.processphoenix.ProcessPhoenix;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class fragment_user extends Fragment {
    private static final String TAG = "userFragment";
    List<Book> newbooklist;
    SharedPreferences Auto_login;
    User usertoView = null;
    View v;
    List<User> currentlyfollow = new ArrayList<>();

    //list of reviews made by this user
    List<Review> reviewsByUser = new ArrayList<>();
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionBook2 = FirebaseFirestore.getInstance().collection("Book");
    private CollectionReference mCollectionRefreview = FirebaseFirestore.getInstance().collection("Reviews");
    AdapterReviewForUSer reviewadapter;

    //list of books
    ArrayList<Book> userfav = new ArrayList<>();
    AdapterBookMain bookadapter;

    ImageView Pic;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        //jj-gets the user currently following list
        try {
            DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
            DBaccess.open();
            currentlyfollow = DBaccess.getUserFollowing(Integer.toString(MainActivity.loggedinuser.getUseridu()));
            DBaccess.close();
        }catch (Exception e){}
        if (bundle.getParcelable("searchuser") != null) {
            Log.v(TAG,"showing search user profile");
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_user,container,false);

            usertoView = bundle.getParcelable("searchuser");
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


            //gets user object from database
            final Button followthisuser = view.findViewById(R.id.follow1);
            //jj-checks if logged in user follows this user
            for(User check : currentlyfollow){
                if(check.getUseridu()==usertoView.getUseridu()){
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
                    for(User user : currentlyfollow){
                        //if user is currently following this user, update the boolean
                        if(user.getUseridu()==usertoView.getUseridu()){
                            follow=true;
                            break;
                        }
                    }
                    //if user is currently following this user and wishes to unfollow, update the list
                    Iterator<User> iter = currentlyfollow.iterator();
                    if(follow==true){
                        while (iter.hasNext()) {
                            User user = iter.next();
                            if (user.getUseridu()==usertoView.getUseridu())
                                iter.remove();
                        }
                    }
                    else {
                        currentlyfollow.add(usertoView);
                    }
                    //creates the string to be entered into database
                    String followid = "";
                    for(User followed : currentlyfollow){
                        followid=followid+Integer.toString(followed.getUseridu())+";";
                    }
                    if(followid.equals("")){
                    }
                    else {
                        //removes the final ";"
                        followid = followid.substring(0, followid.length() - 1);
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
                    if(currentlyfollow.contains(usertoView)){
                        followthisuser.setText("Followed");
                    }
                    else{
                        followthisuser.setText("Follow");
                    }
                }
            });
            //jj-removes the arguements so that i will get the reason why this page is loaded
            this.getArguments().putParcelable("searchuser",null);
        }

        //checks if the user is viewing his own profile
        else {
            //jj- inflates the fragment into the container for the fragment
            view = inflater.inflate(R.layout.fragment_loggeduser,container,false);

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

        }

        v=view;
        Log.v(TAG, "user view: username: "+ usertoView.getUsername());
        int userid = usertoView.getUseridu();
        //jj - loads user into layout
        try {
            Pic = view.findViewById(R.id.userPic);
            String path = getimage(usertoView);
            loaduserintoview(view,usertoView,path);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //user favourite books loaded from firebase

        try {
            userfav = loaduserbooks(usertoView);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(userfav!= null) {
            //jj - load favourite user books recyclerview
            RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favbookslist);
            //jj-layout manager linear layout manager manages the position of the recyclerview items
            LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
            //jj-set the recyclerview's manager to the previously created manager
            favouritebooks.setLayoutManager(llm);
            //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
            loadBookurlsbooks();
            bookadapter = new AdapterBookMain(userfav, this.getContext());
            //jj- set the recyclerview object to its adapter
            favouritebooks.setAdapter(bookadapter);
        }

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
    //jj - gets the user's favourite books
    public ArrayList<Book> loaduserbooks(User user) throws ExecutionException, InterruptedException {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());

        ArrayList<Book> userbooklist = new ArrayList<>();
        AsyncTask<String, Void,ArrayList<Book>> task = new APIaccessBookList(getContext()).execute(user.getUserisbn());

        userbooklist=task.get();
        Log.v(TAG,"fav book list is loaded");
        this.newbooklist=userbooklist;
        return userbooklist;
    }
    //jj- get user reviews made
    public void loaduserreviews(final User user){
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
                                int points = Integer.parseInt(dss.getString("vote"));
                                int uid = Integer.parseInt(dss.getString("uid"));
                                String name = dss.getString("uname");
                                Review r1 = new Review(uid, name, review, points, userID);
                                r1.setReviewisbn(isbn);
                                reviewsByUser.add(r1);
                                Log.v("Test", review);
                                Log.v("Test", String.valueOf(points));
                                Log.v("Test", String.valueOf(uid));
                                Log.v("Test", String.valueOf(name));
                            } else {
                                continue;
                            }
                        }
                    }
                    loadBookurlsreviews();
                    reviewadapter.notifyDataSetChanged();
                }
            }
        });
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
                        Log.v(TAG,"Bookloop="+review.getReviewisbn());
                        for(DocumentSnapshot doc : data){
                            Log.v(TAG,"Docloop="+doc.getReference().getId());
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
}
