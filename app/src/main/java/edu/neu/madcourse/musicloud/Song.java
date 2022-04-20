package edu.neu.madcourse.musicloud;


public class Song {
    private String title;
    private String artist;
    private String img;
    private String track_uri;
    private String preview;
    public Song(String title,String artist,String img,String preview, String track_uri){
        this.title = title;
        this.artist = artist;
        this.img = img;
        this.preview = preview;
        this.track_uri = track_uri;
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
        return preview;
    }

    public String getTrack_uri(){ return track_uri;}
}
