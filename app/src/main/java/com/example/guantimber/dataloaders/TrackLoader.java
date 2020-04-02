package com.example.guantimber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.example.guantimber.data.SongTrack;

import java.util.ArrayList;

public class TrackLoader {

    public static Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

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

        Cursor cursor = getTrackCursor(context);

        Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
        String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
        String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));

        String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
        Long artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID));
        Long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));

        return null;
    }


    private static Cursor getTrackCursor(Context context){
        StringBuilder builder = new StringBuilder();
        builder.append("title !=''");
        builder.append(" AND "+IS_MUSIC+"=1");
        Cursor cursor = context.getContentResolver().query(MEDIA_URI,COLUMNS,builder.toString(),null,null);
        return cursor;
    }
}

