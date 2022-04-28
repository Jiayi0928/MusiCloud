package edu.neu.madcourse.musicloud;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import edu.neu.madcourse.musicloud.comments.Comment;
import edu.neu.madcourse.musicloud.comments.RecyclerViewAdapter;
import edu.neu.madcourse.musicloud.dashboard.DashBoardActivity;

public class PostActivity extends AppCompatActivity {
    private final static String TAG = "PostActivity";

    // Post info
    private Song currSong;
    private String postId;
    private User currentUser;

    // Recycler view
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager rViewLayoutManager;
    private ArrayList<Comment> commentsList;

    // Database
    private DatabaseReference dbReference;
    private DatabaseReference userDbReference;
    private DatabaseReference postDbReference;
    private DatabaseReference commentsDbReference;
    private ValueEventListener postValueEventListener;
    private ValueEventListener existPostValueEventListener;
    private ChildEventListener commentsChildEventListener;

    // Views
    private RelativeLayout navBarLayout;
    private ImageView navBarUserAvatar;
    private TextView navBarTitle;
    private ImageView navBarHome;
    private ImageView songImage;
    private TextView songTitle;
    private TextView songArtist;
    private TextView songLikes;

    private TextView commentSectionCnt;
    private Button playButton, pauseButton;
    private TextInputLayout commentInputLayout;
    private TextInputEditText commentInput;
    private MediaPlayer mediaPlayer;
    private ImageView menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);



        // Bind views and set on click listeners
        navBarLayout = (RelativeLayout) findViewById(R.id.navbar);
        navBarUserAvatar = navBarLayout.findViewById(R.id.navUserAvatar);


        navBarHome = navBarLayout.findViewById(R.id.navMenu);
        navBarTitle = navBarLayout.findViewById(R.id.navTitle);
        navBarTitle.setText("POST");
        navBarHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, HomeScreenActivity.class);
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
            }
        });

        songImage = findViewById(R.id.songImg);
        songTitle = findViewById(R.id.songTitle);
        songArtist = findViewById(R.id.songArtist);
        songLikes = findViewById(R.id.songLikes);

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);

        commentSectionCnt = findViewById(R.id.commentsCnt);
        commentInputLayout = findViewById(R.id.commentsInputLayout);
        commentInput = findViewById(R.id.commentsInput);






        // Handle posting comments
        commentInputLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postComment();
            }
        });

        // Handle likes
        songLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                like();
            }
        });

        // Retrieve song data and user, set currSong and currUser
//        Bundle extras = getIntent().getExtras();
        Bundle extras = getIntent().getExtras();
