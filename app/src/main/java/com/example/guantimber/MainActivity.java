package com.example.guantimber;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.guantimber.fragments.MainFragment;
import com.example.guantimber.fragments.SongFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements SongFragment.SongClickCallback {

    String TAG = "guantbb";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: permission granted");
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
            return;
        }
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= 21){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        MainFragment fragment = new MainFragment();

        transaction.replace(R.id.fragment_container,fragment).commitAllowingStateLoss();

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.inflateHeaderView(R.layout.nav_view);



        

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
    }
}
