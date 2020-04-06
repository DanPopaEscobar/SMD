package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION_CODES.O;

public class MainActivity extends AppCompatActivity {
    public static final String ACTION_Incoming = "com.example.lab3.action.IncomingReceiver";
    Button button;
    Button button2;
    Button button3;
    Button button4;
    TextView textView;
    TextView textView2;
    BoundService boundService;
    Intent intent = null;
    IntentFilter intentFilter= null;
    IncomingReceiver myIncomingReceiver= new IncomingReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindToService();
        intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_Incoming);
        intent = MyStartedService.getIntent(this, "hello");
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationService();
            }
        });
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationService();
            }
        });
        button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView2.setText(boundService.getCurrentDate());
            }
        });
        button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLuckyService();
            }
        });
        registerMyReceiver();

    }
    private void startLuckyService() {
        LuckyIntentService.startActionLucky(this);
    }
    private void startLocationService() {
        startService(intent);
    }
    private void stopLocationService() {
        stopService(intent);
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(myIncomingReceiver);
        serviceConnection = null;
        super.onDestroy();
    }
    private void bindToService() {
        if (boundService == null) {
            Intent intent = new Intent(this, BoundService.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        }
    }
    private void registerMyReceiver() {

        try
        {
            LocalBroadcastManager.getInstance(this).registerReceiver(myIncomingReceiver, intentFilter);
//            registerReceiver(myIncomingReceiver, intentFilter);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }

    private ServiceConnection serviceConnection =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            boundService = ((BoundService.MyStartedBinder) iBinder).get();
//            myStartedService.startLocationTracking(INTERVAL_SECONDS);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            boundService = null;
        }
    };
    public class IncomingReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("IncomingReceiver -"+ intent.getExtras());
            if (intent.getAction().equals(ACTION_Incoming)) {
                Bundle extras = intent.getExtras();
                Toast.makeText(context,"You won " + extras.getString("Number") + " euro",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
