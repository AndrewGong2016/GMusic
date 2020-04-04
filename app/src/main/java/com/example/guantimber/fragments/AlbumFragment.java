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
import com.example.guantimber.data.AlbumData;
import com.example.guantimber.data.SongTrack;
import com.example.guantimber.dataloaders.AlbumLoader;
import com.example.guantimber.dataloaders.TrackLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumFragment extends Fragment {

    private RecyclerView mRecycleView;
    private AlbumAdapter albumAdapter;

    private static String TAG = "AlbumFragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.albums_fragment,container,false);
        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        new LoadAlbums().execute("");
        return rootView;
    }

    private class LoadAlbums extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<AlbumData> albums = AlbumLoader.getAlbums(getActivity());

            AlbumAdapter songAdapter = new AlbumAdapter(albums);
            albumAdapter = songAdapter;

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            mRecycleView.setAdapter(albumAdapter);
            albumAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }
     class  AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumHolder>{

        private List<AlbumData> albums;

        public AlbumAdapter(List<AlbumData> list){
            this.albums = list;
        }
        @NonNull
        @Override
        public AlbumHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album_list,null);
            return new AlbumHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull AlbumHolder holder, int position) {

            AlbumData album= albums.get(position);
            holder.albumName.setText(album.getAlbumTitle());
            holder.artistNames.setText(album.getArtistNames());

            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_empty_music2)
                    .error(R.drawable.ic_empty_music2);
            Glide.with(getContext())
                    .load(AlbumLoader.getAlbumArtUri(album.getAlbumId()))
                    .apply(options)
                    .into(holder.albumArt);
        }

        @Override
        public int getItemCount() {
            return albums.size();
        }

        class AlbumHolder extends RecyclerView.ViewHolder{

            private ImageView albumArt;
            private TextView albumName;
            private TextView artistNames;

            public AlbumHolder(@NonNull View itemView) {
                super(itemView);
                albumArt = itemView.findViewById(R.id.albumArt);
                albumName = itemView.findViewById(R.id.album_name);
                artistNames = itemView.findViewById(R.id.artist_names);

                itemView.setOnClickListener(new View.OnClickListener() {
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
