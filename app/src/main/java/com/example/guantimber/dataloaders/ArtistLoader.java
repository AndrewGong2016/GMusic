package com.example.guantimber.dataloaders;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.guantimber.data.ArtistData;

import java.util.ArrayList;

public class ArtistLoader {

    public static Uri ARTIST_URI = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
    public static String TAG = "ArtistLoader";

    public static String[] COLUMNS = new String[]{
            MediaStore.Audio.ArtistColumns.ARTIST,
            MediaStore.Audio.ArtistColumns.NUMBER_OF_ALBUMS,
            MediaStore.Audio.ArtistColumns.NUMBER_OF_TRACKS,
    };

    public static ArrayList<ArtistData> getArtists(Context context) {

        Cursor cursor = context.getContentResolver().query(ARTIST_URI, COLUMNS, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            ArrayList<ArtistData> artists = new ArrayList<ArtistData>();
            do {
                ArtistData artistData = new ArtistData();
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(COLUMNS[0]));
                int numberOfAlbums = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNS[1]));
                int numberOfTracks = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMNS[2]));

                artistData.setArtist(artist);
                artistData.setNumberOfAlbums(numberOfAlbums);
                artistData.setNumberOfSongs(numberOfTracks);
                artists.add(artistData);
            } while (cursor.moveToNext());

            Log.d(TAG, context.getPackageName() + "Media Store contains " + artists.size() + " artists.");

            StringBuilder builder = new StringBuilder();
            for (ArtistData albumData : artists) {
                builder.append("," + albumData.getArtist());
            }
            Log.d(TAG, "Artists : " + builder.toString());

            cursor.close();

            return artists;
        }
        Log.d(TAG, context.getPackageName()+" are tring to get Artists from "+ ARTIST_URI.toString() + " and nothing returned");
        return null;
    }
}
