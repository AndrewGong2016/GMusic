package com.example.guantimber.dataloaders;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.guantimber.data.AlbumData;
import com.example.guantimber.data.SongTrack;

import java.util.ArrayList;

public class AlbumLoader {
    public static Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    public static String TAG = "AlbumLoader";

    public static String[] COLUMNS = new String[]{
            "_id",
            MediaStore.Audio.AlbumColumns.ALBUM,
            MediaStore.Audio.AlbumColumns.ARTIST,
            MediaStore.Audio.AlbumColumns.ARTIST_ID,
            MediaStore.Audio.AlbumColumns.NUMBER_OF_SONGS,
    };

    public static String IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC;

    public static ArrayList<AlbumData> getAlbums(Context context){
        Cursor cursor= context.getContentResolver().query(ALBUM_URI,COLUMNS,null,null,null);
        if (cursor!= null && cursor.moveToNext()) {
            ArrayList<AlbumData> albums = new ArrayList<AlbumData>();
            do{
                AlbumData albumData = new AlbumData();
                Long albumId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMNS[0]));
                String albumName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[1]));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[2]));
                Long artistId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMNS[3]));
                int songsCount = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNS[4]));
                albumData.setAlbumId(albumId);
                albumData.setAlbumTitle(albumName);
                albumData.setArtistName(artist);
                albumData.setArtistId(artistId);
                albumData.setSongCount(songsCount);

                albums.add(albumData);
            }while(cursor.moveToNext());

            Log.d(TAG, context.getPackageName()+"Media Store contains "+ albums.size()+" albums.");

            StringBuilder builder = new StringBuilder();
            for (AlbumData albumData : albums) {
                builder.append(","+albumData.getAlbumTitle());
            }
            Log.d(TAG, "Albums : "+ builder.toString());
            return albums;
        }
        Log.d(TAG, context.getPackageName()+" are tring to get Albums from "+ ALBUM_URI.toString() + " and nothing returned");
        return null;
    }

    public static Uri getAlbumArtUri(long albumid){
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"),albumid);
    }
}
