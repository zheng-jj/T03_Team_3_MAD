package com.example.t03team3mad;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class fragment_editUser extends Fragment {
    private static final String TAG = "userEditFragment";
    List<Book> userBooklist = null;
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    private static final int RESULTLOADIMAGE=1;
    AdapterBookMain bookadapter;
    ArrayList<Book> userfav = new ArrayList<>();
    ImageButton Pic;

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
        Pic=view.findViewById(R.id.newprofileimg);

        try {
            userfav=loaduserbooks(usertoEdit);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.favBooksRecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        loadBookurls();

        bookadapter = new AdapterBookMain(userfav, this.getContext());
        if(userfav!=null) {
            //jj- set the recyclerview object to its adapter
            favouritebooks.setAdapter(bookadapter);
        }

        //onclick listener for button to edit user favourite books
        Button editBooks = view.findViewById(R.id.allfavbooks);
        final User finalUsertoEdit = usertoEdit;
        //brings user to edit all user books fragment
        editBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_editUserBooks fragment_editUserBooks = new fragment_editUserBooks();
                Bundle bundle = new Bundle();
                bundle.putParcelable("UserToEdit", finalUsertoEdit);
                Log.v(TAG,"user sending to editBooks = "+ finalUsertoEdit.getUsername());
                fragment_editUserBooks.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment_editUserBooks,getActivity(),"editUserBooks");
            }
        });

        //jj-edits user and refreshes the fragment
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
                User todatabase = finalUsertoEdit1;
                if(todatabase.getUserisbn()==""){
                    todatabase.setUserisbn(null);
                }
                //jj-updates database on new user details
                DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
                DBaccess.open();
                DBaccess.editUserData(todatabase);
                DBaccess.close();
                if(finalUsertoEdit.getUserisbn()==null){
                    finalUsertoEdit.setUserisbn("");
                }
                //updates firestore
                AsyncTask<User,Void,Void> tasktoupdateUser = new updateFireStoreUser.AccessUser().execute(finalUsertoEdit1);
                try {
                    tasktoupdateUser.get();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //reloads the fragment
                fragment_user fragment = new fragment_user();

                //updates global variable
                MainActivity.loggedinuser=finalUsertoEdit1;

                //updates database on new image
                Bitmap userprofileimg = ((BitmapDrawable) Pic.getDrawable()).getBitmap();
                //saves image from gallery to internal storage
                try {
                    SaveBitmap(userprofileimg,Integer.toString(finalUsertoEdit1.getUseridu()));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //jj- bundle to be moved to fragment
                Bundle bundle = new Bundle();
                bundle.putParcelable("loggedin", finalUsertoEdit1);
                fragment.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment,getActivity(),"UserFragment");
            }
        });

        //jj-upload image from gallery
        Pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //jj-creates a new intent to PICK image from the media folder
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //jj-when gallery intent is started, we need to get back a result, hence use startActivityforresult instead of start activity
                startActivityForResult(gallery, RESULTLOADIMAGE);
            }
        });
        return view;
    }
    //jj-method which is called when the user selects image from gallery(startActivityForResult
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checks if the gallery intent is the one who called this method, and if data returned is not null, and also checks if result code is correct(user successfully select image)
        if(requestCode == RESULTLOADIMAGE && data!=null && resultCode==Activity.RESULT_OK ){
            Uri selected = data.getData();
            //sets picture to imagebutton widget
            Pic.setImageURI(selected);
        }
    }
    //jj- used to save a bitmap to internal storage
    private void SaveBitmap(Bitmap bitmap, String uid) throws ExecutionException, InterruptedException {
        FileOutputStream outStream = null;
        try {
            File directory = new File("/data/data/com.example.t03team3mad/app_imageDir");
            directory.mkdirs();
            //image saved name
            String fileName = "user"+uid+".jpg";
            //creates the file
            File filetosave = new File(directory, fileName);
            //saves the file
            outStream = new FileOutputStream(filetosave);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
            //jj-media scanner is used to scan all media files without having to restart application
            MediaScannerConnection.scanFile(getActivity(), new String[] { "/data/data/com.example.t03team3mad/app_imageDir" }, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri)
                {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }



        //saves bitmap to firestore
        String filename = "user" +uid +".jpg";
        imageTaskSaveParameters parameters = new imageTaskSaveParameters(filename,bitmap);
        //jj gets image from firebase and saves to local storage
        AsyncTask<imageTaskSaveParameters, Void, Void> task = new FirebaseStorageImageSave().execute(parameters);
        task.get();
    }

    public ArrayList<Book> loaduserbooks(User user) throws ExecutionException, InterruptedException {

        ArrayList<Book> userbooklist = new ArrayList<>();

        AsyncTask<String, Void,ArrayList<Book>> task = new APIaccessBookList(getContext()).execute(user.getUserisbn());

        userbooklist=task.get();
        Log.v(TAG,"fav book list is loaded");
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

    public void loadBookurls() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    if(userfav!=null) {
                        for (Book book : userfav) {
                            Log.v(TAG, "Bookloop=" + book.getIsbn());
                            for (DocumentSnapshot doc : data) {
                                Log.v(TAG, "Docloop=" + doc.getReference().getId());
                                if (doc.getReference().getId().equals(book.getIsbn())) {
                                    book.setimglink(doc.getString("coverurl"));
                                }
                            }
                        }
                    }
                    bookadapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
