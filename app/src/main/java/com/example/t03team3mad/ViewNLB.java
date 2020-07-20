package com.example.t03team3mad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.t03team3mad.model.NLB;
import com.example.t03team3mad.model.Review;
import com.example.t03team3mad.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ViewNLB extends Fragment {
    private static final String TAG = "ViewNLB";
    
    ArrayList<NLB> nlbentries;
    private CollectionReference nlbCollection;
    AdapterNLB adapterNLB;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_view_n_l_b,container,false);


        TextView title = view.findViewById(R.id.booktitle);
        title.setText(ViewToGet.book.getBooktitle());

        nlbentries = new ArrayList<>();
        nlbCollection = FirebaseFirestore.getInstance().collection("Book").document(ViewToGet.book.getIsbn()).collection("avail");
        loadbookNLB();
        //jj - load favourite user books recyclerview
        RecyclerView favouritebooks = (RecyclerView) view.findViewById(R.id.nlbList);
        //jj-layout manager linear layout manager manages the position of the recyclerview items
        LinearLayoutManager llm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        //jj-set the recyclerview's manager to the previously created manager
        favouritebooks.setLayoutManager(llm);

        adapterNLB = new AdapterNLB(nlbentries, this.getContext());
        //jj- set the recyclerview object to its adapter
        favouritebooks.setAdapter(adapterNLB);


        return view;
    }

    //jj- get the details of the book availability in NLB
    public void loadbookNLB(){
        nlbCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> data = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot dss : data) {
                        Map items =  dss.getData();
                        for(Object itemnum : items.keySet()){
                            //jj-creates the new NLB object
                            Map itemdetails = (Map) items.get(itemnum);
                            NLB toadd = new NLB(String.valueOf(itemdetails.get("branch")), itemnum.toString(),Boolean.valueOf((Boolean) itemdetails.get("avail")) ,String.valueOf(itemdetails.get("status")));
                            Log.v(TAG,toadd.getItemNo());
                            Log.v(TAG,toadd.getAvailability().toString());
                            Log.v(TAG,toadd.getBranchName());
                            Log.v(TAG,toadd.getStatus());
                            //jj- adds the new item to the list
                            nlbentries.add(toadd);
                        }
                    }
                }
                //jj- after items are added, update the adapter
                adapterNLB.mitemlist = nlbentries;
                adapterNLB.notifyDataSetChanged();
            }
        });
    }
}
