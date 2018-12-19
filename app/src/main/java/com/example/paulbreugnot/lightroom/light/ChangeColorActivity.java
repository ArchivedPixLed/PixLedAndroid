package com.example.paulbreugnot.lightroom.light;

import android.app.Activity;
import android.os.Bundle;

import com.example.paulbreugnot.lightroom.R;

public class ChangeColorActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.change_light_color);

        setContentView(R.layout.change_light_color);
    }
}
