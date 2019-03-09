package com.pixled.pixledandroid.welcome;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.pixled.pixledandroid.R;
import com.pixled.pixledandroid.deviceGroup.mainActivity.GroupSelectionActivity;
import com.pixled.pixledandroid.mqtt.MqttAndroidConnection;
import com.pixled.pixledandroid.mqtt.MqttAndroidConnectionImpl;
import com.pixled.pixledandroid.utils.ServerConfig;

import java.net.Inet4Address;
import java.net.InetAddress;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "LOADING";
    private static final String PREFERENCES = "server_config.xml";

    private SharedPreferences preferences;

    public static MqttAndroidConnection mqttAndroidConnection;

    private Button startButton;

    private Toolbar toolbar;

    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener discoveryListener;

    private boolean serverOk = false;
    private int serverPort;
    private InetAddress serverHost;

    private boolean brokerOk = false;
    private int brokerPort;
    private InetAddress brokerHost;

    private boolean forceDiscoveryStop = false;

    private boolean mqttConnectionOk = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.welcome_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome);

        // Set up tool bar
        toolbar = findViewById(R.id.welcomeToolbar);
        setSupportActionBar(toolbar);

        toolbar.inflateMenu(R.menu.welcome_menu);

        preferences = this.getSharedPreferences(PREFERENCES, 0);

        String server_ip = preferences.getString("server_ip", null);
        if (server_ip != null && server_ip.length() > 0) {
            Log.i(TAG, "Manually set up server ip found : " + server_ip);
            ServerConfig.setEndpoint(8080, server_ip);
            ServerConfig.setBrokerUri(1883, server_ip);
            initMqttConnection();
        }
        else {
            mNsdManager =  (NsdManager) getApplicationContext().getSystemService(Context.NSD_SERVICE);
            discoverServices();
        }



        startButton = findViewById(R.id.welcomeButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                launchGroupSelectionActivity();
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.set_server_ip:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // Get the layout inflater
                LayoutInflater inflater = this.getLayoutInflater();

                final View saveIpDialog = inflater.inflate(R.layout.save_ip_dialog, null);
                EditText ip_input = saveIpDialog.findViewById(R.id.input_server_ip);

                String server_ip = preferences.getString("server_ip", null);
                if (server_ip != null) {
                    ip_input.setText(server_ip);
                }
                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
                builder.setView(saveIpDialog)
                        // Add action buttons
                        .setPositiveButton(R.string.save_ip, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...

                                String server_ip = ip_input.getText().toString();
                                preferences.edit().putString("server_ip", server_ip).apply();
                                if (server_ip.length() > 0) {
                                    forceDiscoveryStop = true;
                                    if (mNsdManager != null) {
                                        // The discovery service had been started
                                        mNsdManager.stopServiceDiscovery(discoveryListener);
                                    }
                                    ServerConfig.setEndpoint(8080, server_ip);
                                    ServerConfig.setBrokerUri(1883, server_ip);
                                    initMqttConnection();
                                }
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        })
                        .show();

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
                if (serviceInfo.getHost() instanceof Inet4Address) {
                    Log.i(TAG, "Resolve Succeeded. " + serviceInfo);

                    NsdServiceInfo mService = serviceInfo;
                    serverPort = mService.getPort();
                    serverHost = mService.getHost();
                    serverOk = true;

                    ServerConfig.setEndpoint(serverPort, serverHost.getCanonicalHostName());

                    updateServerTaskStatus();
                    mNsdManager.stopServiceDiscovery(discoveryListener);
                }
                else {
                    Log.i(TAG, "Resolve Succeeded, but unsupported IPv6 : " + serviceInfo);
                }
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
                if (serviceInfo.getHost() instanceof Inet4Address) {
                    Log.i(TAG, "Resolve Succeeded. " + serviceInfo);

                    NsdServiceInfo mService = serviceInfo;
                    brokerPort = mService.getPort();
                    brokerHost = mService.getHost();
                    brokerOk = true;

                    ServerConfig.setBrokerUri(brokerPort, brokerHost.getCanonicalHostName());

                    updateBrokerTaskStatus();
                    mNsdManager.stopServiceDiscovery(discoveryListener);
                }
                else {
                    Log.i(TAG, "Resolve Succeeded, but unsupported IPv6 : " + serviceInfo);
                }
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
                if(!forceDiscoveryStop) {
                    if (!brokerOk) {
                        mNsdManager.discoverServices(
                                "_mqtt._tcp.", NsdManager.PROTOCOL_DNS_SD, discoveryListener);
                    }
                    else {
                        initMqttConnection();
                    }
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

                startButton.setVisibility(View.VISIBLE);
                launchGroupSelectionActivity();
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
