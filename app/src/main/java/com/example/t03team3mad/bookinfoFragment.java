package com.example.t03team3mad;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.t03team3mad.model.Book;

public class bookinfoFragment extends Fragment {
    private static final String TAG = "bookinfoFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookinfo,container,false);
        TextView title = view.findViewById(R.id.titleview);
        TextView synopsis = view.findViewById(R.id.synopsis);
        TextView releasedate = view.findViewById(R.id.releasedateview);
        TextView genre = view.findViewById(R.id.genreview);
        TextView author= view.findViewById(R.id.authorview);
        ImageView image = view.findViewById(R.id.imageView2);

        Bundle bundle = this.getArguments();
        if (bundle.getParcelable("currentbook") != null) {
            Book receivedbook = bundle.getParcelable("currentbook"); // Key
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());
            System.out.println(receivedbook.getBooktitle());

            title.setText(receivedbook.getBooktitle());
            synopsis.setText(receivedbook.getBookabout());
            releasedate.setText(receivedbook.getPdate());
            genre.setText(receivedbook.getBookgenre());
            author.setText(receivedbook.getBookauthor());

            //QH = SETS IMAGE FROM STRING
            String filename = "book" + receivedbook.getIsbn()+".jpg";
            Bitmap bmImg = BitmapFactory.decodeFile("/data/data/com.example.t03team3mad/app_imageDir/"+filename);
            image.setImageBitmap(bmImg);
        }

        return view;
    }
    //qh -- the book object that is passed here
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}

