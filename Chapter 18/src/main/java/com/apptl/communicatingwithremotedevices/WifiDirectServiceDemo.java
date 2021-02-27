package com.apptl.communicatingwithremotedevices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.app.Activity;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class WifiDirectServiceDemo extends Activity implements WifiP2pManager.ServiceResponseListener, WifiP2pManager.DnsSdServiceResponseListener, WifiP2pManager.DnsSdTxtRecordListener {

    private static final String TAG = "WifiDirectServiceDemo";
    private static final String SERVICE_NAME = "ServiceDemo";
    private WifiP2pManager mWifiP2pManager;
    private Looper mWFDLooper;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pDnsSdServiceInfo mServiceInfo;
    private WifiP2pDnsSdServiceRequest mServiceRequest;
    private MyWifiDirectReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(WifiP2pManager.
                WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.
                WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mReceiver = new MyWifiDirectReceiver();
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        mWifiP2pManager.cancelConnect(mChannel, null);
    }

    private void announceWiFiDirectService() {
        Log.d(TAG, "Setup service announcement!");
        mWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mWFDLooper = handlerThread.getLooper();
        mChannel = mWifiP2pManager.initialize(this, mWFDLooper,
                new WifiP2pManager.ChannelListener() {
                    @Override
                    public void onChannelDisconnected() {
                        Log.d(TAG, "onChannelDisconnected!");
                        mWFDLooper.quit();
                    }
                });
        Map<String, String> txtRecords = new HashMap<String, String>();
        mServiceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_NAME,
                "_http._tcp",
                txtRecords);
        mWifiP2pManager.addLocalService(mChannel, mServiceInfo,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Service announcing!");
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.d(TAG, "Service announcement failed: " + i);
                    }
                });
    }

    private void discoverWiFiDirectServices() {
        mWifiP2pManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        mWFDLooper = handlerThread.getLooper();
        mChannel = mWifiP2pManager.initialize(this, mWFDLooper,
                new WifiP2pManager.ChannelListener() {
                    @Override
                    public void onChannelDisconnected() {
                        Log.d(TAG, "onChannelDisconnected!");
                        mWFDLooper.quit();
                    }
                });
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance("_http._tcp");
        mWifiP2pManager.addServiceRequest(mChannel, mServiceRequest, null);
        mWifiP2pManager.setServiceResponseListener(mChannel, this);
        mWifiP2pManager.setDnsSdResponseListeners(mChannel, this, this);
        mWifiP2pManager.discoverPeers(mChannel,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Peer discovery started!");
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.d(TAG, "Peer discovery failed: " + i);
                    }
                });
        mWifiP2pManager.discoverServices(mChannel,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Log.d(TAG, "Service discovery started!");
                    }

                    @Override
                    public void onFailure(int i) {
                        Log.d(TAG, "Service discovery failed: " + i);
                    }
                });
    }

    @Override
    public void onServiceAvailable(int i, byte[] bytes, WifiP2pDevice wifiP2pDevice) {

    }

    @Override
    public void onDnsSdServiceAvailable(String instanceName,
                                        String registrationType,
                                        WifiP2pDevice srcDevice) {
        Log.d(TAG, "DNS-SD Service available: " + srcDevice);
        mWifiP2pManager.clearServiceRequests(mChannel, null);
        WifiP2pConfig wifiP2pConfig = new WifiP2pConfig();
        wifiP2pConfig.deviceAddress = srcDevice.deviceAddress;
        wifiP2pConfig.groupOwnerIntent = 0;
        mWifiP2pManager.connect(mChannel, wifiP2pConfig, null);
    }

    @Override
    public void onDnsSdTxtRecordAvailable(String s, Map<String, String> stringStringMap, WifiP2pDevice wifiP2pDevice) {

    }

    public class MyWifiDirectReceiver extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)
                    && mWifiP2pManager != null) {
                mWifiP2pManager.requestConnectionInfo(mChannel, this);
            }
        }

        @Override
        public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
            Log.d(TAG, "Group owner address: " + wifiP2pInfo.groupOwnerAddress);
            Log.d(TAG, "Am I group owner: " + wifiP2pInfo.isGroupOwner);
            if(!wifiP2pInfo.isGroupOwner) {
                connectToServer(wifiP2pInfo.groupOwnerAddress);
            }
        }
    }

    private void connectToServer(InetAddress groupOwnerAddress) {
        // TODO Connect to server...
    }
}
