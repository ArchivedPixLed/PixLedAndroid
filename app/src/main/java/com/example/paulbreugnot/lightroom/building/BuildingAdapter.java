package com.example.paulbreugnot.lightroom.building;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paulbreugnot.lightroom.R;

import java.util.List;

public class BuildingAdapter extends RecyclerView.Adapter<BuildingViewHolder> {

    BuildingSelectionActivity buildingSelectionActivity;
    List<Building> list;

    //ajouter un constructeur prenant en entrée une liste
    public BuildingAdapter(List<Building> list, BuildingSelectionActivity buildingSelectionActivity) {
        this.list = list;
        this.buildingSelectionActivity = buildingSelectionActivity;
    }

    //cette fonction permet de créer les viewHolder
    //et par la même indiquer la vue à inflater (à partir des layout xml)
    @Override
    public BuildingViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.building_card_view,viewGroup,false);
        return new BuildingViewHolder(view);
    }

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
