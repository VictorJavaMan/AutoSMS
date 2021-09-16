package com.black.autosmska;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.os.IBinder;
import android.widget.RemoteViews;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.res.ResourcesCompat;

import static com.black.autosmska.MainActivity.db;

public class SmsBlockService extends Service {

    private static final int NOTIF_ID = 1;
    private static final String NOTIF_CHANNEL_ID = "Channel_Id";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        final PendingIntent closeIntent = PendingIntent.getBroadcast(this, 0, new Intent("close_service"), 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground(pendingIntent, closeIntent);
        else
            startForeground(NOTIF_ID, new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID).setOngoing(true).setSmallIcon(R.drawable.ic_baseline_perm_phone_msg_24).setContentTitle(getString(R.string.app_name)).setContentText("Приложение запущено").setContentIntent(pendingIntent).setAutoCancel(true).build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground(PendingIntent pendingIntent, PendingIntent closeIntent){
        NotificationChannel chan = new NotificationChannel(NOTIF_CHANNEL_ID, "App", NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManagerCompat manager = NotificationManagerCompat.from(this);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIF_CHANNEL_ID);
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.layout_notification);
        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setTextViewText(R.id.title, getString(R.string.app_name));
        contentView.setOnClickPendingIntent(R.id.button_close, closeIntent);
        Cursor query = db.rawQuery("SELECT * FROM 'OptionsTable';", null);
        if (query.moveToFirst()) {
            do {
                contentView.setTextViewText(R.id.text, getString(R.string.status_notif) + " (" + ((query.getString(3).equals("1")) ? getString(R.string.block_on_notif) : getString(R.string.block_off_notif)) + ")");
                break;
            }
            while (query.moveToNext());
        }
        Notification notification = notificationBuilder.setOngoing(true).setSmallIcon(R.mipmap.ic_launcher).setContent(contentView).setPriority(NotificationCompat.PRIORITY_HIGH).setCategory(Notification.CATEGORY_SERVICE).setContentIntent(pendingIntent).setAutoCancel(true).build();
        startForeground(NOTIF_ID, notification);
    }
}