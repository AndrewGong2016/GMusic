package com.example.guantimber.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.guantimber.R;

public class NewPlaylistDialog extends DialogFragment {

    String TAG = "NewPlaylistDialog";
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder  builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater= getActivity().getLayoutInflater();

        View view = layoutInflater.inflate(R.layout.create_playlist,(ViewGroup)null);
        final EditText text = view.findViewById(R.id.playlist_editor);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                Log.d(TAG, "onClick: Ok : "+text.getText());
                createPlaylist(getContext(),text.getText().toString());
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: cancel");
            }
        });
        builder.setView(view);
        return builder.create();
    }

    /**
     * @return  playlist id in MediaStore or -1 if already existed
     */
    private long createPlaylist(final Context context,final String name){
        long playlistId = -1;
        final String[] projection = new String[]{
                MediaStore.Audio.PlaylistsColumns.NAME
        };
        final String selection = MediaStore.Audio.PlaylistsColumns.NAME + " = '" + name + "'";

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                projection,selection,null,null);
        if (cursor.getCount()<=0){
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Audio.PlaylistsColumns.NAME,name);
            Uri uri = context.getContentResolver().insert(MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI,
                    values);
            playlistId = Long.parseLong(uri.getLastPathSegment());
            Log.d(TAG, "createPlaylist: id = "+playlistId);
        } else {
            Log.d(TAG, "createPlaylist: already existed a playlist named"+ cursor.getString(0));
        }


        if (cursor != null){
            cursor.close();
        }
        return playlistId;
    }


}
