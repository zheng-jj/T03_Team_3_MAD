package com.T03G3.eLibtheBookManager;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.ArrayList;
import java.util.List;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.T03G3.eLibtheBookManager.model.Book;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

//qh - adapter to verify books
public class AdapterVerify extends RecyclerView.Adapter<AdapterVerify.ViewHolder>
{
    List<String> search = new ArrayList<String>(){};
    private static final String TAG = "AdapterSearch";
    private OnVerifyListener mOnVerifyListener;
    private Context context;
    List<Book> newbooklist;
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");


    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView textisbn;
        TextView texttitle;
        TextView textauthor;
        TextView textpdate;
        TextView textabout;
        OnVerifyListener onVerifyListener;
        ViewHolder(View itemView, OnVerifyListener onVerifyListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.verifybookscardview);
            textisbn = (TextView)itemView.findViewById(R.id.verifyisbn);
            texttitle = (TextView)itemView.findViewById(R.id.verifytitle);
            textauthor = (TextView)itemView.findViewById(R.id.verifyauthor);
            textpdate = (TextView)itemView.findViewById(R.id.verifydate);
            textabout = (TextView)itemView.findViewById(R.id.verifyabout);
            this.onVerifyListener = onVerifyListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onVerifyListener.onVerifyClick(getAdapterPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnVerifyListener {
        void onVerifyClick(int position) throws InterruptedException;
    }

    public AdapterVerify(List<Book> bookList, OnVerifyListener onVerifyListener, Context context) {
        this.mOnVerifyListener = onVerifyListener;
        this.newbooklist = bookList;
        this.context = context;
    }
    @Override
    public AdapterVerify.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.verifybookscardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnVerifyListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(AdapterVerify.ViewHolder viewHolder, int position) {
        viewHolder.textisbn.setText("ISBN : "+newbooklist.get(position).getIsbn());
        viewHolder.texttitle.setText("Title : "+newbooklist.get(position).getBooktitle());
        viewHolder.textauthor.setText("Author : "+newbooklist.get(position).getBookauthor());
        viewHolder.textpdate.setText("Publish Date : "+newbooklist.get(position).getPdate());
        viewHolder.textabout.setText("Description : "+newbooklist.get(position).getBookabout());

    }
    @Override
    public int getItemCount() {
        return newbooklist.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}