package eLib_theBookManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

import eLib_theBookManager.model.Book;

public class ViewToGet extends AppCompatActivity {
    private static final String TAG = "ViewToGet";
    public static Book book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_get_details);
        Log.v(TAG,"started view where to get books");
        Intent intent = getIntent();
        //jj-book is taken from bundle
        Bundle PassedData = intent.getExtras().getBundle("Bundle");
        if(PassedData.getParcelable("book")!=null&&PassedData.getString("User_UID")!=null) {
            book = PassedData.getParcelable("book");
            String uid = PassedData.getParcelable("User_UID");
            String isbn = book.getIsbn();

        }


        //jj-adds fragments to the pager items
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                .add("Buy Online", ViewPrice.class)
                .add("EBook", ViewGEbook.class)
                .add("NLB", ViewNLB.class)
                .create());

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(adapter);

        SmartTabLayout viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
    }
}
