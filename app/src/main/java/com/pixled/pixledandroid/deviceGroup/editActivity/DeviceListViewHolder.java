package com.pixled.pixledandroid.deviceGroup.editActivity;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixled.pixledandroid.R;
import com.pixled.pixledserver.core.device.base.Device;
import com.pixled.pixledserver.core.device.strip.Strip;

public class DeviceListViewHolder extends RecyclerView.ViewHolder {

    public enum Mode {AVAILABLE, IN_GROUP}

    private Device device;
    private Mode mode;

    private TextView name;
    private ImageView type;
    private Button moveButton;

    public DeviceListViewHolder(@NonNull View itemView, Mode mode, EditGroupActivity editActivity) {
        super(itemView);
        this.mode = mode;

        name = itemView.findViewById(R.id.device_name);
        type = itemView.findViewById(R.id.device_type);
        moveButton = itemView.findViewById(R.id.move_button);
        switch(mode) {
            case AVAILABLE:
                moveButton.setText("Add");
                moveButton.setCompoundDrawablesWithIntrinsicBounds(itemView.getResources().getDrawable(R.drawable.down_arrow), null, null, null);
                break;
            case IN_GROUP:
                moveButton.setText("Remove");
                moveButton.setCompoundDrawablesWithIntrinsicBounds(itemView.getResources().getDrawable(R.drawable.delete), null, null, null);
                break;
        }
        moveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(mode) {
                    case AVAILABLE:
                        editActivity.addToGroup(device);
                        break;
                    case IN_GROUP:
                        editActivity.removeFromGroup(device);
                        break;
                }
            }
        });
    }

    public void bind(Device device) {
        this.device = device;

        name.setText(device.getName());
        if (device.getClass() == Strip.class) {
            type.setImageResource(R.drawable.strip_logo);
        }
    }




}
