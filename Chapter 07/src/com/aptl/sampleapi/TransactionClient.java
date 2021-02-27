package com.aptl.sampleapi;

import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * @author Erik Hellman
 */
public class TransactionClient {

public String performCustomBinderTransacttion(IBinder binder, String arg0,
                                              int arg1, float arg2)
        throws RemoteException {
    Parcel request = Parcel.obtain();
    Parcel response = Parcel.obtain();

    // Populate request data...
    request.writeString(arg0);
    request.writeInt(arg1);
    request.writeFloat(arg2);

    // Perform
    boolean isOk = binder.transact(IBinder.FIRST_CALL_TRANSACTION, request, response, 0);

    String result = response.readString();

    request.recycle();
    response.recycle();

    return result;
}

}
