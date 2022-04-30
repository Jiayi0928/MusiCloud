package edu.neu.madcourse.musicloud.comments;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;

import edu.neu.madcourse.musicloud.R;
import edu.neu.madcourse.musicloud.User;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewHolder> {
    private ArrayList<Comment> commentsList;
    private User currentUser;

    public RecyclerViewAdapter(ArrayList<Comment> commentsList, User currentUser) {
        this.commentsList = commentsList;
        this.currentUser = currentUser;
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
        Glide.with(holder.itemView).load(comment.getUser().getProfileImage()).into(holder.commentUserImg);

        holder.commentUsername.setText(comment.getUser().getUsername());
        holder.commentContent.setText(comment.getContent());
        holder.commentTime.setText(p.format(comment.getDate()));
        holder.commentLikes.setText(Integer.toString(comment.getLikeCnt()));

        // Set heart icon color
        ArrayList<User> likedUsers = (ArrayList<User>) comment.getLikes();
        holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.BLACK);

        for (int i=0; i<likedUsers.size(); i++) {
            if (likedUsers.get(i).getUsername().equals(currentUser.getUsername())) {
                holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.RED);
                break;
            }
        }


        DatabaseReference dbReference = FirebaseDatabase.getInstance().getReference(); // points to the root db
        DatabaseReference commentDbReference = dbReference.child("posts")
                                                .child(comment.getPostId())
                                                .child("comments")
                                                .child(comment.getCommentId()); // points to the comment


        // Like the comment
        holder.commentLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("Comment", "liked");
                Log.v("current comment id", comment.getCommentId());

                boolean existUser = false;

                for (int i=0; i<likedUsers.size(); i++) {
                    if (likedUsers.get(i).getUsername().equals(currentUser.getUsername())) {
                        existUser = true;
                        commentDbReference.child("likes").child(currentUser.getUsername()).removeValue();
                        holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.BLACK);

                        // Decrement the like cnt
                        commentDbReference.child("likeCnt").setValue(comment.getLikeCnt() - 1);

                        break;
                    }
                }

                if (!existUser) {
                    commentDbReference.child("likes").child(currentUser.getUsername()).setValue(currentUser);
                    holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.RED);
                    // Increment the like cnt
                    commentDbReference.child("likeCnt").setValue(comment.getLikeCnt() + 1);
                }





//                // Add or remove the user from `likes` based on whether the user has liked this comment before
//                commentDbReference.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (!snapshot.child("likes").hasChild(currentUser.getUsername())) {
//                            // If the current user has not liked this post
//                            commentDbReference.child("likes").child(currentUser.getUsername()).setValue(currentUser);
//                        } else {
//                            // If the current user has already liked this post and want to dislike
//                            commentDbReference.child("likes").child(currentUser.getUsername()).removeValue();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
//
//                // Update the like count of this comment
//                // And update the appearance of the like icon
//                commentDbReference.runTransaction(new Transaction.Handler() {
//                    @NonNull
//                    @Override
//                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {
//                        int currLikeCnt = currentData.child("likeCnt").getValue(Integer.class);
//
//                        // If the user had already liked this post
//                        if (currentData.child("likes").child(currentUser.getUsername()).getValue() != null) {
//                            // Set the heart icon to black
////                            new Handler(Looper.getMainLooper()).post(new Runnable(){
////                                @Override
////                                public void run() {
////                                    holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.BLACK);
////                                }
////                            });
//
//                            // Decrement the like cnt
//                            currentData.child("likeCnt").setValue(currLikeCnt - 1);
//
//
//                        } else {
//                            // Set the heart icon to red
////                            new Handler(Looper.getMainLooper()).post(new Runnable(){
////                                @Override
////                                public void run() {
////                                    holder.commentLikes.getCompoundDrawablesRelative()[1].setTint(Color.RED);
////                                }
////                            });
//
//                            // Increment the like cnt
//                            currentData.child("likeCnt").setValue(currLikeCnt + 1);
//                        }
//                        return Transaction.success(currentData);
//                    }
//
//                    @Override
//                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
//
//                    }
//                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }





}
