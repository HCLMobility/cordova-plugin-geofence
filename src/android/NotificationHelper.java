package com.cowbell.cordova.geofence;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.TaskStackBuilder;

class NotificationHelper extends ContextWrapper {

    Context context;
    public NotificationHelper(Context base) {
        super(base);
        context = base;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);
    }

    public void sendHighPriorityNotification(com.cowbell.cordova.geofence.Notification notify) {

        notify.setContext(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setVibrate(notify.getVibrate())
                .setSmallIcon(notify.getSmallIcon())
                .setLargeIcon(notify.getLargeIcon())
                .setAutoCancel(true)
                .setContentTitle(notify.getTitle())
                .setContentText(notify.getText())
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notify.openAppOnClick) {
            String packageName = context.getPackageName();
            Intent resultIntent = context.getPackageManager()
                    .getLaunchIntentForPackage(packageName);

            if (notify.data != null) {
                resultIntent.putExtra("geofence.notification.data", notify.getDataJson());
            }

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
                    notify.id, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
        }

        NotificationManagerCompat.from(this).notify(notify.id, mBuilder.build());

    }

}
