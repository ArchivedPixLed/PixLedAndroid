package com.example.paulbreugnot.lightroom.building;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.paulbreugnot.lightroom.R;

import java.util.ArrayList;
import java.util.List;

public class BuildingSelectionActivity extends Activity {

    private RecyclerView recyclerView;

    private List<Building> buildings = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // roomContextHttpManager = new RoomContextHttpManager(this);

        setContentView(R.layout.building_select);
        // initTextFieldListener();

        feedBuildings();

        recyclerView = findViewById(R.id.buildingList);

        //définit l'agencement des cellules, ici de façon verticale, comme une ListView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //pour adapter en grille comme une RecyclerView, avec 2 cellules par ligne
        //recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        //puis créer un MyAdapter, lui fournir notre liste de villes.
        //cet adapter servira à remplir notre recyclerview
        recyclerView.setAdapter(new BuildingAdapter(buildings));
    }

    private void feedBuildings() {
        for (int i = 0; i < 20; i++) {
            buildings.add(new Building("Building " + i));
        }
    }
}
