package com.example.t03team3mad;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.NLB;
import com.example.t03team3mad.model.buyBook;

import java.util.ArrayList;
import java.util.List;

public class AdapterBuyBook extends RecyclerView.Adapter<AdapterBuyBook.ViewHolder> {
    private static final String TAG = "buyBook";
    List<buyBook> mitemlist = new ArrayList<buyBook>() {
    };

    private Context context;

    //qh - implemented clicking
    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView condition;
        Button url;
        TextView price;
        TextView vendor;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.nlbcardview);
            //jj-get the widget id and assign them to local variable
            condition = (TextView) itemView.findViewById(R.id.itemno);
            url = (Button) itemView.findViewById(R.id.available);
            price = (TextView) itemView.findViewById(R.id.status);
            vendor = (TextView) itemView.findViewById(R.id.library);
        }
    }

    public AdapterBuyBook(List<buyBook> itemlist, Context context) {

        this.mitemlist = itemlist;
        this.context = context;
    }

    @Override
    public AdapterBuyBook.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.buyonlinecardview, parent, false);
        AdapterBuyBook.ViewHolder viewHolder = new AdapterBuyBook.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterBuyBook.ViewHolder viewHolder, final int position) {

        //jj-loads data into the viewholder from the buy book object
        viewHolder.price.setText("Price: "+mitemlist.get(position).getPrice()+" "+mitemlist.get(position).getCurrency()+" (Excluding shipping)");
        String condition = mitemlist.get(position).getCondition();
        String todisplay = condition.substring(0, 1).toUpperCase() + condition.substring(1);
        viewHolder.condition.setText("Condition: "+todisplay);
        viewHolder.vendor.setText("Vendor: "+mitemlist.get(position).getVendorName());
        //jj-sets on click listener which redirects users to the site to buy the book from
        viewHolder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mitemlist.get(position).getURL()));
                v.getContext().startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mitemlist.size();
    }

    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}