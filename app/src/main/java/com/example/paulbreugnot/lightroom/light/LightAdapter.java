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

    /*
    This class is used to generate views corresponding to each light in the RecyclerView, from a
    list of lights.
     */

    private List<Light> lightList;
    private List<LightViewHolder> lightViews;
    private RoomViewFragment roomViewFragment;

    private boolean enableColorButton;

    public LightAdapter(List<Light> lightList, RoomViewFragment roomViewFragment, boolean enableColorButton) {
        this.lightList = lightList;
        lightViews = new ArrayList<>();
        this.roomViewFragment = roomViewFragment;
        this.enableColorButton = enableColorButton;
    }

    /*
    Automatically called to generated each light view from the light list.
     */
    @Override
    public LightViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.light_card_view,viewGroup,false);
        LightViewHolder newLightView = new LightViewHolder(view, roomViewFragment, enableColorButton);
        lightViews.add(newLightView);
        return newLightView;
    }

    /*
    Bind each list item with the corresponding view.
     */
    @Override
    public void onBindViewHolder(LightViewHolder lightViewHolder, int position) {
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
