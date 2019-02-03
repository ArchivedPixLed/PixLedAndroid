package com.pixled.pixledandroid.deviceGroup.mainActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.pixled.pixledserver.core.group.DeviceGroup;

import java.util.List;

public class GroupPagerAdapter extends FragmentStatePagerAdapter {
    /*
    Class used to generate the room pages from the rooms list.
     */
    private List<DeviceGroup> groups;

    public GroupPagerAdapter(FragmentManager fm, List<DeviceGroup> groups) {
        super(fm);
        this.groups = groups;
    }

    @Override
    public Fragment getItem(int position) {
        DeviceGroup deviceGroup = groups.get(position);

        Bundle args = new Bundle();
        args.putInt("groupId", deviceGroup.getId());
        args.putString("groupName", deviceGroup.getName());
        args.putString("groupStatus", deviceGroup.getDeviceGroupState().getToggleState().toString());
        Fragment fragment = new GroupViewFragment();
        fragment.setArguments(args);
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
}
