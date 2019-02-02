package com.pixled.pixledandroid.welcome;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;

public class MqttConnectionStatusHandler implements IMqttActionListener {

    private WelcomeActivity welcomeActivity;

    public MqttConnectionStatusHandler(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }

    @Override
    public void onSuccess(IMqttToken asyncActionToken) {
        welcomeActivity.notifyMqttConnected();
    }

    @Override
    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
        welcomeActivity.notifyMqttConnectionFailed();
    }
}
