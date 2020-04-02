package com.example.guantimber.data;

public class SongTrack {

    Long id ;
    String data ;
    String title ;
    String artist;
    Long artist_id ;
    Long album_id ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(Long artist_id) {
        this.artist_id = artist_id;
    }

    public Long getAlbum_id() {
        return album_id;
    }

    public void setAlbum_id(Long album_id) {
        this.album_id = album_id;
    }



    public SongTrack(){
    }

    public SongTrack(Long id, String data, String title,String artist,Long artist_id,Long album_id){
        this.id= id;
        this.data=data;
        this.title=title;
        this.artist= artist;
        this.artist_id = artist_id;
        this.album_id = album_id;
    }
}
