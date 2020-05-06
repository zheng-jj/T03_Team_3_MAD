//package com.example.t03team3mad;
//
//import android.content.Context;
//import android.widget.Adapter;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import android.view.LayoutInflater;
//import java.util.List;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//public class AdapterBookMain extends RecyclerView.Adapter<AdapterBookMain.ViewHolder>
//{
//    List<Book> mBooklist = new List<Book>(){};
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        public ViewHolder(View itemView) {
//            super(itemView);
//        }
//    }
//    public AdapterBookMain(List<Book> mBooklist) {
//    }
//    @Override
//    public AdapterBookMain.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        Context context = parent.getContext();
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View contactView = inflater.inflate(R.layout.bookdisplaycardview, parent, false);
//        ViewHolder viewHolder = new ViewHolder(contactView);
//        return viewHolder;
//    }
//    @Override
//    public void onBindViewHolder(AdapterBookMain.ViewHolder viewHolder, int position) {
//    }
//    @Override
//    public int getItemCount() {
//        return mBooklist.size();
//    }
//}