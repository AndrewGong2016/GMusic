package com.example.guantimber.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.guantimber.R;
import com.google.android.material.tabs.TabLayout;

public class MainFragment extends Fragment {

    String TAG = "guantbb";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "on Fragment CreateView: ");
        View rootView = inflater.inflate(R.layout.fragment_main,container,false);

        //setSupportActionBar before activity created OptionsMenu(onCreateOptionsMenu callback)
        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

//        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
//        actionBar.setDisplayShowHomeEnabled(true);

        ViewPager viewPager = rootView.findViewById(R.id.viewpager);
        FragmentPagerAdapter adapter = new FragmentPagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
            }
            @Override
            public Fragment getItem(int position) {
                return new SongFragment();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return "Guan";
            }
        };
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: ");
        super.onViewCreated(view, savedInstanceState);
    }
}
