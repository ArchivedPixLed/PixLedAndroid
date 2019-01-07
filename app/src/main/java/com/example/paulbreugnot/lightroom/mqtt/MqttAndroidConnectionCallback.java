package com.example.paulbreugnot.lightroom.mqtt;

import android.util.Log;

import com.example.paulbreugnot.lightroom.light.LightAdapter;
import com.example.paulbreugnot.lightroom.light.LightViewHolder;
import com.example.paulbreugnot.lightroom.room.RoomPagerAdapter;
import com.example.paulbreugnot.lightroom.room.RoomSelectionActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MqttAndroidConnectionCallback implements MqttCallbackExtended {

    private RoomSelectionActivity roomSelectionActivity;
    private MqttAndroidClient mqttAndroidClient;

    public MqttAndroidConnectionCallback(RoomSelectionActivity roomSelectionActivity, MqttAndroidClient mqttAndroidClient) {
        this.roomSelectionActivity = roomSelectionActivity;
        this.mqttAndroidClient = mqttAndroidClient;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Long id = Long.valueOf(message.toString());
        Log.i("MQTT","Light " + id + " : " + topic);
        LightViewHolder lightView = roomSelectionActivity.getLightViewsIndex().get(id);
        LightAdapter lightAdapter = roomSelectionActivity.getLightAdapterIndex().get(lightView.getLight().getRoomId());
        if (lightView != null) {
            if (topic.equals(MqttAndroidConnection.connected_topic)) {
                lightView.getLight().setConnected(true);

            } else if (topic.equals(MqttAndroidConnection.disconnected_topic)) {
                lightView.getLight().setConnected(false);
            }
            lightAdapter.notifyItemChanged(lightView.getAdapterPosition());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        Log.i("MQTT", "Connection complete!");
        try {
            mqttAndroidClient.subscribe(MqttAndroidConnection.connected_topic, 1);
            mqttAndroidClient.subscribe(MqttAndroidConnection.disconnected_topic, 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
