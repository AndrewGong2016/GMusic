package com.example.guantimber.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.guantimber.R;
import com.example.guantimber.data.SongTrack;
import com.example.guantimber.dataloaders.TrackLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class SongFragment extends Fragment {


    private RecyclerView mRecycleView;
    private SongAdapter mSongAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.song_fragment,container,false);

        mRecycleView = rootView.findViewById(R.id.recycler_view);
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

        }

        @Override
        public int getItemCount() {
            return songTracks.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            ImageView artWrok;
            TextView trackName;
            public ViewHolder(View view){
                super(view);

            }
        }


    }

}
