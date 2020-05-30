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
import com.example.t03team3mad.model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class HomeFragment extends Fragment implements AdapterBookMain.OnBookMainListener {
    Bitmap bitmap;
    private static final String TAG = "HomeFragment";
    List<Book> newbooklist;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //qh saves all images
        saveimagesmethods();

        View view = inflater.inflate(R.layout.fragment_home,container,false);
        Log.v(TAG,"first recycler");
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

        Log.v(TAG,"second recycler");
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
}
