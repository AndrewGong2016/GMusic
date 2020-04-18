package com.example.guantimber.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.guantimber.IMusicPlaybackService;
import com.example.guantimber.MainActivity;
import com.example.guantimber.R;
import com.example.guantimber.dataloaders.TrackLoader;
import com.example.guantimber.fragments.MainFragment;
import com.example.guantimber.service.MusicPlaybackService;

public class NowPlayingActivity extends AppCompatActivity {

    String TAG = "NowPlayingActivity";

    IMusicPlaybackService mService;
    ServiceBinder binder = new ServiceBinder();

    ImageView mAlbumArt;
    TextView mTitle;
    TextView mSubTitle;
    Toolbar mToolbar;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nowplaying);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        mAlbumArt = findViewById(R.id.albumArt);
        mTitle = findViewById(R.id.tiltle);
        mSubTitle = findViewById(R.id.subtitle);

        bindService();

        try {
            if (mService != null) {
                Log.d(TAG, "onCreate: "+ mService.getTrackName());
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    //start the Service and bind with  it
    private void bindService(){

        Intent intent = new Intent(this, MusicPlaybackService.class);
        startService(intent);
        bindService(intent,binder,0);

    }

    private void unbindService(){
        unbindService(binder);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    class ServiceBinder implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            mService = IMusicPlaybackService.Stub.asInterface(iBinder);

            try {
                Log.d(TAG, "onServiceConnected: track name = "+ mService.getTrackName());

                RequestOptions options = new RequestOptions()
                        .placeholder(R.drawable.ic_empty_music2)
                        .error(R.drawable.ic_empty_music2);
                Glide.with(NowPlayingActivity.this)
                        .load(TrackLoader.getAlbumArtUri(mService.getAudioId()))
                        .apply(options)
                        .into(mAlbumArt);


                Log.d(TAG, "onServiceConnected: " + mService.getTrackName() + ", artist:"+mService.getArtistName());
                mTitle.setText(mService.getTrackName());
                mTitle.setSelected(true);

                mSubTitle.setText(mService.getArtistName());
//                mToolbar.setSubtitle(mService.getArtistName());


            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            mService = null;
        }
    }

}
