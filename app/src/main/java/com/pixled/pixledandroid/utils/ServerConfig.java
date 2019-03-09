package com.pixled.pixledandroid.utils;

import android.util.Log;

import java.net.InetAddress;

public abstract class ServerConfig {

    static final String TAG = "SERVER_CONFIG";

    // SPRING
    // public static String ENDPOINT = "http://10.0.1.86:8080/api/";
    public static String ENDPOINT;

    // MQTT
    // public static final String URI = "tcp://192.168.1.124:1883";
    public static String URI;

    public static void setEndpoint(int serverPort, String serverHost) {
        ENDPOINT = "http://" + serverHost + ":" + String.valueOf(serverPort) + "/api/";
        Log.i(TAG, "Server endpoint : " + ENDPOINT);
    }

    public static void setBrokerUri(int brokerPort, String brokerHost) {
        URI = "tcp://" + brokerHost + ":" + String.valueOf(brokerPort);
        Log.i(TAG, "Broker uri : " + URI);
    }
}
