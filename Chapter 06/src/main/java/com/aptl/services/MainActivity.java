package com.aptl.services;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class MainActivity extends Activity
        implements ServiceConnection, MyLocalService.Callback {
    private MyLocalService mService;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent bindIntent = new Intent(this, MyLocalService.class);
        bindService(bindIntent, this, BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mService != null) {
            mService.setCallback(null); // Important to avoid memory leaks
            unbindService(this);
        }
    }

    public void onTriggerLongRunningOperation(View view) {
        if(mService != null) {
            mService.performLongRunningOperation(new MyComplexDataObject());
        }
    }

    @Override
    public void onOperationProgress(int progress) {
        // TODO Update user interface with progress..
    }

    @Override
    public void onOperationCompleted(MyComplexResult complexResult) {
        // TODO Show result to user...
    }

    @Override
    public void onServiceConnected(ComponentName componentName,
                                   IBinder iBinder) {
        mService = ((MyLocalService.LocalBinder) iBinder).getService();
        mService.setCallback(this);

        // Once we have a reference to the service, we can update the UI and
        // enable buttons that should otherwise be disabled.
        findViewById(R.id.trigger_operation_button).setEnabled(true);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        // Disable the button as we are loosing the reference to the service.
        findViewById(R.id.trigger_operation_button).setEnabled(false);
        mService = null;
    }
}
