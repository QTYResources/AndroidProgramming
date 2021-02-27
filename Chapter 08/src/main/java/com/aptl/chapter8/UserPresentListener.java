package com.aptl.chapter8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author Erik Hellman
 */
public class UserPresentListener extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(Intent.ACTION_USER_PRESENT.equals(action)) {

        }
    }
}
