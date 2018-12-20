package com.example.paulbreugnot.lightroom.light;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.room.RoomSelectionActivity;
import com.example.paulbreugnot.lightroom.room.RoomViewFragment;
import com.example.paulbreugnot.lightroom.utils.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LightViewHolder extends RecyclerView.ViewHolder {

    /*
    Controller for lights views (views that are displayed for each light in the RecyclerView)
     */
    private Light light;

    private TextView lightId;
    private Switch lightSwitch;
    private Button changeColorButton;

    public LightViewHolder(final View itemView, final RoomViewFragment roomViewFragment) {
        super(itemView);

        // Light id view
        lightId = itemView.findViewById(R.id.lightId);

        // Button used to switch light
        lightSwitch = itemView.findViewById(R.id.lightSwitch);
        lightSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LightService lightService = new Retrofit.Builder()
                        .baseUrl(LightService.ENDPOINT)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build()
                        .create(LightService.class);

                lightService.switchLight(light.getId()).enqueue(new Callback<Light>() {
                    @Override
                    public void onResponse(Call<Light> call, Response<Light> response) {
                        light.switchLight();
                        // Update room status according to the current lights setup
                        roomViewFragment.updateRoomStatus();
                    }

                    @Override
                    public void onFailure(Call<Light> call, Throwable t){
                        Log.i("RETROFIT", "Error on switchLight " + light.getId());
                        lightSwitch.setChecked(!lightSwitch.isChecked());
                    }
                }
                );
            }
        });

        // Initiate the color selection view
        changeColorButton = itemView.findViewById(R.id.change_color_button);
        changeColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((RoomSelectionActivity) roomViewFragment.getActivity()).showChangeColor(light);
            }
        });

    }

    public void bind(Light light){
        // Used to synchronized the view with informations contained in the corresponding
        // Light instance.
        this.light = light;
        lightId.setText((Long.valueOf(light.getId())).toString());
        lightSwitch.setChecked(light.getStatus() == Status.ON);
        changeColorButton.setBackgroundColor(light.getColor());
    }

    public Light getLight() {
        return light;
    }
}
