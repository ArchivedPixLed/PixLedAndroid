package com.pixled.pixledandroid.welcome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.deviceGroup.GroupSelectionActivity;

public class WelcomeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTitle(R.string.welcome);


        ActionBar actionBar = getActionBar();
        actionBar.hide();

        setContentView(R.layout.welcome);

        findViewById(R.id.welcomeButton).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchBuildingSelectionActivity();
            }
        });
    }

    protected void launchBuildingSelectionActivity(){
        Intent intent = new Intent(this, GroupSelectionActivity.class);
        startActivity(intent);
    }
}
