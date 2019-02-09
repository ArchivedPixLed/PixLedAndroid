package com.pixled.pixledandroid.deviceGroup.editActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.device.DeviceService;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupSelectionActivity;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupService;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.base.DeviceDto;
import com.pixled.pixledserver.core.group.DeviceGroupDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class EditGroupActivity extends AppCompatActivity  {

    private int groupId;
    private EditText editName;
    private List<Device> availableDevices;
    private List<Device> inGroupDevices;
    private Button doneButton;

    private DeviceListAdapter availableDevicesAdapter;
    private DeviceListAdapter inGroupDevicesAdapter;

    // DTO of the device group that correspond to this view.
    private DeviceGroupDto deviceGroupDto;

    public EditGroupActivity() {
        availableDevices = new ArrayList<>();
        inGroupDevices = new ArrayList<>();
    }

    @Override
    public void onCreate(Bundle context) {
        super.onCreate(context);

        setContentView(R.layout.edit_group_view);

        editName = findViewById(R.id.editName);
        doneButton = findViewById(R.id.doneButton);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateGroup();
            }
        });

        groupId = getIntent().getExtras().getInt("groupId");

        // The recycler view (aka a list) in which available devices will be displayed
        RecyclerView availableDevicesRecyclerView = findViewById(R.id.available_devices_list);

        availableDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        availableDevicesAdapter = new DeviceListAdapter(availableDevices, DeviceListViewHolder.Mode.AVAILABLE, this);
        availableDevicesRecyclerView.setAdapter(availableDevicesAdapter);

        // The recycler view (aka a list) in which available devices will be displayed
        RecyclerView inGroupDevicesRecyclerView = findViewById(R.id.group_devices_list);

        inGroupDevicesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        inGroupDevicesAdapter = new DeviceListAdapter(inGroupDevices, DeviceListViewHolder.Mode.IN_GROUP, this);
        inGroupDevicesRecyclerView.setAdapter(inGroupDevicesAdapter);


        fetchGroup();

    }

    private void fetchGroup() {
        GroupService groupService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                // .addConverterFactory(JacksonConverterFactory.create(new ObjectMapper().enableDefaultTyping()))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        groupService.getGroup(groupId).enqueue(new Callback<DeviceGroupDto>() {

            @Override
            public void onResponse(Call<DeviceGroupDto> call, Response<DeviceGroupDto> response) {
                deviceGroupDto = response.body();

                Log.i("RETROFIT",  "Device " + String.valueOf(groupId) + " fetched.");
                editName.setText(deviceGroupDto.getName());

                // Once group info has been loaded, fetch ALL devices.
                fetchAvailableDevices();
            }

            @Override
            public void onFailure(Call<DeviceGroupDto> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

    private void fetchAvailableDevices() {
        DeviceService deviceService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(DeviceService.class);

        deviceService.listDevices().enqueue(new Callback<List<DeviceDto>>() {
            @Override
            public void onResponse(Call<List<DeviceDto>> call, Response<List<DeviceDto>> response) {
                for(DeviceDto d : response.body()) {
                    availableDevices.add(d.generateDevice());
                }
                availableDevicesAdapter.notifyDataSetChanged();
                // Once all devices has been fetched, fetch devices that belong to this group.
                fetchGroupDevices();
            }

            @Override
            public void onFailure(Call<List<DeviceDto>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

    private void fetchGroupDevices() {
        GroupService buildingService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        buildingService.listGroupDevices(groupId).enqueue(new Callback<List<DeviceDto>>() {

            @Override
            public void onResponse(Call<List<DeviceDto>> call, Response<List<DeviceDto>> response) {
                List<DeviceDto> list = response.body();

                Log.i("RETROFIT", list.size() + " devices fetched.");
                for (DeviceDto d : list) {
                    Device device = d.generateDevice();
                    addToGroup(device);
                }
            }

            @Override
            public void onFailure(Call<List<DeviceDto>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

    public void addToGroup(Device device) {
        // If a device is in this group, we remove it from the available devices.
        availableDevices.remove(device);
        if (availableDevices.size() == 0) {
            findViewById(R.id.all_devices_in_group).setVisibility(View.VISIBLE);
        }
        inGroupDevices.add(device);
        availableDevicesAdapter.notifyDataSetChanged();
        inGroupDevicesAdapter.notifyDataSetChanged();
    }

    public void removeFromGroup(Device device) {
        availableDevices.add(device);
        if (availableDevices.size() == 1) {
            // All the devices was in this group before
            findViewById(R.id.all_devices_in_group).setVisibility(View.GONE);
        }
        inGroupDevices.remove(device);
        availableDevicesAdapter.notifyDataSetChanged();
        inGroupDevicesAdapter.notifyDataSetChanged();
    }

    public void updateGroup() {
        deviceGroupDto.setName(editName.getText().toString());
        ArrayList<Integer> devices = new ArrayList<>();
        for (Device d : inGroupDevices) {
            devices.add(d.getId());
        }
        deviceGroupDto.setDevices(devices);

        GroupService buildingService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        Activity thisActivity = this;

        buildingService.updateGroup(groupId, deviceGroupDto).enqueue(new Callback<DeviceGroupDto>() {

            @Override
            public void onResponse(Call<DeviceGroupDto> call, Response<DeviceGroupDto> response) {
                Log.i("RETROFIT", "Group " + deviceGroupDto.getName() + " updated.");
                Intent intent = new Intent(thisActivity, GroupSelectionActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<DeviceGroupDto> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }
}
