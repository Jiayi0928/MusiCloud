package edu.neu.madcourse.musicloud.comments;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.musicloud.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView commentUserImg;
    public TextView commentUsername;
    public TextView commentContent;
    public TextView commentTime;
    public TextView commentLikes;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        commentUserImg = itemView.findViewById(R.id.commentUserImg);
        commentUsername = itemView.findViewById(R.id.commentUsername);
        commentContent = itemView.findViewById(R.id.commentContent);
        commentTime = itemView.findViewById(R.id.commentTime);
        commentLikes = itemView.findViewById(R.id.commentLikesCnt);
    }

}
