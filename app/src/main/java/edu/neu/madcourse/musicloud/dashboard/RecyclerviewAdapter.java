package edu.neu.madcourse.musicloud.dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import edu.neu.madcourse.musicloud.PostActivity;
import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.Song;
import edu.neu.madcourse.musicloud.User;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerViewHolder>{
    private ArrayList<Song> postsArrayList;
    private User currentUser;

    public RecyclerviewAdapter(ArrayList<Song> postsArrayList, User currentUser){
        this.postsArrayList = postsArrayList;
        this.currentUser = currentUser;
    }
    @SuppressLint("NotifyDataSetChanged")
    public ArrayList<Song> setItems(ArrayList<Song> arrayList){
        if(postsArrayList!=null){
            this.postsArrayList = arrayList;
            notifyDataSetChanged();
        }
        return this.postsArrayList;
    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_list,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Song post = postsArrayList.get(position);
        holder.setId(post.getTrack_uri());
        Log.e("title",post.getTitle());
        holder.postsTitle.setText(post.getTitle());

        holder.subContent.setText(post.getArtist());
//        Glide.with(holder.imageView.getContext()).load(post.getImage()).into(holder.imageView);
//        Log.d("ImageUrl",post.getImage());
        Log.e("img",post.getImg());
        Picasso.get().load(post.getImg()).into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), PostActivity.class);
                intent.putExtra("title",post.getTitle());
                intent.putExtra("artist",post.getArtist());
                intent.putExtra("img",post.getImg());
                intent.putExtra("preview",post.getPreview());
                intent.putExtra("track_uri",post.getTrack_uri());
                Log.e("imageUri3",String.valueOf(currentUser.getProfileImage()));
                intent.putExtra("currentUser", currentUser);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postsArrayList.size();
    }
}
