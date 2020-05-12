package com.example.guantimber.datastore;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 * This is a Database helper class ,it helps to create the table whick persists audio-playlist data in a SQLite database
 * Auther : andrew.gong
 * Date : 2020.05.04
 */
public class PlaylistDataBaseHelper extends SQLiteOpenHelper {

    //database info
    public static String DATABASE_NAME = "audioPlaylist.db3";
    public static String AUDIO_PLAYLIST_MAPTABLE_NAME = "audio_playlists_map";
    public static String TABLE_NAME = AUDIO_PLAYLIST_MAPTABLE_NAME;
    public static String PLAYLIST_TABLE_NAME = "playlists_table";

    public static String PLAYLISTCHANGE_INTENT = "com.unisoc.music.playlist.changed";

    //columes in the table
    public static class COLUMES {
        public static String ID = "_id";
        public static String AUDIO_ID = "audio_id";
        public static String PLAYLIST_ID = "playlist_id";
        public static String VOLUME_NAME = "volume_name";
    }

    //playlist columes
    public static class PLAYLIST_COLUMES {
        public static String ID = "_id";
        public static String NAME = "name";
        public static String DISPLAY_NAME = "display_name";
    }

    final String CREATE_MAP_TABLE_SQL =
            "create table audio_playlists_map(_id integer primary " +
                    "key autoincrement , audio_id , playlist_id , volume_name CHAR(50))";

    final String CREAT_PLAYLIST_TABLE_SQL =
            "create table playlist_table(_id integer primary " +
            "key autoincrement , name CHAR(50), display_name CHAR(50))";

    public PlaylistDataBaseHelper(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d("PlaylistDataBaseHelper", "onCreate: creat tables");
        sqLiteDatabase.execSQL(CREATE_MAP_TABLE_SQL);
        sqLiteDatabase.execSQL(CREAT_PLAYLIST_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


}
