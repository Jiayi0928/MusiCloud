package edu.neu.madcourse.musicloud.comments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import edu.neu.madcourse.musicloud.PostActivity;
import edu.neu.madcourse.musicloud.R;

public class RecyclerViewHolder extends RecyclerView.ViewHolder {
    public ImageView commentUserImg;
    public TextView commentUsername;
    public TextView commentContent;
    public TextView commentTime;
    public TextView commentLikes;
    private Drawable likeIcon;

    public RecyclerViewHolder(@NonNull View itemView) {
        super(itemView);

        likeIcon = ContextCompat.getDrawable(PostActivity.getContext(), R.drawable.comment_icons8_heart_24).mutate();

        commentUserImg = itemView.findViewById(R.id.commentUserImg);
        commentUsername = itemView.findViewById(R.id.commentUsername);
        commentContent = itemView.findViewById(R.id.commentContent);
        commentTime = itemView.findViewById(R.id.commentTime);
        commentLikes = itemView.findViewById(R.id.commentLikesCnt);

        commentLikes.setCompoundDrawablesWithIntrinsicBounds(null, likeIcon, null, null);
    }

}
