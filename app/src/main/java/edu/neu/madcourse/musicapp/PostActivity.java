package edu.neu.madcourse.musicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

public class PostActivity extends AppCompatActivity {

    Button playButton,pauseButton;
    ImageView cover;
    TextView title_op,artist_op,nav_title;
    String title,artist,img,preview,track_uri;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        cover = findViewById(R.id.albumImg);
        title_op = findViewById(R.id.item_title);
        artist_op = findViewById(R.id.item_artist);
        nav_title =findViewById(R.id.navTitle);
        nav_title.setText("POST");

        title = getIntent().getExtras().getString("title");
        title_op.setText(title);
        artist = getIntent().getExtras().getString("artist");
        artist_op.setText(artist);
        img = getIntent().getExtras().getString("img");
        Picasso.get().load(img).into(cover);

        track_uri = getIntent().getExtras().getString("track_uri");



        preview = getIntent().getExtras().getString("preview");

        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);

        Uri uri = Uri.parse(preview);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(
                new AudioAttributes
                        .Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build());
        try {
            mediaPlayer.setDataSource(this,uri);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }


        playButton.setOnClickListener(new View.OnClickListener() {
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

    @Override
    public void onBackPressed(){
        mediaPlayer.stop();
        this.finish();
    }

}