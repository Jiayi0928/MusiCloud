package edu.neu.madcourse.musicapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

public class RviewAdapter extends RecyclerView.Adapter<RviewAdapter.ViewHolder> {
    private ArrayList<Song> searchResultsList;
    private ItemClickListener itemClickListener;
    private static MediaPlayer mp;

    public RviewAdapter(ArrayList<Song> searchResultsList){
        this.searchResultsList = searchResultsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song curSong = searchResultsList.get(position);
        holder.artist_name.setText(curSong.getArtist());
        holder.title_name.setText(curSong.getTitle());
        Picasso.get().load(curSong.getImg()).into(holder.album_img);

        holder.player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PlayAudioManager.playAudio(view.getContext(), curSong.getPreview());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }



    @Override
    public int getItemCount() {
        return searchResultsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title_name, artist_name;
        ImageView album_img,player;

        ViewHolder(View view){
            super(view);
            title_name = view.findViewById(R.id.item_title);
            artist_name = view.findViewById(R.id.item_artist);
            album_img = view.findViewById(R.id.albumImg);
            player = view.findViewById(R.id.player);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(itemClickListener != null) itemClickListener.onItemClick(view,getAdapterPosition());

        }
    }



    public interface ItemClickListener{
        void onItemClick(View view, int position);
    }
}
