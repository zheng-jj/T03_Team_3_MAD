package com.example.t03team3mad;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.t03team3mad.model.Book;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class HomeFragment extends Fragment implements AdapterGenreInHomeFragment.OnClickListener {
    Bitmap bitmap;
    private static final String TAG = "HomeFragment";
    List<Book> newbooklist;
    ArrayList<String> GenreList=new ArrayList<>();
    ArrayList<String> Ran5ToDisplay=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //qh saves all images
        saveimagesmethods();

        //Chris - genre recycler view
        View view = inflater.inflate(R.layout.fragment_home,container,false);
        RecyclerView Genre=(RecyclerView)view.findViewById(R.id.genrelistrecyclerview);
        LinearLayoutManager genrelayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        Genre.setLayoutManager(genrelayout);
        LoadRandom5Genre();
        AdapterGenreInHomeFragment adapterGenreInHomeFragment =
                new AdapterGenreInHomeFragment(this.getContext(),Ran5ToDisplay,this);
        Genre.setAdapter(adapterGenreInHomeFragment);


        final List<Book> booklist=loadAllBooks();
        //load main popularbooks recyclerview
        RecyclerView popularbooks = (RecyclerView)view.findViewById(R.id.popularbookrecyclerview);

        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        popularbooks.setLayoutManager(llm);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter  = new AdapterBookMain(booklist,this.getContext());
        //jj- set the recyclerview object to its adapter
        popularbooks.setAdapter(bookadapter);



        //IMPORTANT: THIS IS HOW TO USE THE API CREATED BOOKS
        AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute("9780980200447");
        final ArrayList<Book> booklist2=new ArrayList<>();
        try {
            Book temp = tasktogetbook.get();
            if(temp!=null) {
                Log.v(TAG, "Book created = " + temp.getBooktitle());
                Log.v(TAG, "Book isbn = " + temp.getIsbn());
                Log.v(TAG, "Book about = " + temp.getBookabout());
                Log.v(TAG, "Book date = " + temp.getPdate());
                Log.v(TAG, "Book genre = " + temp.getBookgenre());
                Log.v(TAG, "Book author = " + temp.getBookauthor());
                booklist2.add(temp);
            };
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //load recommended books recyclerview
        //do the same for another recycler view recommendedbooks
        RecyclerView recommended = (RecyclerView)view.findViewById(R.id.recommendbookrecyclerview);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recommended.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter2  = new AdapterBookMain(booklist2, this.getContext());
        //jj- set the recyclerview object to its adapter
        recommended.setAdapter(bookadapter2);

        return view;
    }

    //Chris - load all books into a listnewbooklist
    public List<Book> loadAllBooks()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        List<Book> booklist = DBaccess.loadallbooklist();
        DBaccess.close();
        return booklist;
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


    //qh - method to save the image to internal storage
    public void saveimagestointernalstorage(String filename) {
        try {
            int id = getActivity().getApplicationContext().getResources().getIdentifier(filename, "drawable", getActivity().getApplicationContext().getPackageName());
            Drawable drawable = getResources().getDrawable(id);
            bitmap = ((BitmapDrawable) drawable).getBitmap();
            ContextWrapper cw = new ContextWrapper(getActivity().getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            File file = new File(directory, filename + ".jpg");
            if (!file.exists()) {
                Log.d("path", file.toString());
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();
                } catch (java.io.IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
        }

    }

    //qh - calls the methods to save images into internal storage
    public void saveimagesmethods(){
        saveimagestointernalstorage("book9780060562533");
        saveimagestointernalstorage("book9780312351649");
        saveimagestointernalstorage("book0380802325");
        saveimagestointernalstorage("book9780060091293");
        saveimagestointernalstorage("book0380802317");
        saveimagestointernalstorage("book9780312351663");
        saveimagestointernalstorage("book0380811065");
        saveimagestointernalstorage("book9780380781225");
        saveimagestointernalstorage("user1");
        saveimagestointernalstorage("user2");
        saveimagestointernalstorage("user3");
        saveimagestointernalstorage("author1");
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
}
