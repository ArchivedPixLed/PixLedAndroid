package com.example.paulbreugnot.lightroom.mqtt;

import android.app.Activity;
import android.content.Context;

import com.example.paulbreugnot.lightroom.room.RoomViewFragment;

public interface MqttAndroidConnection {

    String connected_topic = "/connected";
    String disconnected_topic = "/disconnected";

    void connect(Context context);
}
