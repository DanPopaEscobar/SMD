package com.example.lab3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Random;

import static android.content.ContentValues.TAG;

public class MyStartedService extends Service {
    Random rand = null;
    private static final String EXTRA_INFO = "myStartedService.info";
    public MyStartedService() {
        rand = new Random();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("onStartCommand", Thread.currentThread().getName());
        RandomMsg();
        return START_NOT_STICKY;
    }

    private void RandomMsg() {
        Toast.makeText(this, "Bits saved: " + rand.nextInt(1000), Toast.LENGTH_LONG).show();
    }

    public static Intent getIntent(Context context, String info) {
        Intent intent = new Intent(context, MyStartedService.class);
        intent.putExtra(EXTRA_INFO, info);
        return intent;
    }
    @Override
    public void onCreate() {
        Log.d("onCreate", Thread.currentThread().getName());
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind", Thread.currentThread().getName());
        return null;
    }
    @Override
    public void onDestroy() {
        Log.d("onDestroy", Thread.currentThread().getName());
        stopSelf();
        super.onDestroy();
    }
}
