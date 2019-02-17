package com.pixled.pixledandroid.deviceGroup.mainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pixled.pixledserver.core.group.DeviceGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GroupPagerAdapter extends FragmentPagerAdapter {
    /*
    Class used to generate the room pages from the rooms list.
     */
    private List<DeviceGroup> groups;
    private HashMap<Integer, GroupViewFragment> fragmentPositionsIndex;

    public GroupPagerAdapter(FragmentManager fm, List<DeviceGroup> groups) {
        super(fm);
        this.groups = groups;
        this.fragmentPositionsIndex = new HashMap<>();
    }

    @Override
    public Fragment getItem(int position) {
        DeviceGroup group = groups.get(position);

        Bundle args = new Bundle();
        args.putInt("groupId", group.getId());
        GroupViewFragment fragment = new GroupViewFragment();
        fragment.setArguments(args);
        fragmentPositionsIndex.put(position, fragment);
        return fragment;
    }

    @Override
    public int getCount() {
        return groups.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return groups.get(position).getName();
    }

    public HashMap<Integer, GroupViewFragment> getFragmentPositionsIndex() {
        return fragmentPositionsIndex;
    }
}
