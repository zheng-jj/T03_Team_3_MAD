package com.example.t03team3mad;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.LayoutInflater;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.t03team3mad.model.Review;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

//qh - adapter for deleting reviews
public class AdapterDeleteReview extends RecyclerView.Adapter<AdapterDeleteReview.ViewHolder>
{
    private static final String TAG = "AdapterDeleteReview";
    private OnReviewListener mOnReviewListener;
    private Context context;
    private CollectionReference mCollectionBook = FirebaseFirestore.getInstance().collection("Book");
    List<Review> newreviewList;


    //qh -- uses onclicklistener to click
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CardView cardView;
        TextView texttitle;
        TextView userid;
        TextView reviewview;
        ImageView reviewimage;
        OnReviewListener onReviewListener;
        ViewHolder(View itemView, OnReviewListener onReviewListener) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.deletereviewscardview);
            texttitle = (TextView)itemView.findViewById(R.id.booktitleview);
            userid = (TextView)itemView.findViewById(R.id.useridview);
            reviewview = (TextView)itemView.findViewById(R.id.reviewview);
            reviewimage = (ImageView)itemView.findViewById(R.id.reviewImage);
            this.onReviewListener = onReviewListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                onReviewListener.onReviewClick(getAdapterPosition());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public interface OnReviewListener {
        void onReviewClick(int position) throws InterruptedException;
    }

    public AdapterDeleteReview(List<Review> reviewList, OnReviewListener onReviewListener, Context context) {
        this.mOnReviewListener = onReviewListener;
        this.context = context;
        this.newreviewList = reviewList;
    }
    @Override
    public AdapterDeleteReview.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.deletereviewscardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(contactView, mOnReviewListener);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(final AdapterDeleteReview.ViewHolder viewHolder, final int position) {
        viewHolder.texttitle.setText("Title : " + newreviewList.get(position).getReviewTitle());
        viewHolder.userid.setText("User ID : " + newreviewList.get(position).getReviewidu());
        viewHolder.reviewview.setText("Review : " + newreviewList.get(position).getReviewtext());

        DocumentReference docRef = mCollectionBook.document(newreviewList.get(position).getReviewisbn());
        //qh - gets the coverurl of the book from firestore and displays the image
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String coverurl = document.getString("coverurl");
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        if(coverurl == null || coverurl == ""){
                            viewHolder.reviewimage.setImageResource(R.drawable.empty);
                            Log.v(TAG,coverurl + "IMG LINK");
                        }
                        else {
                            Log.v(TAG,coverurl + "IMG LINK");
                            Picasso.with(context).load(coverurl).into(viewHolder.reviewimage);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }
    @Override
    public int getItemCount() {
        return newreviewList.size();
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
