package com.example.t03team3mad;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class fragment_editUserBooks extends Fragment {
    private static final String TAG = "edituserbooksFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edituserbooks,container,false);
        Bundle bundle = this.getArguments();
        User usertoEdit = null;
        if (bundle.getParcelable("UserToEdit") != null) {
            usertoEdit = bundle.getParcelable("UserToEdit");
            Log.v(TAG, "user edit: username: "+ usertoEdit.getUsername());
        }

        ArrayList<Book> userfav = new ArrayList<>();
        try {
            userfav = loaduserbooks(usertoEdit);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //jj - load favourite user books recyclerview
        final RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.edituserbookrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        final AdapterFavBooksList bookadapter = new AdapterFavBooksList(userfav);
        if(userfav!=null) {
            //jj- set the recyclerview object to its adapter
            favouritebooks.setAdapter(bookadapter);
        }


        Button savebookchanges = view.findViewById(R.id.savebooks);
        final User finalUsertoEdit = usertoEdit;
        //choose to remove/unremove books from user's list
        final ArrayList<Book> finalUserfav = userfav;
        savebookchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalUserfav!=null) {
                    for (Book x : finalUserfav) {
                        Log.v(TAG, "Previously in list book :" + x.getBooktitle() + " " + x.getIsbn());
                    }
                }
                String isbn = "";
                if(finalUserfav!=null) {
                    for (Book favoritebook : bookadapter.mBooklistToBeRemoved) {
                        Log.v(TAG, "Removing book :" + favoritebook.getBooktitle() + " " + favoritebook.getIsbn());
                        finalUserfav.remove(favoritebook);
                    }
                }
                if(finalUserfav!=null) {
                    for (Book remainbooks : finalUserfav) {
                        Log.v(TAG, "Currently in list book :" + remainbooks.getBooktitle() + " " + remainbooks.getIsbn());
                        isbn = isbn + remainbooks.getIsbn() + ";";
                    }
                }
                if (isbn.equals("")){
                    isbn="";
                }
                else {
                    isbn.substring(0, isbn.length() - 1);
                }
                Log.v(TAG,"New isbn user =  "+isbn);
                finalUsertoEdit.setUserisbn(isbn);

                Bundle bundle = new Bundle();
                bundle.putParcelable("UserToEdit", finalUsertoEdit);
                Log.v(TAG,"user sending to edit = "+finalUsertoEdit.getUsername());
                fragment_editUser fragment_editUser = new fragment_editUser();
                fragment_editUser.setArguments(bundle);
                //jj-updated the way we add fragments into the view
                MainActivity.addFragment(fragment_editUser,getActivity(),"EditUserBooks");
            }
        });
        return view;
    }
    //jj-loads the books user liked
    //jj - gets the user's favourite books
    public ArrayList<Book> loaduserbooks(User user) throws ExecutionException, InterruptedException {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        //DBaccess.open();
        ArrayList<Book> userbooklist = new ArrayList<>();
//        try {
//            //foreach book in user's local db favourited books, get it from the api
//            for(Book book:DBaccess.loaduserbooklist(DBaccess.searchuserbyid(Integer.toString(MainActivity.loggedinuser.getUseridu())))) {
//                AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(book.getIsbn());
//                try {
//                    Book temp = tasktogetbook.get();
//                    if (temp != null) {
//                        Log.v(TAG, "Book created = " + temp.getBooktitle());
//                        Log.v(TAG, "Book isbn = " + temp.getIsbn());
//                        Log.v(TAG, "Book about = " + temp.getBookabout());
//                        Log.v(TAG, "Book date = " + temp.getPdate());
//                        Log.v(TAG, "Book genre = " + temp.getBookgenre());
//                        Log.v(TAG, "Book author = " + temp.getBookauthor());
//                        userbooklist.add(temp);
//                    }
//                } catch (ExecutionException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }catch (Exception e){}
//        DBaccess.close();

        AsyncTask<String, Void,ArrayList<Book>> task = new APIaccessBookList(getContext()).execute(user.getUserisbn());

        userbooklist=task.get();
        Log.v(TAG,"fav book list is loaded");
        return userbooklist;
    }
}
