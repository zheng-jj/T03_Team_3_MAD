package com.T03G3.eLibtheBookManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.T03G3.eLibtheBookManager.model.Book;
import com.T03G3.eLibtheBookManager.model.SearchClass;
import com.T03G3.eLibtheBookManager.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//qh - searches for Users and Books
public class searchbarFragment extends Fragment implements AdapterSearch.OnSearchListener {
    AdapterSearch searchadapter;
    private static final String TAG = "searchbarFragment";
    List<String> urllist = new ArrayList<>();
    ListView listviewitem;
    List<SearchClass> searchClassList = new ArrayList<SearchClass>();
    private ProgressBar progressbar;
    private CollectionReference userscollection = FirebaseFirestore.getInstance().collection("User");
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    String coverurl;
    String isbn;
    //qh - this check is to check whether the book is uploaded or not
    Boolean check = false;

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
                getdata(query,view);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return view;
        }
    //qh - takes the search query and displays them through the adapter
    public void doMySearch(String query, View view){
        progressbar.setVisibility(View.VISIBLE);
        if (searchClassList.isEmpty()){

        }
        else{
            searchClassList.clear();
        }
        System.out.println(query);

        //qh - searches the api for books associated with the query
        AsyncTask<String, Void, List<SearchClass>> searchapiforbooks = new APIaccessSearchBookTitle().execute(query);
        try {
            searchClassList = searchapiforbooks.get();
            if(searchClassList!=null) {
                Log.v(TAG, "Added Searches");
            };
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Running User Code Now");
        //qh - puts all the results into recycler view to display
        RecyclerView searchresults = (RecyclerView)view.findViewById(R.id.searchrecycler);
        LinearLayoutManager searchlayout = new LinearLayoutManager(getActivity());
        searchresults.setLayoutManager(searchlayout);
        searchadapter  = new AdapterSearch(searchClassList,this, this.getContext());
        searchresults.setAdapter(searchadapter);
    }

    //qh - when clicking search item
    @Override
    public void onSearchClick(int position) throws InterruptedException {
        final SearchClass currentsearchobject = searchClassList.get(position);

        //qh -- if object clicked is a book
        //qh - added jjs code to transfer info
        Book currentbook = null;
        if (currentsearchobject.getUploaded().equals("true")){
            check = true;
            //qh - gets the book info from firestore and passes it to bookinfo (uploaded books store info in firestore)
            mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                        if (i.getId().equals(currentsearchobject.getId())){
                            Book currentbook = new Book(i.getString("booktitle"),i.getString("bookauthor"),i.getString("bookabout"),i.getString("bookgenre"),i.getString("bookpdate"),i.getId());
                            Log.d(TAG, "Uploaded Book: " + currentbook.getBooktitle());
                            Log.d(TAG, "Uploaded Book: " + currentbook.getBookauthor());
                            Log.d(TAG, "Uploaded Book: " + currentbook.getBookgenre());
                            Log.d(TAG, "Uploaded Book: " + currentbook.getBookabout());
                            Log.d(TAG, "Uploaded Book: " + currentbook.getIsbn());
                            bookinfoFragment nextFrag= new bookinfoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable("currentbook", currentbook);
                            nextFrag.setArguments(bundle);
                            MainActivity.addFragment(nextFrag,getActivity(),"findThisFragment"+currentsearchobject.getSearchName());
                        }
                    }
                }
            });

        }
        //qh - this is for non uploaded books
        if (currentsearchobject.getSearchClass().equals("Book") && !check){
            Log.d(TAG, "SEE IF UPLOADED" + currentsearchobject.getUploaded());
            Log.d(TAG, "Why is this code running?");
            //qh - it searches the api for the book with the isbn and returns with the info
            AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(currentsearchobject.getId());
            Log.v(TAG,"searched ="+currentsearchobject.getId());
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
            //qh - gets user from firestore
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



    //qh - get user from firebase (also runs doMySearch) (I put all the methods inside each other because the OnSuccessListener isnt running in order)
    public void getdata (final String query, final View view){
        Log.d(TAG, "getuser method");
        userscollection.whereEqualTo("name",query).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                doMySearch(query, view);
                getuploadedbooks(query,view);
                geturl();
                //qh - adding the users into searchlist
                for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                    Log.d(TAG, "getuser232232323 dsds");
                    String id =   i.getId();
                    Log.d(TAG, id);
                    String username = i.getString("name");
                    String userabout = i.getString("desc");
                    SearchClass new1 = new SearchClass(username,userabout,"User",id);
                    new1.setUploaded("false");
                    searchClassList.add(new1);
                    urllist.add(null);
                }
                searchadapter.notifyDataSetChanged();
            }
        });
        return;
    }
    //qh - get uploaded books
    public void getuploadedbooks (final String query, final View view){
        Log.d(TAG, "Running Uploaded Book");
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                    if (i.getBoolean("uploaded")){
                        if (i.getString("booktitle").equals(query)){
                            SearchClass newsearchclass = new SearchClass(i.getString("booktitle"),i.getString("bookabout"),"Book",i.getId());
                            newsearchclass.setUploaded("true");
                            searchClassList.add(newsearchclass);
                        }
                    }
                }
            }
        });
    }

    //qh - get image of the user
    public static String getimagesearch(SearchClass searchClass) throws ExecutionException, InterruptedException {
        String filename = "user" + searchClass.getId() +".jpg";
        //jj gets image from firebase and saves to local storage
        AsyncTask<String, Void, String> task = new FirebaseStorageImages().execute(filename);
        String path = task.get();
        return path;
    }

    //qh - get url (sets the url link to the search class object)
    public void geturl () {
        //qh - get all isbn from firestore Book Collection
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (QueryDocumentSnapshot i : queryDocumentSnapshots) {
                    for (SearchClass x : searchClassList) {
                        if (i.getId().equals(x.getId())){
                            x.setimglink(i.getString("coverurl"));
                        }
                    }
                }
                searchadapter.notifyDataSetChanged();

            }
        });
    }







}







