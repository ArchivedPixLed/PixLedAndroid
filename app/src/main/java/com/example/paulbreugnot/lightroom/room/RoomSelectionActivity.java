package com.example.paulbreugnot.lightroom.room;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.building.Building;
import com.example.paulbreugnot.lightroom.building.BuildingSelectionActivity;
import com.example.paulbreugnot.lightroom.building.BuildingService;
import com.example.paulbreugnot.lightroom.light.ChangeColorActivity;
import com.example.paulbreugnot.lightroom.light.Light;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RoomSelectionActivity extends FragmentActivity {

    private static final String LOG_TAG = "SELECT_ROOM";

    /**
     * The pager widget, which handles animation and allows swiping horizontally to access previous
     * and next wizard steps.
     */
    private ViewPager mPager;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private PagerAdapter mPagerAdapter;

    private ActionBar.TabListener tabListener;

    private long buildingId;
    private List<Room> rooms = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        final ActionBar actionBar = getActionBar();


        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        buildingId = intent.getLongExtra(BuildingSelectionActivity.EXTRA_BUILDING_ID, 0L);
        String buildingName = intent.getStringExtra(BuildingSelectionActivity.EXTRA_BUILDING_NAME);
        Log.i(LOG_TAG, "Building id : " + buildingId);
        Log.i(LOG_TAG, "Building name : " + buildingName);

        setTitle(buildingName);
        setContentView(R.layout.room_pager);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = findViewById(R.id.room_pager);
        mPagerAdapter = new RoomPagerAdapter(getSupportFragmentManager(), rooms);
        mPager.setAdapter(mPagerAdapter);

        // Create a tab listener that is called when the user changes tabs.
        tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                mPager.setCurrentItem(tab.getPosition());
                // ((TextView) findViewById(R.id.lightNumber)).setText(rooms.get(tab.getPosition()).getName());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

        };

        mPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        fetchRooms();

//        for(int i = 0; i < 12; i++) {
//            actionBar.addTab(
//                    actionBar.newTab()
//                            .setText("Tab " + (i + 1))
//                            .setTabListener(tabListener));
//
//        }
    }

    private void fetchRooms() {
//        for (int i = 0; i < 2; i++) {
//            rooms.add(new Room(i, "Room " + i, 0, -12));
//        }
        BuildingService buildingService = new Retrofit.Builder()
                .baseUrl(BuildingService.ENDPOINT)
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(BuildingService.class);

        buildingService.listBuildingRooms(buildingId).enqueue(new Callback<List<Room>>() {
            ActionBar actionBar = getActionBar();

            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                List<Room> listRooms = response.body();

                Log.i("RETROFIT", listRooms.size() + " buildings fetched.");
                for (Room r : listRooms) {
                    Log.i("RETROFIT","Room : " + r.getName());
                    rooms.add(r);
                    mPagerAdapter.notifyDataSetChanged();
                    actionBar.addTab(
                            actionBar.newTab()
                                    .setText(r.getName())
                                    .setTabListener(tabListener));
                    // buildingAdapter.notifyItemInserted(buildings.size() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.e("RETROFIT","Retrofit error : " + t);
            }
        });
    }

}
