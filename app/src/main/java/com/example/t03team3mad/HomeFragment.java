package com.example.t03team3mad;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.t03team3mad.model.Book;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class HomeFragment extends Fragment implements AdapterBookMain.OnBookMainListener,AdapterGenreInHomeFragment.OnClickListener {
    Bitmap bitmap;
    private static final String TAG = "HomeFragment";
    List<Book> newbooklist;
    ArrayList<String> GenreList=new ArrayList<>();
    ArrayList<String> Ran5ToDisplay=new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //qh saves all images
        saveimagesmethods();

        View view = inflater.inflate(R.layout.fragment_home,container,false);

        RecyclerView Genre=(RecyclerView)view.findViewById(R.id.genrelistrecyclerview);
        LinearLayoutManager genrelayout = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        Genre.setLayoutManager(genrelayout);
        LoadAllGenre();
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
        AdapterBookMain bookadapter  = new AdapterBookMain(loadAllBooks(),this,this.getContext());
        //jj- set the recyclerview object to its adapter
        popularbooks.setAdapter(bookadapter);

        //load recommended books recyclerview
        //do the same for another recycler view recommendedbooks
        RecyclerView recommended = (RecyclerView)view.findViewById(R.id.recommendbookrecyclerview);
        LinearLayoutManager llm2 = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        recommended.setLayoutManager(llm2);
        //jj- get the data needed by the adapter to fill the cardview and put it in the adapter's parameters
        AdapterBookMain bookadapter2  = new AdapterBookMain(loadAllBooks(),this,this.getContext());
        //jj- set the recyclerview object to its adapter
        recommended.setAdapter(bookadapter2);
        return view;
    }
    //load all books into a list
    public List<Book> loadAllBooks()
    {
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        newbooklist = DBaccess.loadallbooklist();
        DBaccess.close();
        return newbooklist;
    }
    //Chris - get all the genre in the database
    public void LoadAllGenre()
    {
        //Chris - the 5 genres to display are different everytime
        Ran5ToDisplay.clear();
        DatabaseAccess DBaccess = DatabaseAccess.getInstance(getActivity().getApplicationContext());
        DBaccess.open();
        newbooklist = DBaccess.loadallbooklist();
        DBaccess.close();
        //Chris - To seperate the long genre string into individual genre
        for(int i = 0; i<newbooklist.size(); i++)
        {
            String GenreListForATable= newbooklist.get(i).getBookgenre();
            String[] ToGetIndividualGenre =GenreListForATable.split(",");
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


    @Override
    public void onBookMainClick(int position) {
        Book currentbook = newbooklist.get(position);

        bookinfoFragment nextFrag= new bookinfoFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("currentbook", currentbook);  // Key, value
        nextFrag.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFrag, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }


    //qh - method to save the image to internal storage
    public void saveimagestointernalstorage(String filename) {

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

    }

    //calls the methods to save images into internal storage
    public void saveimagesmethods(){
        saveimagestointernalstorage("book9780439362139");
        saveimagestointernalstorage("book9780545128285");
        saveimagestointernalstorage("book9780747591061");
        saveimagestointernalstorage("book9781338132083");
        saveimagestointernalstorage("user1");
        saveimagestointernalstorage("user2");
        saveimagestointernalstorage("user3");
        saveimagestointernalstorage("author1");
    }


    //Chris - if user click on the genre on the recycler view
    @Override
    public void OnClick(int postion) {
        String Genre=Ran5ToDisplay.get(postion);
        Log.v(TAG,Genre);
        Bundle bundle = new Bundle();
        bundle.putString("Genre", Genre);  // Key, value
        Book_ByGenre nextFragment = new Book_ByGenre();  //will go the fragment where it will display all the books of that genre
        nextFragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainactivitycontainer, nextFragment, "findThisgenre")
                .addToBackStack(null)
                .commit();
    }
}
