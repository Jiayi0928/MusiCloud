package edu.neu.madcourse.musicloud.comments;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.neu.madcourse.musicloud.User;

public class Comment implements Comparable<Comment> {
    protected User user;
    protected String content;
    protected  Date date;
    protected int likeCnt;
    protected String postId;
    protected String commentId;
    protected Object likes;

    public Comment(User user, String content, Date date, String postId) {
        this.user = user;
        this.content = content;
        this.date = date;
        this.likeCnt = 0;
        this.postId = postId;
    }

    public Comment(User user, String content, Date date, String postId, String commentId) {
        this.user = user;
        this.content = content;
        this.date = date;
        this.likeCnt = 0;
        this.postId = postId;
        this.commentId = commentId;
    }

    public Comment(User user, String content, Date date, String postId, int likeCnt) {
        this.user = user;
        this.content = content;
        this.date = date;
        this.postId = postId;
        this.likeCnt = likeCnt;
    }

    public Comment() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLikeCnt() {
        return likeCnt;
    }

    public void setLikeCnt(int likeCnt) {
        this.likeCnt = likeCnt;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public Object getLikes() {
        return likes;
    }

    public void setLikes(Object likes) {
        this.likes = likes;
    }

    @Override
    public int compareTo(Comment comment) {
        return getDate().compareTo(comment.getDate());
    }
}
