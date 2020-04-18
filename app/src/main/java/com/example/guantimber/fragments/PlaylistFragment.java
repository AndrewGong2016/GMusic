package com.example.guantimber.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guantimber.R;
import com.example.guantimber.data.PlaylistData;
import com.example.guantimber.dataloaders.PlaylistLoader;

import java.util.ArrayList;

public class PlaylistFragment extends Fragment {

    private RecyclerView mRecycleView;
    private PlaylistAdapter playlistAdapter;

    private static String TAG = "PlaylistFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.playlists_fragment,container,false);
        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        TextView addPlaylist = rootView.findViewById(R.id.add_playlist_title);
        addPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.add(new NewPlaylistDialog(),null).commitAllowingStateLoss();
            }
        });


        new LoaderPlaylist().execute("");

        return rootView;
    }


    class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder>{

        ArrayList<PlaylistData> playlistDatas;

        PlaylistAdapter(ArrayList<PlaylistData> data){
            this.playlistDatas = data;
        }

        @NonNull
        @Override
        public PlaylistAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.item_playlist,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlaylistAdapter.ViewHolder holder, int position) {
            PlaylistData playlistData = playlistDatas.get(position);
            holder.numberOfSongs.setText(playlistData.getNumberOfSongs()+" songs totally");
            holder.playlistName.setText(playlistData.getPlaylistName());

        }

        @Override
        public int getItemCount() {
            return playlistDatas.size();
        }


        class ViewHolder extends RecyclerView.ViewHolder{
            TextView playlistName;
            TextView numberOfSongs;
            ImageView playlistArt;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                playlistName = itemView.findViewById(R.id.playlist_name);
                playlistArt = itemView.findViewById(R.id.playlistArt);
                numberOfSongs = itemView.findViewById(R.id.number_of_songs);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: ");
                    }
                });
            }
        }
    }


    class LoaderPlaylist extends AsyncTask<String,Void ,String>{

        @Override
        protected String doInBackground(String... strings) {

            ArrayList<PlaylistData> playlistData = PlaylistLoader.getAllPlaylist(getContext());

            playlistAdapter = new PlaylistAdapter(playlistData);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mRecycleView.setAdapter(playlistAdapter);
            playlistAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }


}
