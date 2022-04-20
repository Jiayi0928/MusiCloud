package edu.neu.madcourse.musicloud;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RviewAdapter extends RecyclerView.Adapter<RviewAdapter.ViewHolder> {
    private ArrayList<Song> searchResultsList;
    private ItemClickListener itemClickListener;

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


        holder.title_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                intent.putExtra("title",curSong.getTitle());
                intent.putExtra("artist",curSong.getArtist());
                intent.putExtra("img",curSong.getImg());
                intent.putExtra("preview",curSong.getPreview());
                intent.putExtra("track_uri",curSong.getTrack_uri());
                view.getContext().startActivity(intent);
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
