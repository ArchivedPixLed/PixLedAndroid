package com.example.paulbreugnot.lightroom.building;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;

public class BuildingViewHolder extends RecyclerView.ViewHolder {

    /*
    Controller for buildings views (views that are displayed for each building in the RecyclerView)
     */
    private TextView textView;
    private Building building;
    private BuildingSelectionActivity buildingSelectionActivity;

    public BuildingViewHolder(final View itemView) {
        super(itemView);

        textView = itemView.findViewById(R.id.text);

        final LinearLayout button = itemView.findViewById(R.id.layout);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("BUILDING SELECT", building.getName());
                buildingSelectionActivity.launchRoomSelectionActivity(building);
            }
        });
    }

    public void bind(Building building, BuildingSelectionActivity buildingSelectionActivity){
        // Used to synchronized the view with information contained in the corresponding
        // Building instance.
        this.building = building;
        this.buildingSelectionActivity = buildingSelectionActivity;
        textView.setText(building.getName());
    }
}
