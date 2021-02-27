package com.apptl.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

/**
 * @author Erik Hellman
 */
public class MySmsReceiver extends BroadcastReceiver {
    // Hidden constant from Telephony.java
    public static final String SMS_RECEIVED_ACTION
            = "android.provider.Telephony.SMS_RECEIVED";

    public static final String MESSAGE_SERVICE_NUMBER = "+461234567890";
    private static final String MESSAGE_SERVICE_PREFIX = "MYSERVICE";

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (SMS_RECEIVED_ACTION.equals(action)) {
            // “pdus” is the hidden key for the SMS data
            Object[] messages =
                    (Object[]) intent.getSerializableExtra("pdus");
            for (Object message : messages) {
                byte[] messageData = (byte[]) message;
                SmsMessage smsMessage =
                        SmsMessage.createFromPdu(messageData);
                processSms(smsMessage);
            }
        }
    }

    private void processSms(SmsMessage smsMessage) {
        String from = smsMessage.getOriginatingAddress();
        if (MESSAGE_SERVICE_NUMBER.equals(from)) {
            String messageBody = smsMessage.getMessageBody();
            if (messageBody.startsWith(MESSAGE_SERVICE_PREFIX)) {
                // TODO: Message verified - start processing...
            }
        }
    }
}
