package com.example.paulbreugnot.lightroom.room;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.paulbreugnot.lightroom.light.LightAdapter;
import com.example.paulbreugnot.lightroom.light.LightViewHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomPagerAdapter extends FragmentStatePagerAdapter {
    /*
    Class used to generate the room pages from the rooms list.
     */
    private List<Room> rooms;

    public RoomPagerAdapter(FragmentManager fm, List<Room> rooms) {
        super(fm);
        this.rooms = rooms;
    }

    @Override
    public Fragment getItem(int position) {
        Room room = rooms.get(position);

        Bundle args = new Bundle();
        args.putLong("roomId", room.getId());
        args.putString("roomName", room.getName());
        args.putString("roomStatus", room.getStatus().toString());
        Fragment fragment = new RoomViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return rooms.size();
    }
}
