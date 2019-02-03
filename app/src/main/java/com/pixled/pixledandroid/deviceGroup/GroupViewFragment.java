package com.pixled.pixledandroid.deviceGroup;

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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.device.DeviceAdapter;
import com.pixled.pixledandroid.device.DeviceViewHolder;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledserver.core.ToggleState;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.base.DeviceDto;
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
    private int groupId;
    private List<Device> deviceList = new ArrayList<>();

    private DeviceAdapter deviceAdapter;

    private TextView deviceNumberTextView;
    private int deviceNumber = 0;
    private Switch groupSwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.group_page, container, false);

        /*
        Used to show the number of available rooms at the top of the view.
         */
        deviceNumberTextView = rootView.findViewById(R.id.deviceCount);
        deviceNumberTextView.setText("0");

        /*
        The button to switch a whole room.
        How this operation is handled depends on the server side, but each light status will be
        synchronized in any case.
         */
        groupSwitch = rootView.findViewById(R.id.groupSwitch);
        groupSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupService roomService = new Retrofit.Builder()
                        .baseUrl(ServerConfig.ENDPOINT)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build()
                        .create(GroupService.class);

                roomService.switchGroup(groupId).enqueue(new Callback<List<DeviceDto>>() {
                    @Override
                    public void onResponse(Call<List<DeviceDto>> call, Response<List<DeviceDto>> response) {
                        // Synchronize each light status
                        HashMap<Integer, ToggleState> newDeviceStatus = new HashMap<>();
                        for (DeviceDto device : response.body()) {
                            newDeviceStatus.put(device.getId(), device.getState().getToggle());
                        }
                        for (DeviceViewHolder deviceView : deviceAdapter.getDeviceViews()) {
                            Device viewDevice = deviceView.getDevice();
                            viewDevice.getDeviceState().setToggleState(newDeviceStatus.get(viewDevice.getId()));
                            deviceAdapter.notifyItemChanged(deviceView.getAdapterPosition());
                        }
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
        groupSwitch.setChecked(getArguments().getString("groupStatus").equals("ON"));

        // Retrieve the id of the group corresponding to this view
        groupId = getArguments().getInt("groupId");

        // The recycler view (aka a list) in which lights will be displayed
        RecyclerView recyclerView = rootView.findViewById(R.id.deviceList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        deviceAdapter = new DeviceAdapter(deviceList, this, true);
        recyclerView.setAdapter(deviceAdapter);

        // Synchronize index
        ((GroupSelectionActivity) getActivity()).getDeviceAdapterIndex().put(groupId, deviceAdapter);

        // Fetch lights from the server, feeding the recycler view.
        fetchDevices();

        return rootView;
    }

    private void fetchDevices() {
        GroupService buildingService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                // .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().enableDefaultTyping()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        buildingService.listGroupDevices(groupId).enqueue(new Callback<List<DeviceDto>>() {

            @Override
            public void onResponse(Call<List<DeviceDto>> call, Response<List<DeviceDto>> response) {
                List<DeviceDto> list = response.body();

                Log.i("RETROFIT", deviceList.size() + " devices fetched.");
                for (DeviceDto d : list) {
                    Log.i("RETROFIT","Device : " + d.getId());
                    deviceList.add(d.generateDevice());
                    // Update the recycler view
                    deviceAdapter.notifyItemInserted(deviceList.size() - 1);

                    // Update light number
                    deviceNumber++;
                    deviceNumberTextView.setText(String.valueOf(deviceNumber));
                }
            }

            @Override
            public void onFailure(Call<List<DeviceDto>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

    public void updateGroupStatus() {
        /*
        Used when room are switched, to keep room status synchronized.
        For example when all lights are switched off, the room status must be set to off.
        Notice that room status is kept consistent from the server side, this function just
        fetch each time the room status to keep it synchonized.
        */
        GroupService roomService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        roomService.getGroup(groupId).enqueue(new Callback<DeviceGroupDto>() {
            @Override
            public void onResponse(Call<DeviceGroupDto> call, Response<DeviceGroupDto> response) {
                groupSwitch.setChecked(response.body().getState().getToggle() == ToggleState.ON);
            }

            @Override
            public void onFailure(Call<DeviceGroupDto> call, Throwable t) {

            }
        });
    }

    public DeviceAdapter getDeviceAdapter() {
        return deviceAdapter;
    }

}
