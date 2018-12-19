package com.example.paulbreugnot.lightroom.light;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.room.RoomViewFragment;

import java.util.ArrayList;
import java.util.List;

public class LightAdapter extends RecyclerView.Adapter<LightViewHolder> {

    private List<Light> lightList;
    private List<LightViewHolder> lightViews;
    private RoomViewFragment roomViewFragment;

    //ajouter un constructeur prenant en entrée une liste
    public LightAdapter(List<Light> lightList, RoomViewFragment roomViewFragment) {
        this.lightList = lightList;
        lightViews = new ArrayList<>();
        this.roomViewFragment = roomViewFragment;
    }

    //cette fonction permet de créer les viewHolder
    //et par la même indiquer la vue à inflater (à partir des layout xml)
    @Override
    public LightViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.light_card_view,viewGroup,false);
        LightViewHolder newLightView = new LightViewHolder(view, roomViewFragment);
        lightViews.add(newLightView);
        return newLightView;
    }

    @Override
    public void onBindViewHolder(LightViewHolder lightViewHolder, int position) {
        Log.i("LIGHT_ADAPTER", "Binding view holder.");
        Light light = lightList.get(position);
        lightViewHolder.bind(light);
    }

    @Override
    public int getItemCount() {
        return lightList.size();
    }

    public List<LightViewHolder> getLightViews() {
        return lightViews;
    }
}
