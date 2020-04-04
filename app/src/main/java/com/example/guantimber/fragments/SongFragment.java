package com.example.guantimber.fragments;

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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.guantimber.R;
import com.example.guantimber.data.SongTrack;
import com.example.guantimber.dataloaders.TrackLoader;
import java.util.ArrayList;
import java.util.List;

public class SongFragment extends Fragment {
    private RecyclerView mRecycleView;
    private SongAdapter mSongAdapter;

    private static String TAG = "SongFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_fragment,container,false);
        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        new LoadSongs().execute("");
        return rootView;
    }

    private class LoadSongs extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<SongTrack> songTracks = TrackLoader.getAllSongs(getActivity());
            SongAdapter songAdapter = new SongAdapter(songTracks);
            mSongAdapter = songAdapter;

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            mRecycleView.setAdapter(mSongAdapter);
            mSongAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }

    class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

        private List<SongTrack> songTracks;
        public SongAdapter(List<SongTrack> songTracks){
            this.songTracks= songTracks;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_song_list,null);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //Recycle view 将Adapter 数据绑定给 到ViewHolder 时将回调该方法，用户可以对ViewHolder中的views进行设定
            SongTrack songTrack = songTracks.get(position);
            holder.trackName.setText(songTrack.getTitle());
            holder.artistName.setText(songTrack.getArtist());

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_empty_music2)
                    .error(R.drawable.ic_empty_music2);
            Glide.with(getContext())
                    .load(TrackLoader.getAlbumArtUri(songTrack.getId()))
                    .apply(options)
                    .into(holder.artWrok);
        }

        @Override
        public int getItemCount() {
            return songTracks!=null?songTracks.size():0;
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView artWrok;
            TextView trackName;
            TextView artistName;

            public ViewHolder(View view){
                super(view);
                trackName = view.findViewById(R.id.track_name);
                artistName = view.findViewById(R.id.artist_name);
                artWrok = view.findViewById(R.id.albumArt);

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "onClick: " + getAdapterPosition());
                        getAdapterPosition();
                    }
                });
            }
        }

    }
}
