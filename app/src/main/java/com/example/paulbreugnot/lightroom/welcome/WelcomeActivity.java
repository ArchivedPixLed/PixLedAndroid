package com.example.paulbreugnot.lightroom.welcome;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.paulbreugnot.lightroom.R;
import com.example.paulbreugnot.lightroom.building.BuildingSelectionActivity;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        findViewById(R.id.welcomeButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchBuildingSelectionActivity();
            }
        });
    }

    protected void launchBuildingSelectionActivity(){
        Intent intent = new Intent(this, BuildingSelectionActivity.class);
        startActivity(intent);
    }
}
