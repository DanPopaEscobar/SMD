package com.example.lab3;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Random;

import static android.os.Build.VERSION_CODES.O;

public class LuckyIntentService extends IntentService {
    Random rand = null;
    private static final String ACTION_Lucky = "com.example.lab3.action.Lucky";
    public LuckyIntentService() {
        super("LuckyIntentService");
        rand = new Random();
    }
    @Override
    public void onCreate() {
        Log.d("onCreate", Thread.currentThread().getName());
        if (isOreoOrHigher()) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);

        }
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", Thread.currentThread().getName());
        onHandleIntent(intent);
        return START_STICKY;
    }

    public static void startActionLucky(Context context) {
        Log.d("startActionLucky", Thread.currentThread().getName());
        Intent intent = new Intent(context, LuckyIntentService.class);
        intent.setAction(ACTION_Lucky);
        if (isOreoOrHigher()) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }
    private static Boolean isOreoOrHigher() {
        return Build.VERSION.SDK_INT >= O;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_Lucky.equals(action)) {
                handleActionLucky();
            }
        }
    }
    private void handleActionLucky() {
        Log.d("handleActionLucky", Thread.currentThread().getName());
        Integer i = rand.nextInt(10000);
        Log.d("ActionLucky", i.toString());
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_Incoming);
        intent.putExtra("Number", i.toString());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        //sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        Log.d("onDestroy", Thread.currentThread().getName());
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }
}
