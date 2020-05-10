package com.example.guantimber.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * This is a Database helper class ,it helps to create the table whick persists audio-playlist data in a SQLite database
 * Auther : andrew.gong
 * Date : 2020.05.04
 */
public class MusicPlaylistStore extends SQLiteOpenHelper {

    //database info
    public static String DATABASE_NAME = "audioPlaylist.db3";
    public static String TABLE_NAME = "audio_playlist";

    //columes in the table
    public static class COLUMES {
        public static String ID = "_id";
        public static String AUDIO_ID = "audio_id";
        public static String PLAYLIST_ID = "playlist_id";
        public static String VOLUME_NAME = "volume_name";
    }

    final String CREATE_TABLE_SQL =
            "create table audio_playlist(_id integer primary " +
                    "key autoincrement , audio_id , playlist_id , volume_name CHAR(50))";

    public MusicPlaylistStore(@Nullable Context context, @Nullable String name, int version) {
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
