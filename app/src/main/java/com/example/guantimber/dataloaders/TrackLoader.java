package com.example.guantimber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import com.example.guantimber.data.SongTrack;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class TrackLoader {

    public static Uri MEDIA_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public static String TAG = "TrackLoader";

    public static String IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC;


    static {

        ArrayList<SongTrack> songTracks = new ArrayList<SongTrack>();
        long[] audios = null;
        for(long id : audios){

        }

    }

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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    String volume_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.VOLUME_NAME));
                }
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
//        StringBuilder builder = new StringBuilder();
//        builder.append("title !=''");
//        builder.append(" AND "+IS_MUSIC+"=1");
//        Cursor cursor = context.getContentResolver().query(MEDIA_URI,COLUMNS,builder.toString(),null,null);
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                getTrackQueryCols(),
                "title != ''" + " AND is_music=1",
                (String[]) null,
                "title_key");
        return cursor;
    }

    public static String[] getTrackQueryCols() {
        String[] strArr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            strArr = new String[9];
        } else {
            strArr = new String[8];
        }

        strArr[0] = "_data";
        strArr[1] = "_id";
        strArr[2] = "title";
        strArr[3] = "artist";
        strArr[4] = "artist_id";
        strArr[5] = "album_id";
        strArr[6] = "mime_type";
        strArr[7] = "date_added";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            strArr[8] = "volume_name";
        }

        return strArr;
    }

    public static Uri getAlbumArtUri(long id){
        Uri uri= Uri.withAppendedPath(MEDIA_URI,id + "/albumart");
        return uri;
    }

    public static SongTrack getTrackWithId(Context context, Long id){
        StringBuilder builder = new StringBuilder();
        builder.append("title !=''");
        builder.append(" AND "+IS_MUSIC+"=1");
        builder.append(" AND "+MediaStore.Audio.AudioColumns._ID+"="+id);
        Cursor cursor = context.getContentResolver().query(MEDIA_URI,getTrackQueryCols(),builder.toString(),null,null);

        if (cursor!= null && cursor.moveToNext()){
            SongTrack song = new SongTrack();
            Long cursorId = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID));
            String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DATA));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST));
            Long artist_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST_ID));
            Long album_id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID));
            song.setId(cursorId);
            song.setData(data);
            song.setTitle(title);
            song.setArtist(artist);
            song.setArtist_id(artist_id);
            song.setAlbum_id(album_id);

            Log.d(TAG, "getTrackWithId: id = "+id+",and title is :"+ title);

            return song;

        } else {
            Log.d(TAG, "getTrackWithId: id = "+id+",and Not found!");
            return null;
        }



    }
}

