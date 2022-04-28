package edu.neu.madcourse.musicloud.comments;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

import edu.neu.madcourse.musicloud.R;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<Comment> commentsList;

    public RecyclerViewAdapter(ArrayList<Comment> commentsList) {
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_card, parent, false);
        return new RecyclerViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        Comment comment = commentsList.get(position);
        PrettyTime p = new PrettyTime();
        Log.e("imageUri",String.valueOf(comment.getUser().getProfileImage()));
        Picasso.get().load(comment.getUser().getProfileImage()).into(holder.commentUserImg);
//        Glide.with(holder.itemView).load(comment.getUser().getProfileImage()).into(holder.commentUserImg);

        holder.commentUsername.setText(comment.getUser().getUsername());
        holder.commentContent.setText(comment.getContent());
        holder.commentTime.setText(p.format(comment.getDate()));
        holder.commentLikes.setText(Integer.toString(comment.getLikeCnt()));
    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

}
