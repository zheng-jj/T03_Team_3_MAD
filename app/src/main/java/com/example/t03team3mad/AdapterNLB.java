package com.example.t03team3mad;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.NLB;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class AdapterNLB extends RecyclerView.Adapter<AdapterNLB.ViewHolder> {
    private static final String TAG = "AdapterNLB";
    List<NLB> mitemlist = new ArrayList<NLB>() {
    };

    private Context context;

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView itemno;
        TextView avail;
        TextView status;
        TextView library;
        Book book;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.nlbcardview);
            //jj-get the widget id and assign them to local variable
            itemno = (TextView) itemView.findViewById(R.id.itemno);
            avail = (TextView) itemView.findViewById(R.id.available);
            status = (TextView) itemView.findViewById(R.id.status);
            library = (TextView) itemView.findViewById(R.id.library);
        }
    }

    public AdapterNLB(List<NLB> itemlist, Context context) {

        this.mitemlist = itemlist;
        this.context = context;
    }

    @Override
    public AdapterNLB.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.nlbcardview, parent, false);
        AdapterNLB.ViewHolder viewHolder = new AdapterNLB.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterNLB.ViewHolder viewHolder, final int position) {
        try {
            String itemnumber = mitemlist.get(position).getItemNo();
            Boolean availability = mitemlist.get(position).getAvailability();
            String libraryname = mitemlist.get(position).getBranchName();
            String statusstring = mitemlist.get(position).getStatus();


            //!! jj- i swapped text view for library and item no. because it looks better this way


            viewHolder.library.setText("Item Number: "+itemnumber);
            if(availability){
                viewHolder.avail.setText("Available: Yes");
            }
            else {
                viewHolder.avail.setText("Available: No");
            }

            viewHolder.itemno.setText("From: "+libraryname);

            viewHolder.status.setText("Status: "+statusstring);

        } catch (Exception e) {
        }
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}