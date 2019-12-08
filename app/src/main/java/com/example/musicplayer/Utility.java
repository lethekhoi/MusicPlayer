package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

public class Utility {
    public static final int NOTIFICATION_ID = 1;
    @SuppressWarnings("unused")
    public static NotificationManager mNotificationManager;

    public static void initNotification(String songTitle, Context mContext) {
        try {
            String ns = Context.NOTIFICATION_SERVICE;
            mNotificationManager = (NotificationManager) mContext.getSystemService(ns);
            int icon = R.drawable.music_showcase;
            CharSequence tickerText = "Playing your song";
            long when = System.currentTimeMillis();

            /*Notification notification = new Notification(stop,tickerText,when);
            notification.flags = Notification.FLAG_ONGOING_EVENT;*/
            Context context = mContext.getApplicationContext();
            CharSequence songName = "" + songTitle;

            Intent notificationIntent = new Intent(mContext, MainActivity.class);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            //From API 23+, setLastestEventInfo cant be used. Instead, use NotificationCompat.Builder
            NotificationCompat.Builder b = new NotificationCompat.Builder(context);
            b.setAutoCancel(true).setDefaults(Notification.FLAG_ONGOING_EVENT).setSmallIcon(icon).setTicker(tickerText).setWhen(when).setContentTitle(songName).setContentIntent(contentIntent);
            mNotificationManager.notify(NOTIFICATION_ID, b.build());
            /*notification.setLatestEventInfo();*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage = (((double) currentSeconds) / totalSeconds) * 100;
        return percentage.intValue();
    }

    public static int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double) progress) / 100) * totalDuration);
        return currentDuration * 1000;
    }

    public static String millSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) (milliseconds % (1000 * 60 * 60)) % (1000 * 60) / (1000);
        if (hours > 0) {
            finalTimerString = hours + ":";
        }
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        return finalTimerString;
    }
}
