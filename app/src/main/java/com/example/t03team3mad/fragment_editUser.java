package com.example.t03team3mad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;

public class fragment_editUser extends Fragment implements AdapterBookMain.OnBookMainListener {
    private static final String TAG = "userEditFragment";
    List<Book> userBooklist = null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_editprofile,container,false);
        //jj - obtains which user to displayBundle bundle = this.getArguments();
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }
        //jj - loads user into layout
        if(usertoEdit!=null) {
            loaduserintoview(view, usertoEdit);
        }

        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favBooksRecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter = new AdapterBookMain(loaduserbooks(usertoEdit),this, this.getContext());
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(bookadapter);
        //onclick listener for button to edit user favourite books
        Button editBooks = view.findViewById(R.id.allfavbooks);
        final User finalUsertoEdit = usertoEdit;
        editBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_editUserBooks fragment_editUserBooks = new fragment_editUserBooks();
                Bundle bundle = new Bundle();
                bundle.putParcelable("UserToEdit", finalUsertoEdit);
                Log.v(TAG,"user sending to editBooks = "+ finalUsertoEdit.getUsername());
                fragment_editUserBooks.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mainactivitycontainer, fragment_editUserBooks, "editUserBooks")
                        .addToBackStack(null)
                        .commit();
            }
        });

        //edits user and refreshes the fragment
        Button saveEdits = view.findViewById(R.id.savechanges);
        final User finalUsertoEdit1 = usertoEdit;
        saveEdits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mainview = v.getRootView();
                EditText Name = mainview.findViewById(R.id.editName);
                EditText Desc = mainview.findViewById(R.id.editDes);
                finalUsertoEdit1.setUsername(Name.getText().toString());
                finalUsertoEdit1.setUserabout(Desc.getText().toString());
                Log.v(TAG,"new user name: "+finalUsertoEdit1.getUsername()+" New user desc: "+finalUsertoEdit1.getUserabout());
                //jj-updates database on new user details
                DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                DBaccess.open();
                DBaccess.editUserData(finalUsertoEdit1);
                DBaccess.close();
                //reloads the fragment
                Fragment edituserfragment= getFragmentManager().findFragmentByTag("editUser");
                final FragmentTransaction refreshthisfragment = getFragmentManager().beginTransaction();
                refreshthisfragment.detach(edituserfragment);
                refreshthisfragment.attach(edituserfragment);
                refreshthisfragment.commit();

                //updates global variable
                MainActivity.loggedinuser=finalUsertoEdit1;
            }
        });
        return view;
    }
//    public List<User> loadAllusers()
//    {
//        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
//        DBaccess.open();
//        List<User> mUserlist = DBaccess.loadalluserlist();
//        DBaccess.close();
//        return mUserlist;
//    }
    //jj - gets the user's favourite books
    public List<Book> loaduserbooks(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> userbooklist = DBaccess.loaduserbooklist(user);
        DBaccess.close();
        Log.v(TAG,"list is loaded");
        return userbooklist;
    }
    //jj - Loads the user information into the layout
    public void loaduserintoview(View view, User user){
        ImageButton Pic = view.findViewById(R.id.newprofileimg);
        EditText Name = view.findViewById(R.id.editName);
        EditText Desc = view.findViewById(R.id.editDes);
        Log.v(TAG,"current user name= "+user.getUsername());
        Log.v(TAG,"current user dec= "+user.getUserabout());
        Name.setText(user.getUsername());
        Desc.setText(user.getUserabout());
        String filename = "user" +Integer.toString(user.getUseridu()) +".jpg";
        Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
        Pic.setImageBitmap(bmImg);
    }
    //qh -- the user object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBookMainClick(int position) {

    }
}
