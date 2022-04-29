package edu.neu.madcourse.musicloud.dashboard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class LikesFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager rLayoutManager;
    private RecyclerviewAdapter recyclerViewAdapter;
    private ArrayList<Song> postsArrayList = new ArrayList<>();
    private ArrayList<String> likesArrayList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private DatabaseReference likesReference;
    private DatabaseReference postDatabase;
    private User user;
    private Loading loading;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loading = new Loading(LikesFragment.this);
        // Inflate the layout for this fragment
        user = DashBoardActivity.currentUser;
        Log.e("Username:",user.getUsername());
        String username = user.getUsername();
        //set up database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        postDatabase = databaseReference.child("posts");
        likesReference = databaseReference.child("users").child(username).child("likes");

        view =inflater.inflate(R.layout.fragment_likes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        LoadingTaskB loadingTaskB = new LoadingTaskB();
        loadingTaskB.execute();
        rLayoutManager = new LinearLayoutManager(getActivity());
//        recyclerView.setLayoutManager(rLayoutManager);
        recyclerView.setHasFixedSize(false);
        recyclerViewAdapter = new RecyclerviewAdapter(postsArrayList,user);
//        recyclerView.setAdapter(recyclerViewAdapter);
//        recyclerView.setLayoutManager(new GridLayoutManager(view.getContext(),2));

        return view;
    }

    private void getLikesId(){
        likesReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    String postId = dataSnapshot.getKey();
                    Log.e("postId",dataSnapshot.getKey());
                    likesArrayList.add(postId);
                }
                addPosts(likesArrayList,postsArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void addPosts(ArrayList<String> likesArrayList, ArrayList<Song>postsArrayList) {
        if (likesArrayList != null && likesArrayList.size() != 0) {
            for (String id : likesArrayList) {
                getSong(id, new ListCallBack() {
                    @Override
                    public void songCallBack(String title, String artist, String img, String preview, String track_uri) {
                        postsArrayList.add(new Song(title,artist,img,preview,id));
                        recyclerViewAdapter.setItems(postsArrayList);

                    }
                });
            }
        }
    }
    private void getSong(String id, ListCallBack callBack){
        postDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Song song = snapshot.child(id).getValue(Song.class);
                callBack.songCallBack(song.getTitle(),song.getArtist(),song.getImg(),song.getPreview(),song.getTrack_uri());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private class LoadingTaskB extends AsyncTask<String, Integer,String> {
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
            getLikesId();
            return "nice" ;
        }
}
}