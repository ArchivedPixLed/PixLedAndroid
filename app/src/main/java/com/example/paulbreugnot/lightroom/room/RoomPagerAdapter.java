package com.example.paulbreugnot.lightroom.room;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class RoomPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_PAGES = 12;

    public RoomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new RoomViewFragment();
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

}
