package com.aptl.chapter8;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MyActivity extends Activity {
    private ChargerConnecedListener mPowerConnectionReceiver;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        mPowerConnectionReceiver = new ChargerConnecedListener();
        registerReceiver(mPowerConnectionReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mPowerConnectionReceiver);
    }

public void enableBroadcastReceiver() {
    PackageManager packageManager = getPackageManager();
    packageManager.setComponentEnabledSetting(new ComponentName(this, ChargerConnecedListener.class),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
}

public void disableBroadcastReceiver() {
    PackageManager packageManager = getPackageManager();
    packageManager.setComponentEnabledSetting(new ComponentName(this, ChargerConnecedListener.class),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
}
}
