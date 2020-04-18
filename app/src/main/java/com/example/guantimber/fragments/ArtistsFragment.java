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

import com.example.guantimber.R;
import com.example.guantimber.data.AlbumData;
import com.example.guantimber.data.ArtistData;
import com.example.guantimber.dataloaders.AlbumLoader;
import com.example.guantimber.dataloaders.ArtistLoader;

import java.util.ArrayList;

public class ArtistsFragment extends Fragment {

    String TAG= "ArtistsFragment";
    private RecyclerView mRecycleView;
    private ArtistsFragment.ArtistAdaper artistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.artists_fragment,container,false);

        mRecycleView = rootView.findViewById(R.id.recycler_view);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));


        new LoadArtist().execute("");

        return rootView;
    }

    private class LoadArtist extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... strings) {
            ArrayList<ArtistData> artists = ArtistLoader.getArtists(getActivity());

            ArtistsFragment.ArtistAdaper artistAdaper= new ArtistsFragment.ArtistAdaper(artists);
            artistAdapter = artistAdaper;

            return "Executed";
        }

        @Override
        protected void onPostExecute(String s) {
            mRecycleView.setAdapter(artistAdapter);
            artistAdapter.notifyDataSetChanged();
            super.onPostExecute(s);
        }
    }

    class ArtistAdaper extends RecyclerView.Adapter<ArtistAdaper.ArtistHolder>{

        private ArrayList<ArtistData> artists;

        ArtistAdaper(ArrayList<ArtistData> artistData){
            this.artists = artistData;
        }

        @NonNull
        @Override
        public ArtistHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_artist_list,null);
            return new ArtistHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistHolder holder, int position) {

            ArtistData artist = artists.get(position);
            holder.artistName.setText(artist.getArtist());
            holder.albumNumber.setText(artist.getNumberOfAlbums()+" album"+ (artist.getNumberOfAlbums()>1? "s ":" "));

        }

        @Override
        public int getItemCount() {
            return artists.size();
        }

        class ArtistHolder extends RecyclerView.ViewHolder{
            private ImageView artistArt;
            private TextView artistName;
            private TextView albumNumber;

            public ArtistHolder(@NonNull View itemView) {
                super(itemView);
                artistArt = itemView.findViewById(R.id.artistArt);
                artistName = itemView.findViewById(R.id.artist_name);
                albumNumber = itemView.findViewById(R.id.number_of_albums);

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
