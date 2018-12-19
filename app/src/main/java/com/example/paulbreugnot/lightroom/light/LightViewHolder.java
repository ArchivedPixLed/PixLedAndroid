package com.example.paulbreugnot.lightroom.light;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.room.RoomViewFragment;
import com.example.paulbreugnot.lightroom.utils.Status;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LightViewHolder extends RecyclerView.ViewHolder {

    private Light light;

    private TextView lightId;
    private Switch lightSwitch;

    private RoomViewFragment roomViewFragment;

    public LightViewHolder(final View itemView, final RoomViewFragment roomViewFragment) {
        super(itemView);

        this.roomViewFragment = roomViewFragment;

        lightId = itemView.findViewById(R.id.lightId);
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
        Button changeColorButton = itemView.findViewById(R.id.change_color_button);
        changeColorButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                launchChangeColorActivity(light);
            }
        });

    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(Light light){
        this.light = light;
        lightId.setText((Long.valueOf(light.getId())).toString());
        lightSwitch.setChecked(light.getStatus() == Status.ON);
        // Picasso.with(imageView.getContext()).load(myObject.getImageUrl()).centerCrop().fit().into(imageView);
    }

    public Light getLight() {
        return light;
    }

    protected void launchChangeColorActivity(Light light){
        Intent intent = new Intent(roomViewFragment.getContext(), ChangeColorActivity.class);
        roomViewFragment.getActivity().startActivity(intent);
    }
}
