package com.pixled.pixledandroid.deviceGroup.editActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pixled.pixledandroid.R;
import com.pixled.pixledserver.core.device.base.Device;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListViewHolder> {

    private List<Device> devices;

    private DeviceListViewHolder.Mode mode;

    // Reference to the edit activity to move devices in group. This activity is given to view holders.
    private EditGroupActivity editGroupActivity;

    public DeviceListAdapter(List<Device> devices, DeviceListViewHolder.Mode mode, EditGroupActivity editGroupActivity) {
        this.devices = devices;
        this.mode = mode;
        this.editGroupActivity = editGroupActivity;
    }

    @NonNull
    @Override
    public DeviceListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.add_device_cardview,viewGroup,false);
        DeviceListViewHolder newdeviceView = new DeviceListViewHolder(view, mode, editGroupActivity);
        return newdeviceView;
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceListViewHolder deviceListViewHolder, int i) {
        Device device = devices.get(i);
        deviceListViewHolder.bind(device);
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }
}
