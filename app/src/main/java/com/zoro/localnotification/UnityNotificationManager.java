package com.zoro.localnotification;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerNativeActivity;

/**
 * Created by trong on 06/03/2016.
 */
public class UnityNotificationManager extends BroadcastReceiver {
    public static void SetNotification(int id, long delayMs, String title, String message, String ticker, int sound, int vibrate,
                                       int lights, String largeIconResource, String smallIconResource, int bgColor, int executeMode)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("l_icon", largeIconResource);
        intent.putExtra("s_icon", smallIconResource);
        intent.putExtra("activity", UnityPlayer.currentActivity.getClass().getName());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (executeMode == 2)
                am.setExactAndAllowWhileIdle(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
            else if (executeMode == 1)
                am.setExact(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
            else
                am.set(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        }
        else
            am.set(0, System.currentTimeMillis() + delayMs, PendingIntent.getBroadcast(currentActivity, id, intent, PendingIntent.FLAG_UPDATE_CURRENT));
    }

    public static void SetRepeatingNotification(int id, long delay, String title, String message, String ticker, long rep, int sound, int vibrate, int lights,
                                                String largeIconResource, String smallIconResource, int bgColor)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        intent.putExtra("ticker", ticker);
        intent.putExtra("title", title);
        intent.putExtra("message", message);
        intent.putExtra("id", id);
        intent.putExtra("color", bgColor);
        intent.putExtra("sound", sound == 1);
        intent.putExtra("vibrate", vibrate == 1);
        intent.putExtra("lights", lights == 1);
        intent.putExtra("l_icon", largeIconResource);
        intent.putExtra("s_icon", smallIconResource);
        intent.putExtra("activity", UnityPlayer.currentActivity.getClass().getName());
        am.setRepeating(0, System.currentTimeMillis() + delay, rep, PendingIntent.getBroadcast(currentActivity, id, intent, 0));
    }

    public void onReceive(Context context, Intent intent)
    {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        String ticker = intent.getStringExtra("ticker");
        String title = intent.getStringExtra("title");
        String message = intent.getStringExtra("message");
        String s_icon = intent.getStringExtra("s_icon");
        String l_icon = intent.getStringExtra("l_icon");
        int color = intent.getIntExtra("color", 0);
        String unityClass = intent.getStringExtra("activity");
        Boolean sound = intent.getBooleanExtra("sound", false);
        Boolean vibrate = intent.getBooleanExtra("vibrate", false);
        Boolean lights = intent.getBooleanExtra("lights", false);
        int id = intent.getIntExtra("id", 0);

        Resources res = context.getResources();

        Intent notificationIntent = new Intent(context,
                UnityPlayerNativeActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        //Notification.Builder builder = new Notification.Builder(context);

        builder.setContentIntent(contentIntent)
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            builder.setColor(color);

        if(ticker != null && ticker.length() > 0)
            builder.setTicker(ticker);

        if (s_icon != null && s_icon.length() > 0)
            builder.setSmallIcon(res.getIdentifier(s_icon, "drawable", context.getPackageName()));

        if (l_icon != null && l_icon.length() > 0)
            builder.setLargeIcon(BitmapFactory.decodeResource(res, res.getIdentifier(l_icon, "drawable", context.getPackageName())));

        if(sound)
            builder.setSound(RingtoneManager.getDefaultUri(2));

        if(vibrate)
            builder.setVibrate(new long[] {
                    1000L, 1000L
            });

        if(lights)
            builder.setLights(Color.GREEN, 3000, 3000);

        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }

    public static void CancelNotification(int id)
    {
        Activity currentActivity = UnityPlayer.currentActivity;
        AlarmManager am = (AlarmManager)currentActivity.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentActivity, UnityNotificationManager.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(currentActivity, id, intent, 0);
        am.cancel(pendingIntent);
    }

    public static void CancelAll(){
        NotificationManager notificationManager = (NotificationManager)UnityPlayer.currentActivity.getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
