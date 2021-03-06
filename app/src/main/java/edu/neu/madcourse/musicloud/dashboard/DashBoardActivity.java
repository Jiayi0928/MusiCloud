package edu.neu.madcourse.musicloud.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import edu.neu.madcourse.musicloud.Home;
import edu.neu.madcourse.musicloud.HomeScreenActivity;
import edu.neu.madcourse.musicloud.LoginActivity;
import edu.neu.madcourse.musicloud.PostActivity;
import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.User;

public class DashBoardActivity extends AppCompatActivity {

    private ImageView avatar;
    private Button shake;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewAdapter viewAdapter;
    private FragmentContainerView fragmentContainerView;
    private  final String[] tabName = {"Comments","Likes"};
    private final Integer CONTENT_ID=1;
    private ShakeActivity shakeActivity;
    private TextView name;
    protected static User currentUser;
    private RelativeLayout relativeLayout;

    private DatabaseReference databaseReference;
    private DatabaseReference userDatabase;
    private Toolbar navBarLayout;
    private ImageView navBarHome;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        shake=(Button) findViewById(R.id.shaky);
        relativeLayout= findViewById(R.id.dashboardHead);
        name = relativeLayout.findViewById(R.id.nickName);


        navBarLayout = (Toolbar) findViewById(R.id.navbar2);
        navBarLayout.inflateMenu(R.menu.menu_res);
        navBarLayout.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_logout) {
               startActivity(new Intent(this,LoginActivity.class));

                return true;
            }
            return false;
        });



        navBarHome = findViewById(R.id.navMenu);
        navBarHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashBoardActivity.this, HomeScreenActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

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

        databaseReference = FirebaseDatabase.getInstance().getReference();
        userDatabase = databaseReference.child("users").child(currentUser.getUsername()).child("profileImage");
        avatar = (ImageView) findViewById(R.id.navUserAvatar);
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("img4",snapshot.getValue(String.class));
                Glide.with(getApplicationContext()).load(snapshot.getValue(String.class)).into(avatar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),GetAvatar.class);

                intent.putExtra("currentUser",currentUser);
                startActivity(intent);
            }

        });

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;
        RelativeLayout dashboardHead = (RelativeLayout) findViewById(R.id.dashboardHead);
        int dashboard_height = dashboardHead.getHeight();
        int toolbar_height = navBarLayout.getHeight();
        int tab_height = tabLayout.getHeight();
        int height = screenHeight-dashboard_height-tab_height-toolbar_height;




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