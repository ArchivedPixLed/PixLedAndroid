package com.pixled.pixledandroid.device;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupSelectionActivity;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupService;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupViewFragment;
import com.pixled.pixledandroid.utils.DeviceGroupIdPair;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledserver.core.ToggleState;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.base.DeviceDto;
import com.pixled.pixledserver.core.group.DeviceGroup;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class DeviceViewHolder extends RecyclerView.ViewHolder {

    /*
    Controller for lights views (views that are displayed for each light in the RecyclerView)
     */
    private Device device;

    private EditText deviceName;
    private Switch deviceSwitch;
    private Button changeColorButton;
    private TextView connectedTextView;
    private SeekBar intensitySeekBar;
    private ImageView deviceIcon;

    // Card view associated to this view
    private CardView rootCardView;

    private GroupSelectionActivity groupSelectionActivity;

    public DeviceViewHolder(final View itemView,
                            final GroupSelectionActivity groupSelectionActivity,
                            boolean enableColorButton) {
        super(itemView);
        rootCardView = itemView.findViewById(R.id.rootCardView);

        this.groupSelectionActivity = groupSelectionActivity;

        // Light id view
        deviceName = itemView.findViewById(R.id.nameEdit);
        deviceName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // When the user finishes to edit the name, the EditText lose focus
                if(!hasFocus) {
                    Log.i("TEST NAME", "New name : " + deviceName.getText());
                    DeviceService deviceService = new Retrofit.Builder()
                            .baseUrl(ServerConfig.ENDPOINT)
                            .addConverterFactory(JacksonConverterFactory.create())
                            .build()
                            .create(DeviceService.class);
                    String oldName = device.getName();
                    device.setName(deviceName.getText().toString());
                    deviceService.updateDevice(device.getId(),
                            "application/json;charset=UTF-8",
                            device.generateDto()).enqueue(
                            new Callback<DeviceDto>() {
                        @Override
                        public void onResponse(Call<DeviceDto> call, Response<DeviceDto> response) {
                            for (DeviceGroup dg : device.getDeviceGroups()) {
                                DeviceViewHolder deviceViewHolder = groupSelectionActivity.getDeviceViewsIndex().get(new DeviceGroupIdPair(device.getId(), dg.getId()));
                                if (deviceViewHolder != null) {
                                    deviceViewHolder.updateDeviceName();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<DeviceDto> call, Throwable t) {
                            deviceName.setText(oldName);
                        }
                    });
                }
            }
        });

        // Device Icon
        deviceIcon = itemView.findViewById(R.id.deviceIcon);
        deviceIcon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(groupSelectionActivity, R.style.Theme_AppCompat_Dialog_Alert);
                builder.setTitle("Delete Device")
                        .setMessage("Are you sure you want to delete this device?\n" + device.getName())
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                DeviceService deviceService = new Retrofit.Builder()
                                        .baseUrl(ServerConfig.ENDPOINT)
                                        .addConverterFactory(JacksonConverterFactory.create())
                                        .build()
                                        .create(DeviceService.class);

                                deviceService.deleteDevice(device.getId()).enqueue(new Callback() {
                                    @Override
                                    public void onResponse(Call call, Response response) {
                                        groupSelectionActivity.getDeviceViewsIndex().remove(device.getId());
                                        for (DeviceGroup deviceGroup : device.getDeviceGroups()) {
                                            deviceGroup.getDevices().remove(device);
                                            groupSelectionActivity.getViewFragmentIndex().get(deviceGroup.getId()).getDeviceAdapter().notifyDataSetChanged();
                                        }

                                        CharSequence text = "Device " + device.getName() + " deleted.";
                                        int duration = Toast.LENGTH_SHORT;

                                        Toast toast = Toast.makeText(groupSelectionActivity, text, duration);
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
                return false;
            }
        });

        // Connected TextView
        connectedTextView = itemView.findViewById(R.id.connected);

        // Button used to switch light
        deviceSwitch = itemView.findViewById(R.id.deviceSwitch);
        deviceSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceService deviceService = new Retrofit.Builder()
                        .baseUrl(ServerConfig.ENDPOINT)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build()
                        .create(DeviceService.class);

                deviceService.switchDevice(device.getId()).enqueue(new Callback<DeviceDto>() {
                    @Override
                    public void onResponse(Call<DeviceDto> call, Response<DeviceDto> response) {
                        device.switchDevice();

                        // Update views in all the group views potentially concerned
                        for (DeviceGroup dg : device.getDeviceGroups()) {
                            GroupViewFragment groupViewFragment = groupSelectionActivity.getViewFragmentIndex().get(dg.getId());
                            // groupViewFragment.getDeviceAdapter().notifyDataSetChanged();
                            groupViewFragment.updateDeviceGroupState();
                        }

                        for (DeviceGroup dg : device.getDeviceGroups()) {
                            DeviceViewHolder deviceViewHolder = groupSelectionActivity.getDeviceViewsIndex().get(new DeviceGroupIdPair(device.getId(), dg.getId()));
                            if (deviceViewHolder != null) {
                                deviceViewHolder.updateSwitch();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<DeviceDto> call, Throwable t){
                        Log.i("RETROFIT", "Error on switchLight " + device.getId());
                        deviceSwitch.setChecked(!deviceSwitch.isChecked());
                    }
                }
                );
            }
        });

        // Initiate the color selection view
        changeColorButton = itemView.findViewById(R.id.change_color_button);
        final DeviceViewHolder thisLightViewHolder = this;
        if (enableColorButton) {
            changeColorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    groupSelectionActivity.showChangeColor(device, thisLightViewHolder);
                }
            });
        }

        // Instanciating a device service
        final DeviceService deviceService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(DeviceService.class);

        // Initiate the intensity slider
        intensitySeekBar = itemView.findViewById(R.id.intensitySeekBar);
        intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // set seek bar color
                if (fromUser) {
                    // Synchronize other views
                    for (DeviceGroup dg : device.getDeviceGroups()) {
                        DeviceViewHolder deviceViewHolder = groupSelectionActivity.getDeviceViewsIndex().get(new DeviceGroupIdPair(device.getId(), dg.getId()));
                        if (deviceViewHolder != null) {
                            deviceViewHolder.updateColorIntensity(progress);
                        }
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                GroupSelectionActivity.publishColorChanged(deviceService, device);
            }
        });
    }

    public void bind(Device device){
        // Used to synchronized the view with informations contained in the corresponding
        // Light instance.
        this.device = device;
        if (device.getName() != null && device.getName().length() > 0) {
            deviceName.setText(device.getName());
        }
        else {
            deviceName.setText("Device " + Long.valueOf(device.getId()));
        }

        updateSwitch();

        updateConnectionStatus();

        updateColorBox();

        int initialColor = device.getDeviceState().getColor().getArgb();
        intensitySeekBar.getProgressDrawable().setColorFilter(initialColor, PorterDuff.Mode.MULTIPLY);
        intensitySeekBar.getThumb().setColorFilter(initialColor, PorterDuff.Mode.SRC_ATOP);
        intensitySeekBar.setProgress((int) (device.getDeviceState().getColor().getValue() * 100));
    }

    public void updateColorBox() {
        float[] hsv = {
                device.getDeviceState().getColor().getHue(),
                device.getDeviceState().getColor().getSaturation(),
                1};
        int fullColor = Color.HSVToColor(hsv);
        changeColorButton.setBackgroundColor(fullColor);
    }

    public void updateColorIntensity(int progress) {
        intensitySeekBar.setProgress(progress);
        device.getDeviceState().getColor().setValue(((float) progress) / 100);
        intensitySeekBar.getProgressDrawable().setColorFilter(device.getDeviceState().getColor().getArgb(), PorterDuff.Mode.MULTIPLY);
        intensitySeekBar.getThumb().setColorFilter(device.getDeviceState().getColor().getArgb(), PorterDuff.Mode.SRC_ATOP);
    }

    public void updateConnectionStatus() {
        connectedTextView.setText(device.getDeviceState().isConnected() ? "connected" : "disconnected");

        connectedTextView.setTextColor(device.getDeviceState().isConnected() ?
                groupSelectionActivity.getResources().getColor(R.color.device_connected) :
                groupSelectionActivity.getResources().getColor(R.color.device_disconnected));

        rootCardView.setCardBackgroundColor(device.getDeviceState().isConnected() ?
                groupSelectionActivity.getResources().getColor(R.color.card_view_background) :
                groupSelectionActivity.getResources().getColor(R.color.disconnected_background));
    }

    public void updateSwitch() {
        deviceSwitch.setChecked(device.getDeviceState().getToggleState() == ToggleState.ON);
    }

    public void updateDeviceName() {
        deviceName.setText(device.getName());
    }

    public Button getChangeColorButton() {
        return changeColorButton;
    }

    public SeekBar getIntensitySeekBar() {
        return intensitySeekBar;
    }

    public Device getDevice() {
        return device;
    }
}
