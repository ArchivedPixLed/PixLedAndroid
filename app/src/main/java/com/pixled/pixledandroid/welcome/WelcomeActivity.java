package com.pixled.pixledandroid.welcome;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.deviceGroup.GroupSelectionActivity;
import com.pixled.pixledandroid.mqtt.MqttAndroidConnection;
import com.pixled.pixledandroid.mqtt.MqttAndroidConnectionImpl;
import com.pixled.pixledandroid.utils.ServerConfig;

import java.net.InetAddress;

public class WelcomeActivity extends Activity {

    private static final String TAG = "LOADING";

    public static MqttAndroidConnection mqttAndroidConnection;

    private Button startButton;

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener discoveryListener;

    private boolean serverOk = false;
    private int serverPort;
    private InetAddress serverHost;

    private boolean brokerOk = false;
    private int brokerPort;
    private InetAddress brokerHost;

    private boolean mqttConnectionOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTitle(R.string.welcome);


        ActionBar actionBar = getActionBar();
        actionBar.hide();

        setContentView(R.layout.welcome);


        mNsdManager =  (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);

        startButton = findViewById(R.id.welcomeButton);
        startButton.setEnabled(false);
        startButton.getBackground().setAlpha(100);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGroupSelectionActivity();
            }
        });

        discoverServices();
    }

    protected void launchGroupSelectionActivity(){
        Intent intent = new Intent(this, GroupSelectionActivity.class);
        startActivity(intent);
    }

    private void discoverServices(){

        NsdManager.ResolveListener serverResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve server failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Resolve Succeeded. " + serviceInfo);

                NsdServiceInfo mService = serviceInfo;
                serverPort = mService.getPort();
                serverHost = mService.getHost();
                serverOk = true;

                ServerConfig.setEndpoint(serverPort, serverHost);

                updateServerTaskStatus();
                mNsdManager.stopServiceDiscovery(discoveryListener);
            }
        };

        NsdManager.ResolveListener brokerResolveListener = new NsdManager.ResolveListener() {

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve MQTT broker failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.i(TAG, "Resolve Succeeded. " + serviceInfo);

                NsdServiceInfo mService = serviceInfo;
                brokerPort = mService.getPort();
                brokerHost = mService.getHost();
                brokerOk = true;

                ServerConfig.setBrokerUri(brokerPort, brokerHost);

                updateBrokerTaskStatus();
                mNsdManager.stopServiceDiscovery(discoveryListener);
                initMqttConnection();
            }
        };

        discoveryListener = initializeMdnsDiscoveryListener(
                serverResolveListener,
                brokerResolveListener
        );

        mNsdManager.discoverServices(
                "_http._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);

    }

    private NsdManager.DiscoveryListener initializeMdnsDiscoveryListener(
            NsdManager.ResolveListener serverResolveListener,
            NsdManager.ResolveListener brokerResolveListener) {

        // Instantiate a new DiscoveryListener
        return new NsdManager.DiscoveryListener() {

            // Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String regType) {
                Log.i(TAG, "Service discovery started.");
            }

            @Override
            public void onServiceFound(NsdServiceInfo service) {
                // A service was found! Do something with it.
                Log.i(TAG, "Service discovery success : " + service);
                if (service.getServiceType().equals("_http._tcp.")) {
                    if(!serverOk && service.getServiceName().equals("PixLedServer")) {
                        Log.i(TAG, "PixLedServer found, try to resolve");
                        mNsdManager.resolveService(service, serverResolveListener);
                    }
                }
                else if (service.getServiceType().equals("_mqtt._tcp.")) {
                    if(!brokerOk && service.getServiceName().equals("PixLedBroker")) {
                        Log.i(TAG, "PixLedBroker found, try to resolve");
                        mNsdManager.resolveService(service, brokerResolveListener);
                    }
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo service) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: " + service);
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(TAG, "Discovery stopped: " + serviceType);
                if (!brokerOk) {
                    mNsdManager.discoverServices(
                            "_mqtt._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
                }
            }

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }
        };
    }

    private void updateServerTaskStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView checkServer = findViewById(R.id.taskStatus);
                checkServer.setText(R.string.checkMqtt);
            }
        });
    }

    private void updateBrokerTaskStatus() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView checkBroker = findViewById(R.id.taskStatus);
                checkBroker.setText(R.string.connecting_mqtt);
            }
        });
    }

    private void initMqttConnection() {
        // Set up MQTT
        mqttAndroidConnection = new MqttAndroidConnectionImpl(this);
        mqttAndroidConnection.connect(this);
    }

    public void notifyMqttConnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView checkMqttConnection = findViewById(R.id.taskStatus);
                findViewById(R.id.taskView).setVisibility(View.GONE);

                findViewById(R.id.welcomeButton).setEnabled(true);
                startButton.getBackground().setAlpha(255);
            }
        });
    }

    public void notifyMqttConnectionFailed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView checkBroker = findViewById(R.id.taskStatus);
                checkBroker.setText(R.string.mqtt_connection_failed);
                checkBroker.setTextColor(getResources().getColor(R.color.device_disconnected));
                findViewById(R.id.welcomeProgressBar).setVisibility(View.GONE);
            }
        });
    }
}
