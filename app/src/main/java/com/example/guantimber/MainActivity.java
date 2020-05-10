package com.example.guantimber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.example.guantimber.activities.NowPlayingActivity;
import com.example.guantimber.fragments.MainFragment;
import com.example.guantimber.fragments.SongFragment;
import com.example.guantimber.service.MusicPlaybackService;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SongFragment.SongClickCallback {

    String TAG = "MainActivity";

    ServiceBinder binder = new ServiceBinder();
    IMusicPlaybackService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setTheme(R.style.MainFaceTheme);


        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: permission granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            return;
        }
        setContentView(R.layout.activity_main);


        //add fragment into the activity
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        MainFragment fragment = new MainFragment();
        transaction.replace(R.id.fragment_container,fragment).commitAllowingStateLoss();

        //init navigation view
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.inflateHeaderView(R.layout.nav_view);


        //bind service
        bindService();
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

    class ServiceBinder implements ServiceConnection{

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, "onServiceConnected: ");
            mService = IMusicPlaybackService.Stub.asInterface(iBinder);
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected: ");
            mService = null;
        }
    }
    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onPostCreate: ");
        super.onPostCreate(savedInstanceState);
    }


//    This callback is called after onResume,so we can setMenu in a Fragment.onCreateView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                Toast.makeText(this,"main navegation",Toast.LENGTH_LONG).show();
                break;
            case R.id.nav_search:
                Toast.makeText(this,"search",Toast.LENGTH_LONG).show();
                break;

        };
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSongClick(View view, long[] list, int position) {

        Log.d(TAG, "onSongClick: " +" position = " + position + " in song list"+Arrays.toString(list));
        Log.d(TAG, "onSongClick: song id = "+ list[position]);

        try {
            if (mService != null ){
                mService.open(list,position,0,0);
                mService.play();
                startActivity(new Intent(this,NowPlayingActivity.class));
            }

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
