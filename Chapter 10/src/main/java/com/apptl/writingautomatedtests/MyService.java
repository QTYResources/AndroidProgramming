package com.apptl.writingautomatedtests;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author Erik Hellman
 */
public class MyService extends Service {
    private LocalBinder mLocalBinder = new LocalBinder();

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public class LocalBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }
}
