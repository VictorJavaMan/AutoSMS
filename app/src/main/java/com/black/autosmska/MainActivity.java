package com.black.autosmska;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import net.sqlcipher.database.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.telecom.TelecomManager;
import android.telephony.SmsManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    public static final int PERMISSION_REQUEST_READ_PHONE_STATE = 155;
    public static int CT = 0;
    private NavController navController;
    public static SQLiteDatabase db;
    public static MainActivity mainAct;
    private static final int REQUEST_CODE_READ_CONTACTS = 133;
    private static boolean READ_CONTACTS_GRANTED = false;
    public static String key_split = "\n", key_empty = "#265@871#";
    public static BroadcastReceiver sendBroadcastReceiver, deliveryBroadcastReceiver, bReceiver;
    public EditText editText_main = null, editText_name = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                try {
                    if (intent.getAction().equals("close_service")) {
                        finish();
                        context.getApplicationContext().stopService(new Intent(context.getApplicationContext(), SmsBlockService.class));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        registerReceiver(bReceiver, new IntentFilter("close_service"));

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert_error_title)).setMessage(getString(R.string.alert_error_body)).setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            builder.create().show();
        }

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_listphones, R.id.navigation_addphone, R.id.navigation_story, R.id.navigation_options).build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
        PackageManager pm = getPackageManager();
        mainAct = this;
        fixBottomNavigationText(navView);
        boolean isTelephonySupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        boolean isGSMSupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM);

        if (isTelephonySupported && isGSMSupported) {
            //getBaseContext().deleteDatabase("app_.db");
            File dbfile = getDatabasePath("app_.db");
            dbfile.getParentFile().mkdirs();
            SQLiteDatabase.loadLibs(this);
            db = SQLiteDatabase.openOrCreateDatabase(dbfile.getPath(), "34j5jh345uyh34", null);
            db.execSQL("CREATE TABLE if not exists 'MainTable' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'phone' TEXT, 'sms' TEXT, 'chec' INTEGER, 'block' INTEGER, 'name' TEXT);");
            db.execSQL("CREATE TABLE if not exists 'HistoryTable' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'phone' TEXT, 'time' TEXT);");
            db.execSQL("CREATE TABLE if not exists 'OptionsTable' ('id' INTEGER PRIMARY KEY AUTOINCREMENT, 'time_phone' TEXT, 'time_sms' TEXT, 'block_state' TEXT);");
            Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
            if (!query.moveToFirst()) {
                db.execSQL("INSERT INTO 'OptionsTable' ('time_phone', 'time_sms', 'block_state') VALUES ('1000', '1000', '1');");
            }
            startService(new Intent(this, SmsBlockService.class));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.alert_error_title));
            builder.setMessage(getString(R.string.alert_error_body));
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            });
            builder.create().show();
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.ANSWER_PHONE_CALLS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ANSWER_PHONE_CALLS, Manifest.permission.READ_CALL_LOG, Manifest.permission.SEND_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_PHONE_NUMBERS, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE};
                requestPermissions(permissions, PERMISSION_REQUEST_READ_PHONE_STATE);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        android.os.Process.killProcess(android.os.Process.myPid());
        return super.onSupportNavigateUp();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v instanceof EditText) {
            menu.add(0, 1, 0, getString(R.string.add_from_contacts)).setActionView(v);
        }
        else if (v instanceof Button) {
            if (v.getTag() == null) {
                menu.add(0, 2, 0, getString(R.string.delete_all_text)).setActionView(v);
            }
            else {
                menu.add(0, 3, 0, getString(R.string.block_text)).setActionView(v);
                menu.add(0, 4, 0, getString(R.string.delete_all_text)).setActionView(v);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                addContactToEdit((EditText)item.getActionView(), (EditText)((LinearLayout)item.getActionView().getParent()).getChildAt(1));
                break;
            case 2:
                db.execSQL("DELETE FROM 'HistoryTable';");
                Toast.makeText(item.getActionView().getContext(), getString(R.string.toast_clean_story), Toast.LENGTH_SHORT).show();
                StoryFragment storyFragment = FragmentManager.findFragment(item.getActionView());
                storyFragment.refreshItems(storyFragment.requireView());
                break;
            case 3:
                Cursor query = db.rawQuery("SELECT block FROM 'MainTable' WHERE id = " + item.getActionView().getId() + ";", null);
                if (query.moveToFirst()) {
                    do {
                        db.execSQL("UPDATE 'MainTable' SET block = " + ((query.getInt(0) == 0) ? 1 : 0) + " WHERE id = " + item.getActionView().getId() + ";");
                    }
                    while (query.moveToNext());
                }
                ListPhonesFragment listPhonesFragment = FragmentManager.findFragment(item.getActionView());
                listPhonesFragment.refreshItems(listPhonesFragment.requireView());
                break;
            case 4:
                db.execSQL("DELETE FROM 'MainTable';");
                ListPhonesFragment listPhonesFragment_ = FragmentManager.findFragment(item.getActionView());
                listPhonesFragment_.refreshItems(listPhonesFragment_.requireView());
                break;
        }
        return super.onContextItemSelected(item);
    }

    public void addContactToEdit(EditText phone, EditText name) {
        int hasReadContactPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (hasReadContactPermission == PackageManager.PERMISSION_GRANTED) {
            READ_CONTACTS_GRANTED = true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, REQUEST_CODE_READ_CONTACTS);
        }
        if (READ_CONTACTS_GRANTED) {
            Intent contactPickerIntent = new Intent(Intent.ACTION_PICK);
            editText_main = null; editText_name = null;
            editText_main = phone;
            editText_name = name;
            contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
            startActivityForResult(contactPickerIntent, REQUEST_CODE_READ_CONTACTS);
        }
    }

    public boolean cutTheCall(String number, String sms, String name, int time_phone, int time_sms) {
        try {
            TelecomManager telecomManager = (TelecomManager) getApplicationContext().getSystemService(TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED || telecomManager == null) {
                return false;
            }
            if (telecomManager.isInCall()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    SystemClock.sleep(time_phone);
                    telecomManager.endCall();
                    Toast.makeText(getBaseContext(), getString(R.string.toast_reset_call), Toast.LENGTH_SHORT).show();
                    if (!sms.equals("") && !sms.equals(key_empty)) {
                        SystemClock.sleep(time_sms);
                        sendSMS(number, sms);
                    }
                    DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault());
                    String datetimeText = timeFormat.format(new Date());
                    db.execSQL("INSERT INTO 'HistoryTable' ('phone', 'time') VALUES ('" + name + "', '" + datetimeText + "');");
                    mainAct.addNotification(getString(R.string.toast_reset_call), name, sms);
                }
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE_READ_CONTACTS) {
                Uri contactData = data.getData();

                try (Cursor cur = getContentResolver().query(contactData, null, null, null, null)) {
                    if (cur.moveToFirst()) {
                        String str_phone = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[ -]", "");
                        String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        if (editText_main != null) {
                            editText_main.setText(str_phone);//.substring(str_phone.length() - 10));
                        }
                        if (editText_name != null) {
                            editText_name.setText(name);
                        }
                    }
                }
            }
        }
        catch (Exception e) {}
    }

    public void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        sendBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), getString(R.string.toast_send_sms), Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        break;
                }
            }
        };

        deliveryBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
            }
        };

        registerReceiver(sendBroadcastReceiver, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReceiver, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    @Override
    protected void onStop() {
        try {
            if (sendBroadcastReceiver != null && deliveryBroadcastReceiver != null && bReceiver != null) {
                unregisterReceiver(bReceiver);
                unregisterReceiver(sendBroadcastReceiver);
                unregisterReceiver(deliveryBroadcastReceiver);
            }
        }
        catch (Exception e) { }
        super.onStop();
    }

    public void addNotification(String title, String name, String msg) {
        NotificationChannel channel1 = new NotificationChannel("channel1", "Channel 1", NotificationManager.IMPORTANCE_HIGH);
        NotificationManager manager = this.getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel1);
        NotificationCompat.InboxStyle inStyle = (!msg.equals(key_empty)) ? new NotificationCompat.InboxStyle().addLine(getString(R.string.hint_phone_name) + ": " + name).addLine(getString(R.string.hint_sms) + ": " + msg) : new NotificationCompat.InboxStyle().addLine(getString(R.string.hint_phone_name) + ": " + name);
        Notification notification = new NotificationCompat.Builder(this, "channel1")
                .setSmallIcon(R.drawable.ic_baseline_perm_phone_msg_24)
                .setContentTitle(title)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroupSummary(false)
                .setGroup("App")
                .setStyle(inStyle)
                .build();

        NotificationManagerCompat.from(this).notify(2, notification);
    }

    private void fixBottomNavigationText(BottomNavigationView bottomNavigationView) {
        for (int i = 0; i < bottomNavigationView.getChildCount(); i++) {
            View item = bottomNavigationView.getChildAt(i);

            if (item instanceof BottomNavigationMenuView) {
                BottomNavigationMenuView menu = (BottomNavigationMenuView) item;

                for (int j = 0; j < menu.getChildCount(); j++) {
                    View menuItem = menu.getChildAt(j);

                    View small = menuItem.findViewById(com.google.android.material.R.id.smallLabel);
                    if (small instanceof TextView) {
                        ((TextView) small).setSingleLine(false);
                        ((TextView) small).setTextSize(12);
                        ((TextView) small).setGravity(Gravity.CENTER);
                    }
                    View large = menuItem.findViewById(com.google.android.material.R.id.largeLabel);
                    if (large instanceof TextView) {
                        ((TextView) large).setSingleLine(false);
                        ((TextView) large).setTextSize(12);
                        ((TextView) large).setGravity(Gravity.CENTER);
                    }
                }
            }
        }
    }
}