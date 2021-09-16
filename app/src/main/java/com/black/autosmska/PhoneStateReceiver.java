package com.black.autosmska;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.telephony.TelephonyManager;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.GregorianCalendar;

import static com.black.autosmska.MainActivity.db;

public class PhoneStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        try {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (number != null) {
                if (state.equalsIgnoreCase(TelephonyManager.EXTRA_STATE_RINGING)) {
                    int time_phone = 0, time_sms = 0, block_state = 0;
                    Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
                    if (query.moveToFirst()) {
                        do {
                            time_phone = Integer.parseInt(query.getString(1));
                            time_sms = Integer.parseInt(query.getString(2));
                            block_state = Integer.parseInt(query.getString(3));
                            break;
                        }
                        while (query.moveToNext());
                    }
                    if (block_state == 1) {
                        query = db.rawQuery("SELECT * FROM 'MainTable' WHERE phone LIKE '%" + number + "%';", null);
                        if (query.moveToFirst()) {
                            do {
                                if (query.getInt(4) == 1) {
                                    String[] phones = query.getString(1).split("\n");
                                    String[] smss = query.getString(2).split("\n");
                                    int check = Integer.parseInt(query.getString(3));
                                    String[] names = query.getString(5).split("\n");
                                    MainActivity.mainAct.cutTheCall(number, smss[check].substring(3), names[Arrays.asList(phones).indexOf(number)].substring(3), time_phone, time_sms);
                                }
                            }
                            while (query.moveToNext());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}