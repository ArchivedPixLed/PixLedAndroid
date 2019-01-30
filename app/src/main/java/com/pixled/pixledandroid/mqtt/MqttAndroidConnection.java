package com.pixled.pixledandroid.mqtt;

import android.content.Context;

public interface MqttAndroidConnection {

    String connected_topic = "/connected";
    String disconnected_topic = "/disconnected";

    void connect(Context context);
}
