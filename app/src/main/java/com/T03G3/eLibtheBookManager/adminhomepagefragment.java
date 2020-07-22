package com.T03G3.eLibtheBookManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

//qh - this links to all the other admin actions
public class adminhomepagefragment extends Fragment {
    private static final String TAG = "AdminHome";
    Button verify;
    Button ban;
    Button reviews;
    Button unbanbutton;
    Button viewreports;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        final View view = inflater.inflate(R.layout.adminhomepage,container,false);

        //qh - button to go and verify uploaded books
        verify = view.findViewById(R.id.verifyoption);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    verfiybooksfragment nextFragment = new verfiybooksfragment();
                    MainActivity.addFragment(nextFragment,getActivity(),"Verify Books");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can verify books!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //qh - button to go to ban users
        ban = view.findViewById(R.id.deleteuser);
        ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    banusersfragment nextFragment = new banusersfragment();
                    MainActivity.addFragment(nextFragment,getActivity(),"Ban Users");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can ban users!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //qh - button to go and remove reviews
        reviews = view.findViewById(R.id.removereview);
        reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    RemoveReviewsBookFragment nextFragment = new RemoveReviewsBookFragment();
                    MainActivity.addFragment(nextFragment,getActivity(),"Ban Users");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can remove reviews!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //qh - button to go and unban banned users
        unbanbutton = view.findViewById(R.id.unbanusers);
        unbanbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    unbanusersfragment nextFragment = new unbanusersfragment();
                    MainActivity.addFragment(nextFragment,getActivity(),"Ban Users");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can remove reviews!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //qh - button to go and view all reports made
        viewreports = view.findViewById(R.id.viewreports);
        viewreports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String role = MainActivity.loggedinuser.getAdmin();
                if (role.equals("Admin")){
                    //qh - make sure users are admins
                    viewreportsfragment nextFragment = new viewreportsfragment();
                    MainActivity.addFragment(nextFragment,getActivity(),"Ban Users");
                }
                Log.v(TAG,role);
                if (role.equals("User")){
                    Toast.makeText(getContext(), "Only admins can view reports!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

}
