package com.example.t03team3mad;

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

import java.util.List;

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
        final List<Book> userfav = loaduserbooks(usertoEdit);
        //jj - load favourite user books recyclerview
        final RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.edituserbookrecycler);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        final AdapterFavBooksList bookadapter = new AdapterFavBooksList(userfav);
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(bookadapter);


        Button savebookchanges = view.findViewById(R.id.savebooks);
        final User finalUsertoEdit = usertoEdit;
        //choose to remove/unremove books from user's list
        savebookchanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Book x: userfav){
                    Log.v(TAG,"Previously in list book :"+x.getBooktitle() + " "+x.getIsbn());
                }
                String isbn = "";
                for(Book favoritebook : bookadapter.mBooklistToBeRemoved){
                    Log.v(TAG,"Removing book :"+favoritebook.getBooktitle() + " "+favoritebook.getIsbn());
                    userfav.remove(favoritebook);
                }

                for(Book remainbooks : userfav){
                    Log.v(TAG,"Currently in list book :"+remainbooks.getBooktitle() + " "+remainbooks.getIsbn());
                    isbn=isbn+remainbooks.getIsbn()+";";
                }
                if (isbn.equals("")){
                    isbn=null;
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
    public List<Book> loaduserbooks(User user){
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> userbooklist = DBaccess.loaduserbooklist(user);
        DBaccess.close();
        Log.v(TAG,"list is loaded" + Integer.toString(userbooklist.size()));
        return userbooklist;
    }
}
