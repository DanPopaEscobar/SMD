package com.example.lab3;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

public class BoundService extends Service {
    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm");
    public BoundService() {
    }
    @Override
    public void onCreate() {
        Log.d("onCreate", Thread.currentThread().getName());
    }
    @Override
    public boolean onUnbind (Intent intent) {
        Log.d("onUnbind", Thread.currentThread().getName());
        // TODO: Return the communication channel to the service.
        return true;
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("onBind", Thread.currentThread().getName());
        // TODO: Return the communication channel to the service.
        return binder;
    }
    @Override
    public void onDestroy() {
        Log.d("onDestroy", Thread.currentThread().getName());
        stopSelf();
        super.onDestroy();
    }
    public String getCurrentDate(){
        Date date = new Date();
        return formatter.format(date);
    }
    private MyStartedBinder binder = new MyStartedBinder();
    class MyStartedBinder extends Binder {
        private BoundService boundService = BoundService.this;
        public BoundService get() {
            return boundService;
        }
    }
}
