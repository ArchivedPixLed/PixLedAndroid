package com.example.paulbreugnot.lightroom.room;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

public class RoomPagerAdapter extends FragmentStatePagerAdapter {

    private List<Room> rooms;

    public RoomPagerAdapter(FragmentManager fm, List<Room> rooms) {
        super(fm);
        this.rooms = rooms;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        Room room = rooms.get(position);
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
