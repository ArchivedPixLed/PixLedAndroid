package com.example.paulbreugnot.lightroom.light;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
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
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class LightViewHolder extends RecyclerView.ViewHolder {

    /*
    Controller for lights views (views that are displayed for each light in the RecyclerView)
     */
    private Light light;

    private TextView lightId;
    private Switch lightSwitch;
    private Button changeColorButton;
    private TextView connectedTextView;
    private SeekBar intensitySeekBar;

    private RoomViewFragment roomViewFragment;

    public LightViewHolder(final View itemView,
                           final RoomViewFragment roomViewFragment,
                           boolean enableColorButton) {
        super(itemView);

        this.roomViewFragment = roomViewFragment;

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
        final LightViewHolder thisLightViewHolder = this;
        if (enableColorButton) {
            changeColorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((RoomSelectionActivity) roomViewFragment.getActivity())
                            .showChangeColor(light, thisLightViewHolder);
                }
            });
        }

        // Instanciating a light service
        final LightService lightService = new Retrofit.Builder()
                .baseUrl(LightService.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(LightService.class);

        // Initiate the intensity slider
        intensitySeekBar = itemView.findViewById(R.id.intensitySeekBar);
        intensitySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // set seek bar color
                light.setValue(((float) progress) / 100);
                intensitySeekBar.getProgressDrawable().setColorFilter(light.getArgbColor(), PorterDuff.Mode.MULTIPLY);
                intensitySeekBar.getThumb().setColorFilter(light.getArgbColor(), PorterDuff.Mode.SRC_ATOP);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                RoomSelectionActivity.publishColorChanged(lightService, light);
            }
        });
    }

    public void bind(Light light){
        // Used to synchronized the view with informations contained in the corresponding
        // Light instance.
        this.light = light;
        lightId.setText((Long.valueOf(light.getId())).toString());
        lightSwitch.setChecked(light.getStatus() == Status.ON);
        changeColorButton.setBackgroundColor(light.getColorWithMaxValue());
        int initialColor = light.getArgbColor();
        intensitySeekBar.getProgressDrawable().setColorFilter(initialColor, PorterDuff.Mode.MULTIPLY);
        intensitySeekBar.getThumb().setColorFilter(initialColor, PorterDuff.Mode.SRC_ATOP);
        intensitySeekBar.setProgress((int) (light.getValue() * 100));
    }

    public Button getChangeColorButton() {
        return changeColorButton;
    }

    public SeekBar getIntensitySeekBar() {
        return intensitySeekBar;
    }

    public Light getLight() {
        return light;
    }

    public RoomViewFragment getRoomViewFragment() {
        return roomViewFragment;
    }
}
