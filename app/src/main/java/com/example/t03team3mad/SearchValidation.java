package com.example.t03team3mad;

import android.os.AsyncTask;
import android.util.Log;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.SearchClass;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SearchValidation extends AsyncTask<List<SearchClass>,Void, List<SearchClass>> {
    private static final String TAG = "searchbarFragment";
    @Override
    protected List<SearchClass> doInBackground(List<SearchClass>... lists) {
        List<SearchClass> toremove = new ArrayList<>();
        //qh- to prevent books from showing up that dont have information at all
        for (SearchClass i : lists[0]){
            Book newbook = null;
            AsyncTask<String, Void, Book> tasktogetbook = new APIaccess().execute(i.getId());
            try {
                newbook = tasktogetbook.get();
                if(newbook!=null) {
                    Log.v(TAG, "Book created = " + newbook.getBooktitle());
                    Log.v(TAG, "Book isbn = " + newbook.getIsbn());
                    Log.v(TAG, "Book about = " + newbook.getBookabout());
                    Log.v(TAG, "Book date = " + newbook.getPdate());
                    Log.v(TAG, "Book genre = " + newbook.getBookgenre());
                    Log.v(TAG, "Book author = " + newbook.getBookauthor());
                }
                else{
                    toremove.add(i);
                };
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        lists[0].removeAll(toremove);
        return lists[0];
    }
}
