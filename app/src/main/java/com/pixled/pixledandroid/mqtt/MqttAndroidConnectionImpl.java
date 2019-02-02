package com.pixled.pixledandroid.mqtt;

import android.content.Context;
import android.util.Log;

import com.pixled.pixledandroid.deviceGroup.GroupSelectionActivity;
import com.pixled.pixledandroid.utils.ServerConfig;
import com.pixled.pixledandroid.welcome.MqttConnectionStatusHandler;
import com.pixled.pixledandroid.welcome.WelcomeActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttAndroidConnectionImpl implements  MqttAndroidConnection {

    private MqttAndroidClient mqttAndroidClient;

    private static final String clientId = "PixLedAndroid";

    private GroupSelectionActivity groupSelectionActivity;
    private WelcomeActivity welcomeActivity;

    public MqttAndroidConnectionImpl(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    public void setGroupSelectionActivity(GroupSelectionActivity groupSelectionActivity) {
        this.groupSelectionActivity = groupSelectionActivity;
    }

    @Override
    public void connect(Context context) {
        mqttAndroidClient = new MqttAndroidClient(context, ServerConfig.URI, clientId);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        mqttAndroidClient.setCallback(new MqttAndroidConnectionCallback(groupSelectionActivity, mqttAndroidClient));
        Log.i("MQTT", "Connecting...");
        try {
            mqttAndroidClient.connect(options, context, new MqttConnectionStatusHandler(welcomeActivity));

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
