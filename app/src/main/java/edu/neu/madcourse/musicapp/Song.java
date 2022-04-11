package edu.neu.madcourse.musicapp;

public class Song {
    private String title;
    private String artist;
    private String img;
    public Song(String title,String artist,String img){
        this.title = title;
        this.artist = artist;
        this.img = img;
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
}
