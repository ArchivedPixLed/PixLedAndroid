package com.example.paulbreugnot.lightroom.building;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.light.Light;
import com.example.paulbreugnot.lightroom.room.RoomSelectionActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BuildingSelectionActivity extends Activity {

    public static final String EXTRA_BUILDING_ID = "com.example.paulbreugnot.lightroom.building.EXTRA_BUILDING_ID";
    public static final String EXTRA_BUILDING_NAME = "com.example.paulbreugnot.lightroom.building.EXTRA_BUILDING_NAME";

    private RecyclerView recyclerView;
    private BuildingAdapter buildingAdapter;

    private List<Building> buildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.select_building);

        setContentView(R.layout.building_select);

        fetchBuildings();

        recyclerView = findViewById(R.id.buildingList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        buildingAdapter = new BuildingAdapter(buildings, this);
        recyclerView.setAdapter(buildingAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // getMenuInflater().inflate(R.menu.building_menu, menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.building_menu, menu);
        menu.findItem(R.id.add_building).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//        for (int i = 0; i < menu.size(); i++) {
//            menu.getItem(i).
//        }
        return super.onCreateOptionsMenu(menu);
    }

    private void fetchBuildings() {
//        for (int i = 0; i < 2; i++) {
//            buildings.add(new Building(i, "Building " + i));
//        }
        BuildingService buildingService = new Retrofit.Builder()
                .baseUrl(BuildingService.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BuildingService.class);

        buildingService.listBuildings().enqueue(new Callback<List<Building>>() {

            @Override
            public void onResponse(Call<List<Building>> call, Response<List<Building>> response) {
                List<Building> listBuildings = response.body();

                Log.i("RETROFIT", listBuildings.size() + " buildings fetched.");
                for (Building b : listBuildings) {
                    Log.i("RETROFIT","Building : " + b.getName());
                    buildings.add(b);
                    buildingAdapter.notifyItemInserted(buildings.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Building>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }


    protected void launchRoomSelectionActivity(Building building){
        Intent intent = new Intent(this, RoomSelectionActivity.class);
        // Add extras
        intent.putExtra(EXTRA_BUILDING_ID, building.getId());
        intent.putExtra(EXTRA_BUILDING_NAME, building.getName());
        startActivity(intent);
    }
}
