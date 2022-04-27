package edu.neu.madcourse.musicloud.dashboard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.User;

public class GetAvatar extends AppCompatActivity {
    private Button camera;
    private Button gallery;
    private static final int IMAGE = 1;
    private Uri imageUri;
    private User currentUser;
    private DatabaseReference databaseReference;
    private DatabaseReference userDatabase;
    private ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_avatar);
        avatar = findViewById(R.id.navUserAvatar);
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getParcelable("currentUser") != null) {
            currentUser = extras.getParcelable("currentUser");
            Log.v("Logged in: ", currentUser.getUsername());
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userDatabase = databaseReference.child("users").child(currentUser.getUsername()).child("profileImage");
        gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Photo"),IMAGE);
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMAGE && resultCode == RESULT_OK){
            imageUri = data.getData();
            currentUser.setProfileImage(String.valueOf(imageUri));
            userDatabase.setValue(String.valueOf(imageUri));
            Glide.with(GetAvatar.this).load(imageUri).into(avatar);
        }
    }
}