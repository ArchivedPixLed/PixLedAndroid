package com.example.paulbreugnot.lightroom.building;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paulbreugnot.lightroom.R;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

    /*
    This class is used to generate views corresponding to each building in the RecyclerView, from a
    list of buildings.
     */

    BuildingSelectionActivity buildingSelectionActivity;
    List<Building> list;

    public BuildingAdapter(List<Building> list, BuildingSelectionActivity buildingSelectionActivity) {
        this.list = list;
        this.buildingSelectionActivity = buildingSelectionActivity;
    }

    /*
    Automatically called to generated each building view from the building list.
    */
    @Override
    public BuildingViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.building_card_view,viewGroup,false);
        return new BuildingViewHolder(view);
    }

    /*
    Bind each list item with the corresponding view.
    */
    @Override
    public void onBindViewHolder(BuildingViewHolder buildingViewHolder, int position) {
        Building building = list.get(position);
        buildingViewHolder.bind(building, buildingSelectionActivity);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
