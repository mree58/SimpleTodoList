package com.emrebaran.simpletodolist;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

/**
 * Created by alper on 03/11/16.
 */

public class AlarmReceiver extends BroadcastReceiver {


    NotificationManager notificationManager;
    Notification myNotification;
    private static int SIMPLE_NOTIFICATION_ID=1;

    @Override
    public void onReceive(Context context, Intent intent) {

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SIMPLE BIRTHDAY REMINDER");
        //Acquire the lock
        wl.acquire();

        MyNotification(context,context.getString(R.string.notify_todo1), context.getString(R.string.notify_todo2), context.getString(R.string.notify_todo3), R.mipmap.ic_launcher, R.mipmap.ic_launcher);
        //Release the lock
        wl.release();
    }


    public void MyNotification(Context context,String msg,String title, String text, int icon_small, int icon_big){

        Intent myIntent = new Intent(context, MainActivity.class);
        PendingIntent myPendingIntent = PendingIntent.getActivity(context, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        myNotification = new Notification.Builder(context)
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(msg)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(myPendingIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(icon_small)
                .setVibrate(new long[] { 1000, 1000})
                .build();

        myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

        myNotification.contentView.setImageViewResource(android.R.id.icon, icon_big);
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(SIMPLE_NOTIFICATION_ID, myNotification);

    }

    public void CancelAlarm(Context context, int alarm_id)
    {
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarm_id, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

    }
}
