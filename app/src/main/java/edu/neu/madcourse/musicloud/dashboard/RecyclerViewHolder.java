package edu.neu.madcourse.musicloud.dashboard;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.musicloud.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder{
    public ImageView imageView;
    public TextView postsTitle;
    public TextView subContent;
    public String id;
//    public Button playButton,pauseButton;
//    public MediaPlayer mediaPlayer;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imageView = itemView.findViewById(R.id.songImg);
        this.postsTitle = itemView.findViewById(R.id.songTitle);
        this.subContent = itemView.findViewById(R.id.songArtist);
//        this.playButton = itemView.findViewById(R.id.playButton);
//        this.pauseButton = itemView.findViewById(R.id.pauseButton);


    }

    public void setId(String id) {
        this.id = id;
    }
}
