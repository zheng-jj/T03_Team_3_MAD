package com.T03G3.eLibtheBookManager;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.T03G3.eLibtheBookManager.model.Book;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements AdapterGenreInHomeFragment.OnClickListener {
    Bitmap bitmap;
    private static final String TAG = "HomeFragment";
    List<Book> newbooklist;
    Button uploadbutton;
    Button adminbutton;

    ArrayList<String> GenreList=new ArrayList<>();
    ArrayList<String> Ran5ToDisplay=new ArrayList<>();
    ArrayList<String> isbnlist = new ArrayList<>();

    //jj- these are mainly to load the recyclerviews
    List<Book> booklist=new ArrayList<>();
    List<Book> booklist2=new ArrayList<>();
    List<Book> popularlist=new ArrayList<>();
    List<String> OverallBooklist=new ArrayList<>();
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    AdapterBookMain bookadapter;
    AdapterBookMain bookadapter2;
    Book book ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        book = new Book();
        //Chris - genre recycler view
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        RecyclerView Genre=(RecyclerView)view.findViewById(R.id.genrelistrecyclerview);
        LinearLayoutManager genrelayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        Genre.setLayoutManager(genrelayout);
        LoadRandom5Genre();
        AdapterGenreInHomeFragment adapterGenreInHomeFragment =
                new AdapterGenreInHomeFragment(this.getContext(),Ran5ToDisplay,this);
        Genre.setAdapter(adapterGenreInHomeFragment);


        //load main popularbooks recyclerview
        RecyclerView popularbooks = (RecyclerView)view.findViewById(R.id.popularbookrecyclerview);

        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        popularbooks.setLayoutManager(llm);

        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        bookadapter  = new AdapterBookMain(popularlist,this.getContext());
        //jj- set the recyclerview object to its adapter
        popularbooks.setAdapter(bookadapter);
        getpopularbooks();

        //Chris - get all the isbn from firestore book collection
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(QueryDocumentSnapshot i : queryDocumentSnapshots){
                        OverallBooklist.add(i.getId());

                    }
                }
                //Chris- Randomised the recommendation
                for(int j=0;j<6;j++) {
                    String hi=OverallBooklist.get(new Random().nextInt(OverallBooklist.size()));
                    AsyncTask<String, Void, Book> task = new APIaccess().execute(hi);
                    Log.v(TAG,hi);
                    try {
                        if (task.get() != null) {
                            if(!booklist2.contains(task.get())) {
                                booklist2.add(task.get());
                            }
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }


        });



        //load recommended books recyclerview
        //do the same for another recycler view recommendedbooks
        RecyclerView recommended = (RecyclerView) view.findViewById(R.id.recommendbookrecyclerview);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recommended.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        loadBookurlsreco();
        bookadapter2 = new AdapterBookMain(booklist2, this.getContext());

        //jj- set the recyclerview object to its adapter
        recommended.setAdapter(bookadapter2);
        //qh - go to upload books
        uploadbutton = view.findViewById(R.id.uploadbuttonview);
        uploadbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment_uploadbooks nextFragment = new fragment_uploadbooks();  //will go the fragment where it will display all the books of that genre
                MainActivity.addFragment(nextFragment,getActivity(),"Upload Books");
            }
        });

        adminbutton = view.findViewById(R.id.adminbutton);
        adminbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    adminhomepagefragment nextFragment = new adminhomepagefragment();  //will go the fragment where it will display all the books of that genre
                    MainActivity.addFragment(nextFragment,getActivity(),"Admin Actions");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    adminbutton.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Only admins can access this page!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }


    //Chris - get all the genre in the database
    public void LoadRandom5Genre()
    {
        //Chris - the 5 genres to display are different every time
        Ran5ToDisplay.clear();
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> bookList = DBaccess.loadallbooklist();
        DBaccess.close();
        //Chris - To seperate the long genre string into individual genre
        for(int i = 0; i<bookList.size(); i++)
        {
            String GenreListFormATable= bookList.get(i).getBookgenre();
            String[] ToGetIndividualGenre =GenreListFormATable.split(",");
            for (int a=0;a<ToGetIndividualGenre.length;a++)
            {
                if (!GenreList.contains(ToGetIndividualGenre[a])) {
                    GenreList.add(ToGetIndividualGenre[a]);
                }
            }
        }
        //Chris - Randomised the genre to be display
        for(int x=1;Ran5ToDisplay.size()<6;x++)
        {
            String Genre=GenreList.get(new Random().nextInt(GenreList.size()));
            if (!Ran5ToDisplay.contains(Genre))
                Ran5ToDisplay.add(Genre);
        }

    }



    //Chris - if user click on the genre on the recycler view
    @Override
    public void OnClick(int postion) {
        String Genre=Ran5ToDisplay.get(postion);
        Log.v(TAG,"Going to display books based on "+Genre);
        Bundle bundle = new Bundle();
        bundle.putString("Genre", Genre);  // Key, value
        Book_ByGenre nextFragment = new Book_ByGenre();  //will go the fragment where it will display all the books of that genre
        nextFragment.setArguments(bundle);
        MainActivity.addFragment(nextFragment,getActivity(),"BookByGenre");
    }


    //jj -  loads the url into book objects for recommended books
    public void loadBookurlsreco() {
        mCollectionBook.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for(Book book : booklist2){
                        Log.v(TAG,"Bookloop="+book.getIsbn());
                        for(DocumentSnapshot doc : data){
                            Log.v(TAG,"Docloop="+doc.getReference().getId());
                            if(doc.getReference().getId().equals(book.getIsbn())){
                                book.setimglink(doc.getString("coverurl"));
                            }
                        }
                    }
                    bookadapter2.mBooklist=booklist2;
                    bookadapter2.notifyDataSetChanged();
                }
            }
        });
    }

    public void getpopularbooks(){
        mCollectionBook.whereGreaterThan("viewcount",0).orderBy("viewcount", Query.Direction.DESCENDING).limit(10).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot i:queryDocumentSnapshots){
                    if(!i.getBoolean("uploaded")){
                        String id= i.getId();
                        AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(id);
                        try {
                            book = tasktogetbook.get();



                            if(book!=null) {
                                book.setimglink(i.getString("coverurl"));
                                Log.v(TAG, "Book created = " + book.getBooktitle());
                                Log.v(TAG, "Book isbn = " + book.getIsbn());
                                Log.v(TAG, "Book about = " + book.getBookabout());
                                Log.v(TAG, "Book date = " + book.getPdate());
                                Log.v(TAG, "Book genre = " + book.getBookgenre());
                                Log.v(TAG, "Book author = " + book.getBookauthor());
                                Log.v(TAG, "Book img = " + book.getimglink());
                                popularlist.add(book);
                            };
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Book temp = new Book(i.getString("booktitle"), i.getString("bookauthor"), i.getString("bookabout"), i.getString("bookgenre"), i.getString("bookpdate"), i.getId());
                        temp.setimglink(i.getString("coverurl"));
                        popularlist.add(temp);
                    }

                }
                bookadapter.notifyDataSetChanged();

            }

        });

    }
}
