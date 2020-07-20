package com.example.t03team3mad;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
//qh - code to upload books
public class fragment_uploadbooks extends Fragment {
    EditText bookisbn;
    EditText bookname;
    EditText bookauthor;
    EditText bookdescription;
    EditText bookpdate;
    EditText bookgenre;
    Button upload;
    private static final String TAG = "uploadbooks";
    private CollectionReference mCollectionBooksNotVerified = FirebaseFirestore.getInstance().collection("BooksNotVerified");


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.uploadbook,container,false);

        upload = view.findViewById(R.id.uploadButton);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bookisbn = view.findViewById(R.id.bookISBN);
                bookname = view.findViewById(R.id.bookName);
                bookauthor = view.findViewById(R.id.bookAuthor);
                bookdescription = view.findViewById(R.id.bookAbout);
                bookpdate = view.findViewById(R.id.bookpdate);
                bookgenre = view.findViewById(R.id.bookGenre);

                //qh - check if book already exist in api
                Book currentbook = null;
                AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(bookisbn.getText().toString());
                Log.v(TAG,"searched ="+bookisbn.getText().toString());

                if (bookisbn.getText().toString().equals("")){
                    Toast.makeText(getContext(), "ISBN is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //qh - isbn has to be 10 or 13 characters long
                if (bookisbn.getText().toString().length() != 10 || bookisbn.getText().toString().length() != 13){
                    Toast.makeText(getContext(), "Invalid ISBN!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookname.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Name is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookauthor.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Author is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookdescription.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Description is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookpdate.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Publishing date is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (bookgenre.getText().toString().equals("")){
                    Toast.makeText(getContext(), "Genre is Empty!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    currentbook = tasktogetbook.get();
                    if(currentbook!=null) {
                        Log.v(TAG,"Book already exists!");
                        Toast.makeText(getContext(), "Book already exists!", Toast.LENGTH_SHORT).show();
                        return;
                    };
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                addtofirestore(bookisbn.getText().toString(),bookname.getText().toString(),bookauthor.getText().toString(),bookdescription.getText().toString(),bookpdate.getText().toString(),bookgenre.getText().toString());

            }

        });

        return view;
    }

    public void addtofirestore(String isbn, String name, String author, String description, String pdate, String genre){
        Map<String, Object> book = new HashMap<>();
        book.put("booktitle", name);
        book.put("bookauthor", author);
        book.put("bookabout", description);
        book.put("bookpdate", pdate);
        book.put("bookgenre", genre);
        book.put("uid",String.valueOf(MainActivity.loggedinuser.getUseridu()));
        mCollectionBooksNotVerified.document(isbn).set(book);
        Toast.makeText(getContext(), "Book added! Please wait for admin approval.", Toast.LENGTH_SHORT).show();
    }

}
