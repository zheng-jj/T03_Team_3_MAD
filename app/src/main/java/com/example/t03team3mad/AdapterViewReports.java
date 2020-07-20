package com.example.t03team3mad;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Author;
import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.Reports;
import com.example.t03team3mad.model.SearchClass;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

//qh- adapter for view reports
public class AdapterViewReports extends RecyclerView.Adapter<AdapterViewReports.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    private static final String TAG = "AdapterViewReports";
    private OnViewReportsListener mOnViewReportsListener;
    private Context context;
    List<Reports> newreportlist;
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Reports");


    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView reportedby;
        TextView reportedperson;
        TextView reason;

        OnViewReportsListener onViewReportsListener;
        ViewHolder(View itemView, OnViewReportsListener onViewReportsListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.viewreportscardview);
            reportedby = (TextView)itemView.findViewById(R.id.reportedby);
            reportedperson = (TextView)itemView.findViewById(R.id.reportedperson);
            reason = (TextView)itemView.findViewById(R.id.reportcontent);
            this.onViewReportsListener = onViewReportsListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onViewReportsListener.onViewReportsClick(getAdapterPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnViewReportsListener {
        void onViewReportsClick(int position) throws InterruptedException;
    }

    public AdapterViewReports(List<Reports> reportlist, OnViewReportsListener onViewReportsListener, Context context) {
        this.mOnViewReportsListener = onViewReportsListener;
        this.newreportlist = reportlist;
        this.context = context;
    }
    @Override
    public AdapterViewReports.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.viewreportscardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnViewReportsListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterViewReports.ViewHolder viewHolder, int position) {
        viewHolder.reportedby.setText("Reported by : "+newreportlist.get(position).getUname());
        viewHolder.reportedperson.setText("Person Reported : "+newreportlist.get(position).getAname());
        viewHolder.reason.setText("Reason : "+newreportlist.get(position).getReason());

    }
    @Override
    public int getItemCount() {
        return newreportlist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }



}
