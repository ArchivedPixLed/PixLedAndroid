package com.example.paulbreugnot.lightroom.room;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.building.BuildingSelectionActivity;
import com.example.paulbreugnot.lightroom.building.BuildingService;
import com.example.paulbreugnot.lightroom.light.Light;
import com.example.paulbreugnot.lightroom.light.LightAdapter;
import com.example.paulbreugnot.lightroom.light.LightService;
import com.example.paulbreugnot.lightroom.light.LightViewHolder;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorChangedListener;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.slider.LightnessSlider;
import com.flask.colorpicker.slider.OnValueChangedListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RoomSelectionActivity extends FragmentActivity {

    private static final String LOG_TAG = "SELECT_ROOM";

    /*
    Pager setup
     */
    private ViewPager roomPager;
    private PagerAdapter roomPagerAdapter;

    private ActionBar.TabListener tabListener;

    private long buildingId;
    private String buildingName;
    private List<Room> rooms = new ArrayList<>();

    /*
    Change color setup
     */
    private View changeColor;
    private ColorPickerView colorPicker;
    private LightnessSlider lightnessSlider;
    private Button OkButton;
    private Light selectedLight;
    private LightViewHolder selectedLightViewHolder;

    // The adapter of RecyclerView displayed above the color picker
    private LightAdapter colorChangeLightAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Action bar that is shown on top of the app
        final ActionBar actionBar = getActionBar();

        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Retrieves Building informations from the original BuildingSelectionActivity
        Intent intent = getIntent();
        buildingId = intent.getLongExtra(BuildingSelectionActivity.EXTRA_BUILDING_ID, 0L);
        buildingName = intent.getStringExtra(BuildingSelectionActivity.EXTRA_BUILDING_NAME);
        Log.i(LOG_TAG, "Building id : " + buildingId);
        Log.i(LOG_TAG, "Building name : " + buildingName);

        // Set up main view
        setTitle(buildingName);
        setContentView(R.layout.room_pager);

        // To show pages (aka rooms) as tabs
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);


        // Instantiate a ViewPager and a PagerAdapter.
        roomPager = findViewById(R.id.room_pager);
        roomPagerAdapter = new RoomPagerAdapter(getSupportFragmentManager(), rooms);
        roomPager.setAdapter(roomPagerAdapter);
        getActionBar().show();

        // Create a tab listener that is called when the user changes tabs.
        tabListener = new ActionBar.TabListener() {

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // When the tab is selected, switch to the
                // corresponding page in the ViewPager.
                roomPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

        };

        roomPager.addOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        /*
        Set up change color
         */
        changeColor = findViewById(R.id.change_color);
        colorPicker = findViewById(R.id.color_picker_view);
        colorPicker.addOnColorChangedListener(new OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                selectedLight.setColor(color);
                selectedLightViewHolder.getChangeColorButton().setBackgroundColor(color);
            }
        });
        lightnessSlider = findViewById(R.id.lightness_slider);
        colorPicker.setLightnessSlider(lightnessSlider);
        lightnessSlider.setColorPicker(colorPicker);
        OkButton = findViewById(R.id.validate_color);
        OkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRooms();
            }
        });

        final LightService lightService = new Retrofit.Builder()
                .baseUrl(LightService.ENDPOINT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(LightService.class);

        colorPicker.addOnColorSelectedListener(new OnColorSelectedListener() {
            @Override
            public void onColorSelected(int color) {
                publishColorChanged(lightService, color);
            }
        });

        lightnessSlider.setOnValueChangedListener(new OnValueChangedListener() {
            @Override
            public void onValueChanged(float v) {
                publishColorChanged(lightService, colorPicker.getSelectedColor());
            }
        });


        fetchRooms();
        // changeColor.setVisibility(View.VISIBLE);
        // changeColor.setVisibility(View.GONE);
    }

    private void fetchRooms() {
        // Fetch available rooms for the given building from the server
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
                    roomPagerAdapter.notifyDataSetChanged();
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

    private void publishColorChanged(LightService lightService, int color) {
        Log.i("COLOR PICKER",
                "Color changed : " + Integer.toString(color) + "(" +
                        ((color >> 16) & 0xff) + ", " +
                        ((color >> 8) & 0xff) + ", " +
                        ((color >> 0) & 0xff) + ")");
        lightService.changeLightColor(selectedLight.getId(),
                "text/plain;charset=UTF-8",
                Integer.toString(color))
                .enqueue(new Callback<Light>() {
                    @Override
                    public void onResponse(Call<Light> call, Response<Light> response) {
                        Log.i("COLOR PICKER", "Change color request OK");
                    }

                    @Override
                    public void onFailure(Call<Light> call, Throwable t) {
                        Log.i("COLOR PICKER", "Change color request failed." + t);
                    }
                });
        colorChangeLightAdapter.getLightViews().get(0).getChangeColorButton().setBackgroundColor(color);
    }

    public void showChangeColor(Light light, LightViewHolder lightViewHolder) {
        // Hide the pager view, show the color change view (called from "changeColor buttons" in
        // LightViewHolders.
        selectedLight = light;
        selectedLightViewHolder = lightViewHolder;
        // The recycler view (aka a list) in which lights will be displayed

        final RecyclerView recyclerView = findViewById(R.id.selected_light_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Light> lightList = new ArrayList<>();
        lightList.add(light);
        System.out.println(light == null);
        colorChangeLightAdapter = new LightAdapter(lightList, lightViewHolder.getRoomViewFragment(), false);
        recyclerView.setAdapter(colorChangeLightAdapter);

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        roomPager.setVisibility(View.INVISIBLE);
        changeColor.setVisibility(View.VISIBLE);

        colorPicker.setInitialColor(light.getColor(), false);
        lightnessSlider.setColor(light.getColor());
        float[] hsv = new float[3];
        Color.colorToHSV(light.getColor(), hsv);
        colorPicker.setLightness(hsv[2]);
    }

    public void showRooms() {
        // Synchronized the original light card view with the one that was displayed with the color
        // picker.
        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        selectedLightViewHolder.getRoomViewFragment().getLightAdapter()
                .notifyItemChanged(selectedLightViewHolder.getAdapterPosition());
        // Hide the change color view, show the pager view
        changeColor.setVisibility(View.INVISIBLE);
        roomPager.setVisibility(View.VISIBLE);
    }
}