//        if (extras != null && extras.getParcelable("postId") != null) {
//            postId = extras.getParcelable("postId");
//
//        }
        if (extras != null) {
            String title = extras.getString("title");
            String artist = extras.getString("artist");
            String img = extras.getString("img");
            String preview = extras.getString("preview");
            String track_uri = extras.getString("track_uri");

            currSong = new Song(title, artist, img, preview, track_uri);
            postId = currSong.getTrack_uri();
            currentUser = extras.getParcelable("currentUser");
            Log.v("Logged in:", currentUser.getUsername());
        }
        // Navigate to user dashboard
        navBarUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostActivity.this, DashBoardActivity.class);
                intent.putExtra("currentUser",currentUser);
                startActivity(intent);
            }
        });

        // Set static data
        songTitle.setText(currSong.getTitle());
        songArtist.setText(currSong.getArtist());
        Glide.with(getApplicationContext()).load(currSong.getImg()).into(songImage);
        Glide.with(getApplicationContext()).load(currentUser.getProfileImage()).into(navBarUserAvatar);

        // Initialize empty comments list and create RecyclerView
        commentsList = new ArrayList<>();
        createRecyclerView();

        // Set up database
        dbReference = FirebaseDatabase.getInstance().getReference(); // points to the root db
        postDbReference = dbReference.child("posts").child(currSong.getTrack_uri()); // points to the post
        commentsDbReference = postDbReference.child("comments"); // points to the comments of the post
        userDbReference = dbReference.child("users").child(currentUser.getUsername()); // points to the current user

        // Initialize post if it does not exist in DB
        existPostValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue() == null) {
                    initPost();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        postDbReference.addListenerForSingleValueEvent(existPostValueEventListener);

        // Retrieve dynamic data of the post and listen to changes from DB
        postValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // This check is to avoid null object exceptions in the case of loading new songs
                if (snapshot.hasChild("commentCnt")) {
                    songLikes.setText(Integer.toString(snapshot.child("likeCnt").getValue(Integer.class)));
                    commentSectionCnt.setText("(" + Integer.toString(snapshot.child("commentCnt").getValue(Integer.class)) + ")");

                    // Set the like (heart) icon appearance based on the current user
                    // If the user has liked this post, the icon should be red
                    // Otherwise, the icon should be black
                    if (snapshot.child("likes").hasChild(currentUser.getUsername())) {
                        Log.v("like", "liked");
                        songLikes.getCompoundDrawablesRelative()[0].setTint(Color.RED);
                    } else {
                        Log.v("Like", "not liked");
                        songLikes.getCompoundDrawablesRelative()[0].setTint(Color.BLACK);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        postDbReference.addValueEventListener(postValueEventListener);

        // Retrieve comments of the post and listen to changes from DB
        commentsChildEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Comment newComment = snapshot.getValue(Comment.class);
                commentsList.add(newComment);
                Collections.sort(commentsList, Collections.reverseOrder());
                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        commentsDbReference.addChildEventListener(commentsChildEventListener);

        // Set up MediaPlayer
        Uri uri = Uri.parse(currSong.getPreview());
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currSong.getPreview().equals("null"))
                    Toast.makeText(getApplicationContext(),
                            "Sorry! No preview available for this song on Spotify.", Toast.LENGTH_SHORT).show();
                else if (!mediaPlayer.isPlaying()) {
                        playButton.setVisibility(View.GONE);
                        pauseButton.setVisibility(View.VISIBLE);
                        mediaPlayer.start();
                    } else {
                        playButton.setVisibility(View.VISIBLE);
                        pauseButton.setVisibility(View.GONE);
                        mediaPlayer.pause();
                    }
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mediaPlayer.isPlaying()) {
                    playButton.setVisibility(View.GONE);
                    pauseButton.setVisibility(View.VISIBLE);
                    mediaPlayer.start();
                } else {
                    playButton.setVisibility(View.VISIBLE);
                    pauseButton.setVisibility(View.GONE);
                    mediaPlayer.pause();
                }
            }
        });
    }



    /**
     * Set up RecyclerView for comments, located at the bottom of the Topic Screen.
     */
    private void createRecyclerView() {
        rViewLayoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.commentsRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(rViewLayoutManager);

        recyclerViewAdapter = new RecyclerViewAdapter(commentsList);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    /**
     * Add the new post into DB, initialize likeCnt and commentCnt to 0.
     */
    private void initPost() {
//        postDbReference.child("title").setValue(currSong.getTitle());
//        postDbReference.child("artist").setValue(currSong.getArtist());
//        postDbReference.child("image").setValue(currSong.getImg());
//        postDbReference.child("preview").setValue(currSong.getPreview());
//        postDbReference.child("track_uri").setValue(currSong.getTrack_uri());
        postDbReference.setValue(currentUser);
        postDbReference.setValue(currSong);
        postDbReference.child("likeCnt").setValue(0);
        postDbReference.child("commentCnt").setValue(0);
    }

    private void postComment() {
        String content = commentInput.getText().toString();
        Date now = new Date();

        Comment comment = new Comment(currentUser, content, now, currSong.getTrack_uri());

        // Add comment to posts (/posts/postId/comments) and to user (/users/userId/comments)
        String commentId = commentsDbReference.push().getKey();
        commentsDbReference.child(commentId).setValue(comment);
        userDbReference.child("comments").child(commentId).setValue(comment);

        // Remove previous comment input
        commentInput.setText("");

        // Update comment count of this post
        postDbReference.child("commentCnt").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                int currCommentCnt = currentData.getValue(Integer.class);
                currentData.setValue(currCommentCnt + 1);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }

    private void like() {
        // Add or remove the user from `likes` based on whether the user has liked this post before
        // Also, add or remove the post from the user's `likes`
        postDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.child("likes").hasChild(currentUser.getUsername())) {
                    // If the current user has not liked this post
                    postDbReference.child("likes").child(currentUser.getUsername()).setValue(currentUser);
                    userDbReference.child("likes").child(postId).setValue(true);
                } else {
                    // If the current user has already liked this post and want to dislike
                    postDbReference.child("likes").child(currentUser.getUsername()).removeValue();
                    userDbReference.child("likes").child(postId).removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // Update the like count of this post
        // And update the appearance of the like icon
        postDbReference.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                int currLikeCnt = currentData.child("likeCnt").getValue(Integer.class);

                // If the user had already liked this post
                if (currentData.child("likes").child(currentUser.getUsername()).getValue() != null) {
                    // Set the heart icon to black
                    songLikes.getCompoundDrawablesRelative()[0].setTint(Color.BLACK);
                    // Decrement the like cnt
                    currentData.child("likeCnt").setValue(currLikeCnt - 1);

                } else {
                    // Set the heart icon to red
                    songLikes.getCompoundDrawablesRelative()[0].setTint(Color.RED);
                    // Increment the like cnt
                    currentData.child("likeCnt").setValue(currLikeCnt + 1);
                }
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });
    }



    @Override
    public void onBackPressed(){
        mediaPlayer.stop();
//        Intent intent = new Intent(PostActivity.this,Home.class);
//        intent;
//        startActivity(intent);
//        startActivity(new Intent(getApplicationContext(),HomeScreenActivity.class).putExtra("currentUser",currentUser));
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.release();
        commentsDbReference.removeEventListener(commentsChildEventListener);
        postDbReference.removeEventListener(postValueEventListener);
        postDbReference.removeEventListener(existPostValueEventListener);
    }

    // Clear the text input field focus when user touch elsewhere
    // Code reference: https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

}