package edu.neu.madcourse.musicapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;

public class Search_Results_Activity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    public static ArrayList<Song> songList = new ArrayList<>();
    private RviewAdapter rviewAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        String keyword_i_need = getIntent().getExtras().getString("keyword I need");
        recyclerView = findViewById(R.id.rvSongs);



        databaseReference = FirebaseDatabase.getInstance().getReference().child("Songs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                songList = new ArrayList<>();
                for(DataSnapshot data : snapshot.getChildren()){
                    String title = data.child("Title").getValue(String.class);
                    String artist = data.child("Artist").getValue(String.class);
                    String img = data.child("Img").getValue(String.class);
                    //Log.d(title,artist);
                    assert title != null;
                    if(title.toLowerCase().equals(keyword_i_need.toLowerCase())){
                        songList.add(new Song(title,artist,img));
                        //Log.d("TITLE found, artist by", artist);
                    }
                    assert artist != null;
                    if(artist.toLowerCase().equals(keyword_i_need.toLowerCase())){
                        songList.add(new Song(title,artist,img));
                    }
                }
                //System.out.println(String.valueOf(songList));
                createRecyclerView(songList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    private void createRecyclerView(ArrayList<Song> arr){
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        rviewAdapter = new RviewAdapter(arr);
        recyclerView.setAdapter(rviewAdapter);

    }




}