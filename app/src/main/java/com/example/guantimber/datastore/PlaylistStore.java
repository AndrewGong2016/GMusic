package com.example.guantimber.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

public class PlaylistStore {

    public static String TAG = "PlaylistStore";
    public static String DATABASE_NAME = PlaylistDataBaseHelper.DATABASE_NAME;
    public static String AUDIO_PLAYLIST_TABLENAME = PlaylistDataBaseHelper.AUDIO_PLAYLIST_MAPTABLE_NAME;

    public static String PLAYLIST_TABLENAME = PlaylistDataBaseHelper.PLAYLIST_TABLE_NAME;

    public static String PLAYLISTCHANGE_INTENT = "com.unisoc.music.playlist.changed";


    public static long createPlaylist(Context context,String playlistName){

        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(PlaylistDataBaseHelper.PLAYLIST_COLUMES.NAME,playlistName + System.currentTimeMillis());
        values.put(PlaylistDataBaseHelper.PLAYLIST_COLUMES.DISPLAY_NAME,playlistName);

        long insertedId = sqLiteDatabase.insert(PLAYLIST_TABLENAME,null,values);
        return insertedId;
    }

    // query all audio_playlist map, this will Retrive all audio-playlist ids' maps stored
    public static void queryPlaylists(Context context){
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
     *
     * @param context
     * @param audioIds ids of audios
     * @param playListId
     * @return inserted row number
     */
    public static int addAudiosIntoPlaylist(Context context, long[] audioIds, long playListId){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();


        //begin a transaction to insert large data
        sqLiteDatabase.beginTransaction();
        long starttime = System.currentTimeMillis();
        Log.d(TAG, "addTracksIntoPlaylist: start time: "+ starttime);
        int rowCound = 0;
        for (int i = 0; i<audioIds.length;i++){
            ContentValues values = new ContentValues();
            values.put(PlaylistDataBaseHelper.COLUMES.AUDIO_ID,audioIds[i]);
            values.put(PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID,playListId);
            long rowId = sqLiteDatabase.insert(PlaylistDataBaseHelper.TABLE_NAME,null,values);
            if (rowId > 0 ) rowCound ++;
        }
        //end the transaction
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        long endtime =System.currentTimeMillis();
        Log.d(TAG, "addTracksIntoPlaylist: end time: "+ endtime );
        Log.d(TAG,rowCound +" rows inserted and it takes "+ (endtime-starttime)/1000 + "s.");

        if ( rowCound > 0 ) {
            //notify playlist change using broadcast
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(context);
            broadcastManager.sendBroadcast(new Intent(PlaylistDataBaseHelper.PLAYLISTCHANGE_INTENT));
            Log.d(TAG, "addTracksIntoPlaylist: insert success!!");
        }

        return rowCound;
    }



    /**
     * Insert an audio-playlist row, this will CREATE a new row in the database
     * @param context
     * @param audioId
     * @param playListId
     * @return  the inserted row id in database;
     */
    public static long addAudioIntoPlaylist(Context context, long audioId, long playListId){
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
    public static Cursor getAudiosInPlaylist(Context context,long playlistId,String orderBy){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        //get all audios
        long[] ids = getAudiosInPlaylist(context,playlistId);

        StringBuilder where = new StringBuilder();
        where.append(MediaStore.Audio.Media._ID + " IN (");
        for (int i = 0; i < ids.length; i++) {
            where.append(ids[i]);
            if (i < ids.length - 1) {
                where.append(",");
            }
        }
        where.append(")");


        Log.d(TAG, "getAudiosInPlaylist: fromats " + where);
        //query ids above
//        SELECT * FROM COMPANY WHERE AGE IN ( 25, 27 );

        String[] cols = new String[]{
                MediaStore.Audio.AudioColumns._ID,
                MediaStore.Audio.AudioColumns.ALBUM_ID};
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,cols,where.toString(),null,orderBy);

        return cursor;
    }

    public static long[] getAudiosInPlaylist(Context context,long playlistId){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        long[] ids = null;
        //1 query if the map existed
        String[] columes = new String[]{
                PlaylistDataBaseHelper.COLUMES.ID,
                PlaylistDataBaseHelper.COLUMES.AUDIO_ID,
                PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID
        };

        StringBuilder builder = new StringBuilder();
        builder.append(PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID + "=" + playlistId);
        Cursor cursor = sqLiteDatabase.query(PlaylistDataBaseHelper.TABLE_NAME,columes,builder.toString(),
                null,null,null,null);

        if (cursor != null && cursor.moveToFirst()) {
            long[] audios = new long[cursor.getCount()];
            Log.d(TAG, "getTracInPlaylist: get count:"+ audios.length);
            int index = 0;
            do {
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[0]));
                long audio_id = cursor.getLong(cursor.getColumnIndexOrThrow(columes[1]));
                audios[index++] = audio_id;
            } while (cursor.moveToNext());
            ids = audios;
        } else {
            Log.d(TAG, "getTracInPlaylist: Not Found any audio in the playlist with id = " +playlistId);
        }
        if (cursor != null) cursor.close();
        return ids;
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
        Cursor cursor = sqLiteDatabase.query(
                PlaylistDataBaseHelper.TABLE_NAME,columes,builder.toString(),
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
            Log.d(TAG, "deleteTrackfromPlaylist: Not Found audio id : " + audioId
                    + " in playlist:" +playlistId);
        }
        if(cursor != null) cursor.close();

        //2 delete the audio-playlist map
        int deletedRows = sqLiteDatabase.delete(PlaylistDataBaseHelper.TABLE_NAME,builder.toString(),null);
        Log.d(TAG, "deleteTrackfromPlaylist: deleted "+deletedRows +" rows.");

        sqLiteDatabase.close();
    }

    /**
     * Remove all audios in the playlist
     * @param context
     * @param audioIds
     * @param playlistId
     */
    public static int removeTrackfromPlaylist(Context context,long[] audioIds, long playlistId){
        PlaylistDataBaseHelper dataBaseHelper = new PlaylistDataBaseHelper(context,PlaylistDataBaseHelper.DATABASE_NAME,1);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();

        int removedRow = 0;

        StringBuilder where = new StringBuilder();
        where.append(PlaylistDataBaseHelper.COLUMES.AUDIO_ID + " IN (");
        for (int i = 0; i < audioIds.length; i++) {
            where.append(audioIds[i]);
            if (i < audioIds.length - 1) {
                where.append(",");
            }
        }
        where.append(")");
        where.append(" AND " + PlaylistDataBaseHelper.COLUMES.PLAYLIST_ID + "=" + playlistId);

        removedRow = sqLiteDatabase.delete(PlaylistStore.AUDIO_PLAYLIST_TABLENAME,where.toString(),null);
        sqLiteDatabase.close();
        return removedRow;
    }
}
