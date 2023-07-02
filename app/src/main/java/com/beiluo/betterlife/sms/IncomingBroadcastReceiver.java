package com.beiluo.betterlife.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class IncomingBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();
        try {
            if (null == bundle)
                return;
            final Object[] objects = (Object[]) bundle.get("pdus");
            for (int i = 0, size = objects.length; i < size; i++) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) objects[i]);
                String phoneNumber = smsMessage.getDisplayOriginatingAddress();
                String body = smsMessage.getMessageBody();

                Log.i("Receive", phoneNumber + " " + body);
            }
        } catch (Exception e) {
            Log.e("Receive", e.getMessage());
        }
    }
}