package com.example.myapplication.Notification;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.net.Uri;
import android.os.Build;

public class OreNotification extends ContextWrapper {
    private static final String CHANNEL_ID="com.example.myapplication";
    private static final String CHANNEL_NAME="myapplication";
    private NotificationManager notificationManager;


    public OreNotification(Context base) {
        super(base);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createChannel();
        }

    }


    private void createChannel() {
        NotificationChannel channel= null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            channel = new NotificationChannel(CHANNEL_ID,CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(false);
            channel.enableVibration(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(channel);
        }

    }

    public NotificationManager getManager(){
        if(notificationManager == null){
            notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }


    public Notification.Builder getOreoNotification(String title, String body,
                                                    PendingIntent pendingIntent, Uri sounduri,String icon){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(Integer.parseInt(icon))
                    .setSound(sounduri)
                    .setAutoCancel(true);
        }
        return null;
    }


}
