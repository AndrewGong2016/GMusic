package com.example.guantimber.dataloaders;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.guantimber.data.PlaylistData;
import com.example.guantimber.datastore.PlaylistDataBaseHelper;
import com.example.guantimber.service.MusicPlaybackService;

import java.util.ArrayList;
import java.util.List;

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
        String packageName = MediaStore.Audio.PlaylistsColumns.OWNER_PACKAGE_NAME;



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
        } else {
            Log.d(TAG, "getAllPlaylist: no playlist for " + context.getPackageName());
        }
        if (cursor != null) cursor.close();
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


    // get audio_playlist map, this will Retrive all audio-playlist ids' maps stored
    public static void getPlaylists(Context context){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        String[] columes = new String[]{
                PlaylistDataBaseHelper.COLUMES.ID,
                PlaylistDataBaseHelper.COLUMES.AUDIO_ID,
                PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID
        };

        Cursor cursor = sqLiteDatabase.query(PlaylistDataBaseHelper.TABLE_NAME,columes,
                null,null,null,null,null);
        if (cursor == null) return;
        while (cursor.moveToNext()){
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[0]));
            long audio_id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[1]));
            long playlist_id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[2]));
            Log.d(TAG, "getPlaylist: id = "+id+",audio = " + audio_id + ", playlist id " + playlist_id);
        }
        sqLiteDatabase.close();
    }

    /**
     * Insert an audio-playlist row, this will CREATE a new row in the database
      * @param context
     * @param audioId
     * @param playListId
     * @return  the inserted row id in database;
     */
    public static long addTrackIntoPlaylist(Context context, long audioId, long playListId){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(PlaylistDataBaseHelper.COLUMES.AUDIO_ID,audioId);
        values.put(PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID,playListId);
        long rowId = sqLiteDatabase.insert(PlaylistDataBaseHelper.TABLE_NAME,null,values);
        if ( rowId > 0 ) {
            //notify playlist change using broadcast
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            broadcastManager.sendBroadcast(new Intent(PlaylistDataBaseHelper.PLAYLISTCHANGE_INTENT));
            Log.d(TAG, "addTrack2Playlist: insert success!!");
        }
        sqLiteDatabase.close();
        return rowId;
    }

    private static void getStorageVolumes(Context context){
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> list = storageManager.getStorageVolumes();
        for (StorageVolume volume:list){
            String volumeId = volume.getUuid();
            String state = volume.getState();
            Log.d(TAG, "getStorageVolumes: volume id = " + volumeId + ",state = "+ state);
        }
    }


    /**
     *This will DELETE all audio-playlist map with the specific ids in the database ,think it twice before calling this.
     */
    public static void removeTrackfromPlaylist(Context context,long audioId, long playlistId){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        //1 query if the map existed
        String[] columes = new String[]{
                PlaylistDataBaseHelper.COLUMES.ID,
                PlaylistDataBaseHelper.COLUMES.AUDIO_ID,
                PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID
        };
        StringBuilder builder = new StringBuilder();
        builder.append(PlaylistDataBaseHelper.COLUMES.AUDIO_ID + "="+ audioId);
        builder.append(" AND " + PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID + "=" + playlistId);
        Cursor cursor = sqLiteDatabase.query(PlaylistDataBaseHelper.TABLE_NAME,columes,builder.toString(),
                null,null,null,null);
        if (cursor != null && cursor.moveToFirst()){
            Log.d(TAG, "deleteTrackfromPlaylist: get count:"+ cursor.getCount());
            do{
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[0]));
                long audio_id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[1]));
                long playlist_id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[1]));
                Log.d(TAG, "deleteTrackfromPlaylist: id =" + id);
            }while (cursor.moveToNext());
        } else {
            Log.d(TAG, "deleteTrackfromPlaylist: Not Found audio id : "+ audioId + " in playlist with id = " +playlistId);
        }
        if(cursor != null) cursor.close();

        //2 delete the audio-playlist map
        int deletedRows = sqLiteDatabase.delete(PlaylistDataBaseHelper.TABLE_NAME,builder.toString(),null);
        Log.d(TAG, "deleteTrackfromPlaylist: deleted "+deletedRows +" rows.");

        sqLiteDatabase.close();
    }

    public static void onStorageStateChanged(Context context) {
        Log.d(TAG, "onStorageStateChanged: ");
        getStorageVolumes(context);

    }
}
