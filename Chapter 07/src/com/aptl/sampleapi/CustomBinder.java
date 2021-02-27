package com.aptl.sampleapi;

import android.os.Binder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * @author Erik Hellman
 */
public class CustomBinder extends Binder {

    @Override
    protected boolean onTransact(int code, Parcel request, Parcel response, int flags) throws RemoteException {
        // Read the data in the request
        String arg0 = request.readString();
        int arg1 = request.readInt();
        float arg2 = request.readFloat();

        String result = buildResult(arg0, arg1, arg2);

        // Write the result to the response Parcel
        response.writeString(result);

        // Return true on success
        return true;
    }

    private String buildResult(String arg0, int arg1, float arg2) {
        String result = null;
        // TODO Build the result
        return result;
    }
}
