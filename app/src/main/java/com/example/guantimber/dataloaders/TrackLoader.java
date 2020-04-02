package com.example.guantimber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.guantimber.data.SongTrack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TrackLoader {

    public static Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public static String TAG = "TrackLoader";

    public static String[] COLUMNS = new String[]{
            MediaStore.Audio.AudioColumns._ID,          // 1 : id in media store
            MediaStore.Audio.AudioColumns.DATA,         // 2 : path in media store
            MediaStore.Audio.AudioColumns.TITLE,        // 3 : title in media store
            MediaStore.Audio.AudioColumns.ARTIST,       // 4 : artist of a track
            MediaStore.Audio.AudioColumns.ARTIST_ID,    // 5 : artist id in media store
            MediaStore.Audio.AudioColumns.ALBUM_ID,     // 6 : album of a track
            MediaStore.Audio.AudioColumns.MIME_TYPE
    };
    public static String IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC;

    public static ArrayList<SongTrack> getAllSongs(Context context){
        Cursor cursor = getAllTracks(context);
        if (cursor!= null && cursor.moveToNext()){
            ArrayList<SongTrack> songTracks = new ArrayList<SongTrack>();
            do {
                SongTrack song = new SongTrack();
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
                Long artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID));
                Long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
                song.setId(id);
                song.setData(data);
                song.setTitle(title);
                song.setArtist(artist);
                song.setArtist_id(artist_id);
                song.setAlbum_id(album_id);
                songTracks.add(song);
            }while (cursor.moveToNext());

            Log.d(TAG, context.getPackageName()+"getAllSongs: size = "+ songTracks.size()+".");
            StringBuilder builder = new StringBuilder();
            for (SongTrack songTrack : songTracks) {
                builder.append(","+songTrack.getTitle());
            }
            Log.d(TAG, "getAllSongs: "+ builder.toString());
            return songTracks;
        }
        Log.d(TAG, context.getPackageName()+" are tring to get All Songs from "+ MEDIA_URI.toString() + " and nothing returned");
        return null;
    }

    private static Cursor getAllTracks(Context context){
        StringBuilder builder = new StringBuilder();
        builder.append("title !=''");
        builder.append(" AND "+IS_MUSIC+"=1");
        Cursor cursor = context.getContentResolver().query(MEDIA_URI,COLUMNS,builder.toString(),null,null);
        return cursor;
    }

    public static Uri getAlbumArtUri(long id){
        Uri uri= Uri.withAppendedPath(MEDIA_URI,id + "/albumart");
        return uri;
    }
}

