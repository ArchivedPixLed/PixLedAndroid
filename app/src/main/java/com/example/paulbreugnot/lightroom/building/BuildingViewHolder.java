package com.example.paulbreugnot.lightroom.building;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.paulbreugnot.lightroom.R;

public class BuildingViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewView;
    // private ImageView imageView;

    //itemView est la vue correspondante à 1 cellule
    public BuildingViewHolder(View itemView) {
        super(itemView);

        //c'est ici que l'on fait nos findView

        textViewView = (TextView) itemView.findViewById(R.id.text);
        // imageView = (ImageView) itemView.findViewById(R.id.image);
    }

    //puis ajouter une fonction pour remplir la cellule en fonction d'un MyObject
    public void bind(Building building){
        textViewView.setText(building.getName());
        // Picasso.with(imageView.getContext()).load(myObject.getImageUrl()).centerCrop().fit().into(imageView);
    }
}
