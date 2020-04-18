package com.example.guantimber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import com.example.guantimber.data.AlbumData;
import com.example.guantimber.data.PlaylistData;

import java.util.ArrayList;

public class PlaylistLoader {
    public static final String MUSIC_ONLY_SELECTION = MediaStore.Audio.AudioColumns.IS_MUSIC + "=1"
            + " AND " + MediaStore.Audio.AudioColumns.TITLE + " != ''";
    public static Uri ALBUM_URI = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
    public static String TAG = "PlaylistLoader";

    public static String[] COLUMNS = new String[]{
            BaseColumns._ID,
            MediaStore.Audio.PlaylistsColumns.NAME
    };
    public static ArrayList<PlaylistData> getAllPlaylist(Context context){
        ArrayList<PlaylistData> playlistSet = new ArrayList<PlaylistData>();
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                COLUMNS,
                null,null,MediaStore.Audio.Playlists.DEFAULT_SORT_ORDER);
        if (cursor != null && cursor.moveToFirst()) {
            do {

                final long id = cursor.getLong(0);

                final String name = cursor.getString(1);
                final int songCount = getSongCountForPlaylist(context, id);

                final PlaylistData playlist = new PlaylistData();
                playlist.setPlaylistName(name);
                playlist.setNumberOfSongs(songCount);
                playlistSet.add(playlist);

            } while (cursor.moveToNext());
        }else {
            Log.d(TAG, "getAllPlaylist: no playlist for " + context.getPackageName());
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        return playlistSet;
    }

    private static int getSongCountForPlaylist(Context context,long playlistId){
        Cursor c = context.getContentResolver().query(
                MediaStore.Audio.Playlists.Members.getContentUri("external", playlistId),
                new String[]{BaseColumns._ID}, MUSIC_ONLY_SELECTION, null, null);

        if (c != null) {
            int count = 0;
            if (c.moveToFirst()) {
                count = c.getCount();
            }
            c.close();
            c = null;
            return count;
        }
        return 0;
    }
}
