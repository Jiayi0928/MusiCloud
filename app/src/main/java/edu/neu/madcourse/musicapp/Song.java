package edu.neu.madcourse.musicapp;

import android.net.Uri;

public class Song {
    private String title;
    private String artist;
    private String img;
    private String url;
    public Song(String title,String artist,String img, String url){
        this.title = title;
        this.artist = artist;
        this.img = img;
        this.url = url;
    }

    public Song(){}

    @Override
    public String toString() {
        return "SONG{" +
                "title:'" + title + '\'' +
                ", artist:'" + artist + '\'' +
                ", img url:'" + img + '\''+
                '}';
    }

    public String getArtist() {
        return artist;
    }

    public String getImg() {
        return img;
    }

    public String getTitle() {
        return title;
    }

    public String getPreview() {
        return url;
    }
}
