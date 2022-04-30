package edu.neu.madcourse.musicloud.dashboard;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.Song;
import edu.neu.madcourse.musicloud.User;
import edu.neu.madcourse.musicloud.comments.Comment;


public class PostsFragment extends Fragment {
    private ArrayList<Song> postsArrayList = new ArrayList<>();
    private LinearLayoutManager rLayoutManager;
    private RelativeLayout dashboard;
    private RelativeLayout dashboardHead;
    private RecyclerView recyclerView;
    private RecyclerviewAdapter recyclerViewAdapter;
    private View view;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference commentDatabase;
    private DatabaseReference postDatabase;
    private User user;
    private DataSnapshot dataSnapshot;
    private ArrayList<Comment> commentArrayList = new ArrayList<>();
    private Button playButton, pauseButton;
    private MediaPlayer mediaPlayer;
    private String songImage;
    private TextView songTitle;
    private TextView songArtist;
    private Loading loading;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loading = new Loading(PostsFragment.this);
        user = DashBoardActivity.currentUser;
        Log.e("Username:",user.getUsername());
        String username = user.getUsername();
        //set up database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        commentDatabase = databaseReference.child("users").child(username).child("comments");
        postDatabase = databaseReference.child("posts");
//        getComment(new ListCallBack() {
//                       @Override
//                       public void ListCallBack(ArrayList<Comment> commentArrayList, ArrayList<Posts> postsArrayList) {
//                           Log.e("Pa siza:",String.valueOf(postsArrayList.size()));
//                           recyclerViewAdapter.setItems(postsArrayList);
//
//
//                       }
//                   });
//        getComment();
//        commentDatabase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                int count = Math.toIntExact(snapshot.getChildrenCount());
//                Log.e("Children count: ", String.valueOf(count));
//                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
//                    Comment comment = dataSnapshot1.getValue(Comment.class);
//                    if (comment == null){
//                        Log.e("Comment:","null");
//                    }
//                    commentArrayList.add(comment);
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        int id = 0;
//        for(Comment comment: commentArrayList){
//            Log.e("Comment:",comment.getContent());
//            postsArrayList.add(new Posts(id,"The Weekend",comment.getContent(), R.drawable.cat));
//            id+=1;
//        }

                // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_posts, container, false);
//        songImage = view.findViewById(R.id.songImg);
//        songTitle = view.findViewById(R.id.songTitle);

//        playButton = view.findViewById(R.id.playButton);
//        pauseButton = view.findViewById(R.id.pauseButton);
        //media player

//        songImage = view.findViewById(R.id.songImg);
//        songTitle = view.findViewById(R.id.songTitle);
//        songArtist=view.findViewById(R.id.songArtist);

        recyclerView = view.findViewById(R.id.recycler_view);
        LoadingTask loadingTask = new LoadingTask();
        loadingTask.execute();
        rLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setHasFixedSize(false);
        recyclerViewAdapter = new RecyclerviewAdapter(postsArrayList,user);
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

//        mediaPlayer = new MediaPlayer();
//        // Set up Media Player
//        mediaPlayer.setAudioAttributes(
//                new AudioAttributes.Builder()
//                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                        .setUsage(AudioAttributes.USAGE_MEDIA)
//                        .build()
//        );
//        playButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mediaPlayer.isPlaying()) {
//                    playButton.setVisibility(View.GONE);
//                    pauseButton.setVisibility(View.VISIBLE);
//                    mediaPlayer.start();
//                } else {
//                    playButton.setVisibility(View.VISIBLE);
//                    pauseButton.setVisibility(View.GONE);
//                    mediaPlayer.pause();
//                }
//            }
//        });
//
//        pauseButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mediaPlayer.isPlaying()) {
//                    playButton.setVisibility(View.GONE);
//                    pauseButton.setVisibility(View.VISIBLE);
//                    mediaPlayer.start();
//                } else {
//                    playButton.setVisibility(View.VISIBLE);
//                    pauseButton.setVisibility(View.GONE);
//                    mediaPlayer.pause();
//                }
//            }
//        });
//
//        WebServiceExecutor webServiceExecutor = new WebServiceExecutor();
//        webServiceExecutor.execute(new FetchTrackTask(getActivity(), mediaPlayer,songImage,songTitle,songArtist));
        return view;

    }
    //according to current user fetch comment id and comment content
    private void getPostId(User user){

    }
    private void getComment(){
        commentDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = Math.toIntExact(snapshot.getChildrenCount());
                Log.e("Children count: ", String.valueOf(count));
                for(DataSnapshot dataSnapshot1:snapshot.getChildren()){
                    Comment comment = dataSnapshot1.getValue(Comment.class);
                    if (comment == null){
                        Log.e("Comment:","null");
                    }
                    commentArrayList.add(comment);
                }
                addPosts(commentArrayList,postsArrayList);
//                callBack.ListCallBack(commentArrayList,postsArrayList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    private void addPosts(ArrayList<Comment> commentArrayList, ArrayList<Song> postsArrayList){
        if(commentArrayList!=null && commentArrayList.size()!=0) {
            for (Comment comment : commentArrayList) {
                Log.e("Comment :", comment.getContent());
                Log.e("Comment ID:", comment.getPostId());
                String id = comment.getPostId();
                getSongInfo(comment.getContent(), id, new itemCallBack() {
                    @Override
                    public void songCallBack(String title, String artist, String img, String preview, String track_uri) {
                        postsArrayList.add(new Song(title,artist,img,preview,track_uri));
                        recyclerViewAdapter.setItems(postsArrayList);
                    }
                });
//                getSongInfo(id,new itemCallBack() {
//                    @Override
//                    public void songCallBack(String title, String image) {
//                        postsArrayList.add(new Song(title,))
//
//
////                        postsArrayList.add(new Posts(comment.getPostId(), title, comment.getContent(), image));
//                        recyclerViewAdapter.setItems(postsArrayList);
//
//
//
//                    }
//                });



            }
        }
    }
    private void getSongInfo(String content,String id,itemCallBack callBack){
        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Song song = snapshot.child(id).getValue(Song.class);
                callBack.songCallBack(song.getTitle(),content,song.getImg(),song.getPreview(),song.getTrack_uri());

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//        Log.e("Comment size2:",String.valueOf(commentArrayList.size()));
//        for(Comment comment: commentArrayList){
//            Log.e("Comment:",comment.getContent());
//            postsArrayList.add(new Posts(0,"The Weekend",comment.getContent(), R.drawable.cat));
//
//        }
//        postsArrayList.add(new Posts(0,"The Weekend","Great!",R.drawable.anon));
//        postsArrayList.add(new Posts(1,"avril","Nice",R.drawable.cat));
//        postsArrayList.add(new Posts(2,"index","Boring",R.drawable.ic_baseline_arrow_back_24));
//        postsArrayList.add(new Posts(3,"plain","Nothing",R.drawable.ic_baseline_menu_24));
    private class LoadingTask extends AsyncTask<String, Integer,String>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        loading.startLoading();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));
        recyclerView.setAdapter(recyclerViewAdapter);
        loading.dismissDialog();
    }


    @Override
    protected String doInBackground(String... strings) {
        getComment();
        return "nice" ;
    }
}
    }






