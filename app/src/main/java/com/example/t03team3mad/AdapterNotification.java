package com.example.t03team3mad;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.t03team3mad.model.Book;
import com.example.t03team3mad.model.NLB;
import com.example.t03team3mad.model.User;
import com.example.t03team3mad.model.notifications;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

public class AdapterNotification extends RecyclerView.Adapter<AdapterNotification.ViewHolder> {
    private static final String TAG = "AdapterNotification";
    List<notifications> mitemlist = new ArrayList<notifications>() {
    };

    private Context context;


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView itemname;
        com.suke.widget.SwitchButton switchButton;

        ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.noticard);
            //jj-get the widget id and assign them to local variable
            itemname = (TextView) itemView.findViewById(R.id.notiname);
            switchButton = itemView.findViewById(R.id.togglebutton);
        }
    }

    public AdapterNotification(List<notifications> itemlist, Context context) {

        this.mitemlist = itemlist;
        this.context = context;
    }

    @Override
    public AdapterNotification.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.noticardview, parent, false);
        AdapterNotification.ViewHolder viewHolder = new AdapterNotification.ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final AdapterNotification.ViewHolder viewHolder, final int position) {
        try {
            String notiname = mitemlist.get(position).getNotiname();
            Boolean bool = mitemlist.get(position).getBool();

            viewHolder.itemname.setText(notiname);
            viewHolder.switchButton.setChecked(bool);

            final SharedPreferences topics = viewHolder.cardView.getContext().getSharedPreferences("topics", Context.MODE_PRIVATE);

            final SharedPreferences unsub = viewHolder.cardView.getContext().getSharedPreferences("Unsub", Context.MODE_PRIVATE);

            viewHolder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                    if(isChecked){
                        topics.edit().putBoolean(mitemlist.get(position).getSharedpreferencename(),true).commit();
                        for(String userid : MainActivity.loggedinuser.getfollowingstring().split(";")){
                            unsub.edit().putBoolean("User"+userid+mitemlist.get(position).getNotitype(),false).commit();
                        }
                    }
                    else{
                        topics.edit().putBoolean(mitemlist.get(position).getSharedpreferencename(),false).commit();
                        for(String userid : MainActivity.loggedinuser.getfollowingstring().split(";")){
                            unsub.edit().putBoolean("User"+userid+mitemlist.get(position).getNotitype(),true).commit();
                        }
                    }
                    Log.v(TAG,mitemlist.get(position).getNotiname()+" bool is "+topics.getBoolean(mitemlist.get(position).getSharedpreferencename(),true));
                }
            });
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