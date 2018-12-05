package com.example.paulbreugnot.lightroom;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
// import android.view.Menu;

public class ContextManagementActivity extends Activity {

    private String room;
    private RoomContextState state;
    private RoomContextHttpManager roomContextHttpManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        roomContextHttpManager = new RoomContextHttpManager(this);

        setContentView(R.layout.activity_context_management);
        // initTextFieldListener();
    }

    public void setState(RoomContextState state){
        this.state = state;
    }

//    private void initTextFieldListener() {
//        ((Button) findViewById(R.id.buttonCheck)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                room = ((EditText) findViewById(R.id.editText1))
//                        .getText().toString();
//                roomContextHttpManager.retrieveRoomContextState(room);
//            }
//        });
//
//    }

    public void updateContextView() {
        if (this.state != null) {
            // contextView.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textViewLightValue)).setText(Integer
                    .toString(state.getLight()));
            ((TextView) findViewById(R.id.textViewNoiseValue)).setText(Float
                    .toString(state.getNoise()));

            ImageView image = findViewById(R.id.imageView1);

            if (state.getLightStatus().equals(RoomContextState.ON))
                image.setImageResource(R.drawable.ic_bulb_on);
            else
                image.setImageResource(R.drawable.ic_bulb_off);
        } else {
            initView();
        }
    }

    private void initView() {

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

}
