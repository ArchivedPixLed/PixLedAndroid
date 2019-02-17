package com.pixled.pixledandroid.deviceGroup.editActivity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.device.DeviceService;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupSelectionActivity;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupService;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.base.DeviceDto;
import com.pixled.pixledserver.core.group.DeviceGroup;
import com.pixled.pixledserver.core.group.DeviceGroupDto;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class EditGroupActivity extends AppCompatActivity  {

    private enum Mode {EDIT, NEW}

    private Mode mode;

    private Toolbar toolbar;
    private int groupId;
    private EditText editName;
    private List<Device> availableDevices;
    private List<Device> inGroupDevices;
    private Button doneButton;

    private DeviceListAdapter availableDevicesAdapter;
    private DeviceListAdapter inGroupDevicesAdapter;

    // DTO of the device group that correspond to this view.
    private DeviceGroupDto deviceGroupDto;

    private GroupSelectionActivity groupSelectionActivity;

    public EditGroupActivity() {
        availableDevices = new ArrayList<>();
        inGroupDevices = new ArrayList<>();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return true;
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
                switch(mode){
                    case NEW:
                        createGroup();
                        break;
                    case EDIT:
                        updateGroup();
                        break;
                }
            }
        });

        toolbar = findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);

        if (getIntent().getExtras() != null) {
            groupId = getIntent().getExtras().getInt("groupId", -1);
            if (groupId == -1) {
                // No group id was provided, so we want to create a new group.
                mode = Mode.NEW;
                toolbar.setTitle(R.string.new_group);
            } else {
                mode = Mode.EDIT;
                toolbar.setTitle(R.string.edit_group);
                toolbar.inflateMenu(R.menu.group_menu);
            }
        }
        else {
            mode = Mode.NEW;
            toolbar.setTitle(R.string.new_group);
        }

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

        if (mode == Mode.EDIT) {
            fetchGroup();
        }
        else {
            deviceGroupDto = new DeviceGroupDto(new DeviceGroup());
            fetchAvailableDevices();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Activity thisActivity = this;
        switch (item.getItemId()) {
            case R.id.delete_group:
                // User chose the "Settings" item, show the app settings UI...
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Delete Group")
                        .setMessage("Are you sure you want to delete this group?\n(Devices won't be deleted)")
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                GroupService groupService = new Retrofit.Builder()
                                        .baseUrl(ServerConfig.ENDPOINT)
                                        .addConverterFactory(JacksonConverterFactory.create())
                                        .build()
                                        .create(GroupService.class);

                                groupService.deleteGroup(deviceGroupDto.getId()).enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        Intent intent = new Intent(thisActivity, GroupSelectionActivity.class);
                                        startActivity(intent);
                                        Context context = getApplicationContext();
                                        CharSequence text = "Group " + deviceGroupDto.getName() + " deleted.";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }

                                    @Override
                                    public void onFailure(Call call, Throwable t) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void fetchGroup() {
        GroupService groupService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
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
                    if (deviceGroupDto.getDevices().contains(d.getId())){
                        inGroupDevices.add(d.generateDevice());
                    }
                    else {
                        availableDevices.add(d.generateDevice());
                    }
                }
                inGroupDevicesAdapter.notifyDataSetChanged();
                availableDevicesAdapter.notifyDataSetChanged();
                if (availableDevices.size() == 0) {
                    // All the devices was in this group before
                    findViewById(R.id.all_devices_in_group).setVisibility(View.VISIBLE);
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

    public void createGroup() {
        deviceGroupDto.setName(editName.getText().toString());
        ArrayList<Integer> devices = new ArrayList<>();
        for (Device d : inGroupDevices) {
            devices.add(d.getId());
        }
        deviceGroupDto.setDevices(devices);

        GroupService groupService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(GroupService.class);

        Activity thisActivity = this;

        groupService.createGroup(deviceGroupDto).enqueue(new Callback<DeviceGroupDto>() {

            @Override
            public void onResponse(Call<DeviceGroupDto> call, Response<DeviceGroupDto> response) {
                Log.i("RETROFIT", "Group " + deviceGroupDto.getName() + " created.");
                Intent intent = new Intent(thisActivity, GroupSelectionActivity.class);
                startActivity(intent);
                Context context = getApplicationContext();
                CharSequence text = "Group " + deviceGroupDto.getName() + " created.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onFailure(Call<DeviceGroupDto> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
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
                Context context = getApplicationContext();
                CharSequence text = "Group " + deviceGroupDto.getName() + " updated.";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }

            @Override
            public void onFailure(Call<DeviceGroupDto> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }
}
