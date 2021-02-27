package com.aptl.chapter8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 *
 * @author Erik Hellman
 */
public class NetworkListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

        } else if(WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            // Wi-Fi has been enabled, disabled, enabling, disabling, or unknown..
            int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE);
            // TODO Handle change in Wi-Fi setting..
        } else if(WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
                // TODO Handle change of Wi-Fi connectivity...
            } else if (networkInfo.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                // TODO Handle disconnection from Wi-Fi
            }
        }
    }
}
