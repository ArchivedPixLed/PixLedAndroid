package com.pixled.pixledandroid.deviceGroup.mainActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.device.DeviceAdapter;
import com.pixled.pixledandroid.device.DeviceViewHolder;
import com.pixled.pixledandroid.deviceGroup.editActivity.EditGroupActivity;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledserver.core.ToggleState;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.base.DeviceDto;
import com.pixled.pixledserver.core.group.DeviceGroup;
import com.pixled.pixledserver.core.group.DeviceGroupDto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GroupViewFragment extends Fragment {
    /*
    This fragment corresponds to a group page.
     */

    private static final String TAG = "GROUP_PAGE";
    // Id of the group
    private int groupId;
    // List of devices that belong to this group
    private DeviceGroup deviceGroup;

    // Adapter used to render device list
    private DeviceAdapter deviceAdapter;

    // Labels and controls (group level)
    private TextView deviceNumberTextView;
    private int deviceNumber = 0;
    private Switch groupSwitch;

    // Group selection activity (used to access indexes)
    private GroupSelectionActivity groupSelectionActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.group_page, container, false);

        // Parent activity
        groupSelectionActivity = (GroupSelectionActivity) getActivity();

        // Retrieve the id of the group corresponding to this view
        groupId = getArguments().getInt("groupId");

        // Retrieve device group from groupSelectionActivity
        deviceGroup = groupSelectionActivity.getDeviceGroupsIndex().get(groupId);

        /*
        Used to show the number of available rooms at the top of the view.
         */
        deviceNumberTextView = rootView.findViewById(R.id.deviceCount);
        updateDeviceNumber();

        /*
        The button to switch a whole room.
        How this operation is handled depends on the server side, but each light status will be
        synchronized in any case.
         */
        groupSwitch = rootView.findViewById(R.id.groupSwitch);
        groupSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupService groupService = new Retrofit.Builder()
                        .baseUrl(ServerConfig.ENDPOINT)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build()
                        .create(GroupService.class);

                groupService.switchGroup(groupId).enqueue(new Callback<List<DeviceDto>>() {
                    @Override
                    public void onResponse(Call<List<DeviceDto>> call, Response<List<DeviceDto>> response) {

                        // The switch ha succeeded, so we switch there to.
                        // Notice that switchGroup() is the same function has used on server side,
                        // from the pixledserver-core library
                        deviceGroup.switchGroup();

                        // Update all the group views potentially concerned
                        for (Device d : deviceGroup.getDevices()) {
                            for (DeviceGroup dg : d.getDeviceGroups()) {
                                GroupViewFragment groupViewFragment = groupSelectionActivity.getViewFragmentIndex().get(dg.getId());
                                // groupViewFragment.getDeviceAdapter().notifyDataSetChanged();
                                if (groupViewFragment != null) {
                                    groupViewFragment.updateDeviceGroupState();
                                }
                            }

                            for (DeviceViewHolder deviceViewHolder : groupSelectionActivity.getDeviceViewsIndex().get(d.getId())) {
                                deviceViewHolder.updateSwitch();
                            }
                        }
                        groupSelectionActivity.getGroupPagerAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<List<DeviceDto>> call, Throwable t) {
                        // Canceled operation
                        Log.i(TAG, "Request failed : ", t);
                        groupSwitch.setChecked(!groupSwitch.isChecked());
                    }
                });
            }
        });

        // Init button status
        updateDeviceGroupState();
//        groupSwitch.setChecked(deviceGroup.getDeviceGroupState().getToggleState() == ToggleState.ON);
//        groupSwitch.setSelected(deviceGroup.getDeviceGroupState().getToggleState() == ToggleState.ON);

        // The recycler view (aka a list) in which lights will be displayed
        RecyclerView recyclerView = rootView.findViewById(R.id.deviceList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        deviceAdapter = new DeviceAdapter(deviceGroup.getDevices(), groupSelectionActivity, true);
        recyclerView.setAdapter(deviceAdapter);

        // Synchronize index
        groupSelectionActivity.getViewFragmentIndex().put(groupId, this);

        // Set up edit button
        rootView.findViewById(R.id.editButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        launchEditGroup();
                    }
                }
        );

        return rootView;
    }

    private void launchEditGroup() {
        Intent intent = new Intent(getActivity(), EditGroupActivity.class);
        intent.putExtra("groupId", groupId);
        startActivity(intent);
    }

    public void updateDeviceNumber() {
        deviceNumber = deviceGroup.getDevices().size();
        deviceNumberTextView.setText(String.valueOf(deviceNumber));
    }

    public void updateDeviceGroupState() {
        groupSwitch.setChecked(deviceGroup.getDeviceGroupState().getToggleState() == ToggleState.ON);
    }

    public DeviceAdapter getDeviceAdapter() {
        return deviceAdapter;
    }

    public GroupSelectionActivity getGroupSelectionActivity() {
        return groupSelectionActivity;
    }
}
