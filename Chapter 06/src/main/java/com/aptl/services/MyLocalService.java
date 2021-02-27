package com.aptl.services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

/**
 * @author Erik Hellman
 */
public class MyLocalService extends Service {
    private static final int NOTIFICATION_ID = 1001;
    private LocalBinder mLocalBinder = new LocalBinder();
    private Callback mCallback;

    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    public void performLongRunningOperation(MyComplexDataObject dataObject) {
        new MyAsyncTask().execute(dataObject);
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public class LocalBinder extends Binder {
        public MyLocalService getService() {
            return MyLocalService.this;
        }
    }

    public interface Callback {
        void onOperationProgress(int progress);
        void onOperationCompleted(MyComplexResult complexResult);
    }

    private final class MyAsyncTask extends AsyncTask<MyComplexDataObject, Integer, MyComplexResult> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            startForeground(NOTIFICATION_ID, buildNotification());
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if(mCallback != null && values.length > 0) {
                for (Integer value : values) {
                    mCallback.onOperationProgress(value);
                }
            }
        }

        @Override
        protected MyComplexResult doInBackground(MyComplexDataObject... myComplexDataObjects) {
            MyComplexResult complexResult = new MyComplexResult();
            // Actual operation left out for brevity...
            return complexResult;
        }

        @Override
        protected void onPostExecute(MyComplexResult myComplexResult) {
            if(mCallback != null ) {
                mCallback.onOperationCompleted(myComplexResult);
            }
            stopForeground(true);
        }

        @Override
        protected void onCancelled(MyComplexResult complexResult) {
            super.onCancelled(complexResult);
            stopForeground(true);
        }
    }

    private Notification buildNotification() {
        Notification notification = null;
        // Create a notification for the service..
        return notification;
    }
}
