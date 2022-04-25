package edu.neu.madcourse.musicloud.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import edu.neu.madcourse.musicloud.Home;
import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.User;

public class DashBoardActivity extends AppCompatActivity {

    private ImageView avatar;
    private Button shake;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewAdapter viewAdapter;
    private FragmentContainerView fragmentContainerView;
    private  final String[] tabName = {"Posts","Likes"};
    private final Integer CONTENT_ID=1;
    private ShakeActivity shakeActivity;
    private TextView name;
    protected static User currentUser;


//    private ArrayList<Posts> postsArrayList;
//    private LinearLayoutManager rLayoutManager;
//    private RecyclerView recyclerView;
//    private RecyclerviewAdapter recyclerViewAdapter;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        shake=(Button) findViewById(R.id.shaky);
        name = (TextView)findViewById(R.id.nickName);

        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getParcelable("currentUser") != null) {
            currentUser = extras.getParcelable("currentUser");
            Log.v("Logged in: ", currentUser.getUsername());
        }
        name.setText(currentUser.getUsername());

        shake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shakeActivity = new Intent(DashBoardActivity.this,ShakeActivity.class);
                startActivity(shakeActivity);


            }
        });
        avatar = (ImageView) findViewById(R.id.navUserAvatar);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RelativeLayout dashboardHead = (RelativeLayout) findViewById(R.id.dashboardHead);
        int dashboard_height = dashboardHead.getHeight();
        int tab_height = tabLayout.getHeight();
        int height = screenHeight-dashboard_height-tab_height;




        //fragmentContainerView=findViewById(R.id.fragmentContainer);
        viewPager2=(ViewPager2)findViewById(R.id.viewPager);
        ViewGroup.LayoutParams params = viewPager2.getLayoutParams();
        params.height=height;
        viewPager2.setLayoutParams(params);
        viewAdapter = new ViewAdapter(this);
        viewPager2.setAdapter(viewAdapter);
        //fragmentContainerView.startViewTransition(viewPager2);

        new TabLayoutMediator(tabLayout, viewPager2, ((tab, position) -> {
            tab.setText(tabName[position]);
        })).attach();








    }

}