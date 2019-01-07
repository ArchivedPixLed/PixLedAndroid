package com.example.paulbreugnot.lightroom.room;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.light.Light;
import com.example.paulbreugnot.lightroom.light.LightAdapter;
import com.example.paulbreugnot.lightroom.light.LightViewHolder;
import com.example.paulbreugnot.lightroom.utils.ServerConfig;
import com.example.paulbreugnot.lightroom.utils.Status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RoomViewFragment extends Fragment {
    /*
    This fragment corresponds to a room page.
     */
    private long roomId;
    private List<Light> lightList = new ArrayList<>();

    private LightAdapter lightAdapter;

    private TextView lightNumberTextView;
    private int lightNumber = 0;
    private Switch roomSwitch;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.room_page, container, false);

        /*
        Used to show the number of available rooms at the top of the view.
         */
        lightNumberTextView = rootView.findViewById(R.id.lightNumber);
        lightNumberTextView.setText("0");

        /*
        The button to switch a whole room.
        How this operation is handled depends on the server side, but each light status will be
        synchronized in any case.
         */
        roomSwitch = rootView.findViewById(R.id.roomSwitch);
        roomSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomService roomService = new Retrofit.Builder()
                        .baseUrl(ServerConfig.ENDPOINT)
                        .addConverterFactory(JacksonConverterFactory.create())
                        .build()
                        .create(RoomService.class);

                roomService.switchRoom(roomId).enqueue(new Callback<List<Light>>() {
                    @Override
                    public void onResponse(Call<List<Light>> call, Response<List<Light>> response) {
                        // Synchronize each light status
                        HashMap<Long, Status> newLightStatus = new HashMap<>();
                        for (Light light : response.body()) {
                            newLightStatus.put(light.getId(), light.getStatus());
                        }
                        for (LightViewHolder lightView : lightAdapter.getLightViews()) {
                            Light viewLight = lightView.getLight();
                            viewLight.setStatus(newLightStatus.get(viewLight.getId()));
                            lightAdapter.notifyItemChanged(lightView.getAdapterPosition());
                        }
                    }

                    @Override
                    public void onFailure(Call<List<Light>> call, Throwable t) {
                        // Canceled operation
                        roomSwitch.setChecked(!roomSwitch.isChecked());
                    }
                });
            }
        });

        // Init button status
        roomSwitch.setChecked(getArguments().getString("roomStatus").equals("ON"));

        // Retrieve the id of the room corresponding to this view
        roomId = getArguments().getLong("roomId");

        // The recycler view (aka a list) in which lights will be displayed
        RecyclerView recyclerView = rootView.findViewById(R.id.lightList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        lightAdapter = new LightAdapter(lightList, this, true);
        recyclerView.setAdapter(lightAdapter);

        // Synchronize index
        ((RoomSelectionActivity) getActivity()).getLightAdapterIndex().put(roomId, lightAdapter);

        // Fetch lights from the server, feeding the recycler view.
        fetchLights();

        return rootView;
    }

    private void fetchLights() {
        RoomService buildingService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(RoomService.class);

        buildingService.listRoomLights(roomId).enqueue(new Callback<List<Light>>() {

            @Override
            public void onResponse(Call<List<Light>> call, Response<List<Light>> response) {
                List<Light> list = response.body();

                Log.i("RETROFIT", lightList.size() + " lights fetched.");
                for (Light l : list) {
                    Log.i("RETROFIT","Light : " + l.getId());
                    lightList.add(l);
                    // Update the recycler view
                    lightAdapter.notifyItemInserted(lightList.size() - 1);

                    // Update light number
                    lightNumber++;
                    lightNumberTextView.setText((new Long(lightNumber)).toString());
                }
            }

            @Override
            public void onFailure(Call<List<Light>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

    public void updateRoomStatus() {
        /*
        Used when room are switched, to keep room status synchronized.
        For example when all lights are switched off, the room status must be set to off.
        Notice that room status is kept consistent from the server side, this function just
        fetch each time the room status to keep it synchonized.
        */
        RoomService roomService = new Retrofit.Builder()
                .baseUrl(ServerConfig.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(RoomService.class);

        roomService.getRoom(roomId).enqueue(new Callback<Room>() {
            @Override
            public void onResponse(Call<Room> call, Response<Room> response) {
                roomSwitch.setChecked(response.body().getStatus() == Status.ON);
            }

            @Override
            public void onFailure(Call<Room> call, Throwable t) {

            }
        });
    }

    public LightAdapter getLightAdapter() {
        return lightAdapter;
    }

}
