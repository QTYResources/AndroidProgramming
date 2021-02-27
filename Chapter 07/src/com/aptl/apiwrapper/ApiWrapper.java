package com.aptl.apiwrapper;

import com.aptl.sampleapi.AidlCallback;
import com.aptl.sampleapi.ApiInterfaceV1;
import com.aptl.sampleapi.ApiInterfaceV2;
import com.aptl.sampleapi.CustomData;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

/**
 * @author Erik Hellman
 */
public class ApiWrapper {
    private Context mContext;
    private ApiCallback mCallback;
    private MyServiceConnectionV1 mServiceConnection = new MyServiceConnectionV1();
    private ApiInterfaceV1 mServiceV1;

    public void release() {
        mContext.unbindService(mServiceConnection);
    }

    public ApiWrapper(Context context, ApiCallback callback) {
        mContext = context;
        mCallback = callback;
        Intent v1Intent = new Intent("com.aptl.sampleapi.AIDL_SERVICE");
        v1Intent.putExtra("version", 1);
        mContext.bindService(v1Intent,
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void getAllDataSince(long timestamp, CustomData[] result) {
        if (mServiceV1 != null) {
            try {
                mServiceV1.getAllDataSince(timestamp, result);
            } catch (RemoteException e) {
                // TODO Handle service error
            }
        }
    }

    void storeData(CustomData data) {
        if (mServiceV1 != null) {
            try {
                mServiceV1.storeData(data);
            } catch (RemoteException e) {
                // Handle service error
            }
        }
    }

    private class MyServiceConnectionV1 implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mServiceV1 = ApiInterfaceV1.Stub.asInterface(iBinder);
            try {
                mServiceV1.setCallback(mAidlCallback);
            } catch (RemoteException e) {
                // Handle service error...
            }

            mCallback.onApiReady(ApiWrapper.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mServiceV1 = null;
            if(mCallback != null) {
                mCallback.onApiLost();
            }
        }
    }

    private AidlCallback.Stub mAidlCallback = new AidlCallback.Stub() {
        @Override
        public void onDataUpdated(CustomData[] data) throws RemoteException {
            if(mCallback != null) {
                mCallback.onDataUpdated(data);
            }
        }
    };

    public interface ApiCallback {
        void onApiReady(ApiWrapper apiWrapper);

        void onApiLost();

        void onDataUpdated(CustomData[] data);
    }
}
