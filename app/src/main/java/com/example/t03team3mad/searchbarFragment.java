package com.example.t03team3mad;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class searchbarFragment extends Fragment implements AdapterSearch.OnSearchListener {
    AdapterSearch searchadapter;
    private static final String TAG = "searchbarFragment";
    ListView listviewitem;
    List<SearchClass> searchClassList = new ArrayList<SearchClass>();
    private ProgressBar progressbar;
    private CollectionReference userscollection = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");

    String isbn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_searchbar,container,false);
        SearchView searchbar = view.findViewById(R.id.searchView);
        progressbar = view.findViewById(R.id.progressBar2);
        progressbar.setMax(100);
        //qh - simple search
        searchbar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                doMySearch(query, view);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
        }
    //qh - takes the search query and displays them (can search for author , books and users)
    public void doMySearch(String query, View view){
        progressbar.setVisibility(View.VISIBLE);
        if (searchClassList.isEmpty()){

        }
        else{
            searchClassList.clear();
        }
        System.out.println(query);
        //qh - opens database to create the object lists
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        databaseAccess.open();
        List<User> searchUser = databaseAccess.searchUser(query);
        databaseAccess.close();
        //IMPORTANT: THIS IS HOW TO USE THE API CREATED BOOKS
        AsyncTask<String, Void, List<SearchClass>> searchapiforbooks = new APIaccessSearchBookTitle().execute(query);
        try {
            searchClassList = searchapiforbooks.get();
            if(searchClassList!=null) {
                Log.v(TAG, "Added Searches");
            };
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        //List<SearchClass> toremove = new ArrayList<>();
        //qh- to prevent books from showing up that dont have information at all
        //for (SearchClass i : searchClassList){
            //Book newbook = null;
            //AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(i.getId());
            //try {
               // newbook = tasktogetbook.get();
                //if(newbook!=null) {
                   // Log.v(TAG, "Book created = " + newbook.getBooktitle());
                   // Log.v(TAG, "Book isbn = " + newbook.getIsbn());
                    //Log.v(TAG, "Book about = " + newbook.getBookabout());
                   // Log.v(TAG, "Book date = " + newbook.getPdate());
                   // Log.v(TAG, "Book genre = " + newbook.getBookgenre());
                   // Log.v(TAG, "Book author = " + newbook.getBookauthor());
               // }
               // else{
                  //  toremove.add(i);
               // }
            //} catch (ExecutionException | InterruptedException e) {
             //   e.printStackTrace();
            //}

       // }
       // searchClassList.removeAll(toremove);


        //for (Author var : searchauthor)
        //{
           // System.out.println(var.getAuthorname());
           // SearchClass searchClass = new SearchClass(var.getAuthorname(),var.getAuthorabout(),"Author",String.valueOf(var.getAuthorid()));
           // searchClassList.add(searchClass);
        //}
        //for (User var : searchUser)
        //{
            //System.out.println(var.getUsername());
           // SearchClass searchClass = new SearchClass(var.getUsername(),var.getUserabout(),"User",String.valueOf(var.getUseridu()));
           // searchClassList.add(searchClass);
        //}
        Log.d(TAG, "Running User Code Now");
        loadbookssearch();
        //qh - puts all the results into recycler view to display
        RecyclerView searchresults = (RecyclerView)view.findViewById(R.id.searchrecycler);
        LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
        searchresults.setLayoutManager(searchlayout);
        searchadapter  = new AdapterSearch(searchClassList,this, this.getContext());
        //qh - gets users
        getuser(query);
        searchresults.setAdapter(searchadapter);
    }

    //qh - when clicking search item
    @Override
    public void onSearchClick(int position) throws InterruptedException {
        SearchClass currentsearchobject = searchClassList.get(position);

        //qh -- if object clicked is a book
        //qh - added jjs code to transfer info
        Book currentbook = null;
        if (currentsearchobject.getSearchClass().equals("Book")){
            AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(currentsearchobject.getId());
            Log.v(TAG,"searched ="+currentsearchobject.getId());
            final ArrayList<Book> booklist2=new ArrayList<>();
            try {
                currentbook = tasktogetbook.get();
                if(currentbook!=null) {
                    Log.v(TAG, "Book created = " + currentbook.getBooktitle());
                    Log.v(TAG, "Book isbn = " + currentbook.getIsbn());
                    Log.v(TAG, "Book about = " + currentbook.getBookabout());
                    Log.v(TAG, "Book date = " + currentbook.getPdate());
                    Log.v(TAG, "Book genre = " + currentbook.getBookgenre());
                    Log.v(TAG, "Book author = " + currentbook.getBookauthor());
                };
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bookinfoFragment nextFrag= new bookinfoFragment();
            Bundle bundle = new Bundle();
            //qh - bring the object into book
            bundle.putParcelable("currentbook", currentbook);  // Key, value
            nextFrag.setArguments(bundle);
            MainActivity.addFragment(nextFrag,getActivity(),"findThisFragment"+currentsearchobject.getSearchName());

        }

        //qh -- if object clicked is user
        if (currentsearchobject.getSearchClass().equals("User")){
            //qh - gets user
            AsyncTask<String,Void, User> getbook = new FireStoreAccess.AccessUser2().execute(currentsearchobject.getId());
            User currentuser = null;
            try {
                currentuser = getbook.get();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            Thread.sleep(1000);
            fragment_user nextFrag= new fragment_user();
            Bundle bundle = new Bundle();
            bundle.putParcelable("searchuser", currentuser);
            nextFrag.setArguments(bundle);//jj-updated the way we add fragments into the view
            MainActivity.addFragment(nextFrag,getActivity(),"UserFragment");
        }
    }



    //qh - get user from firebase
    public void getuser (final String query){
        Log.d(TAG, "getuser method");
        userscollection.whereEqualTo("name",query).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                    Log.d(TAG, "getuser232232323 dsds");
                    String id =   i.getId();
                    Log.d(TAG, id);
                    String username = i.getString("name");
                    String userabout = i.getString("desc");
                    SearchClass new1 = new SearchClass(username,userabout,"User",id);
                    searchClassList.add(new1);
                    Log.d(TAG, "getuser232232323 method");
                }
            }
        });

        searchadapter.notifyDataSetChanged();
        return;
    }
    public void loadbookssearch() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(SearchClass searchclass : searchClassList){
                        if (searchclass.getSearchClass().equals("Book")){
                            Log.v(TAG,"Bookloop="+searchclass.getId());
                            for(DocumentSnapshot doc : data){
                                Log.v(TAG,"Docloop="+doc.getReference().getId());
                                if(doc.getReference().getId().equals(searchclass.getId())){
                                    searchclass.setimglink(doc.getString("coverurl"));
                                }
                            }
                        }
                    }
                    searchadapter.notifyDataSetChanged();
                }
            }
        });
    }

    public static String getimagesearch(SearchClass searchClass) throws ExecutionException, InterruptedException {
        String filename = "user" + searchClass.getId() +".jpg";
        //jj gets image from firebase and saves to local storage
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
    }







}







