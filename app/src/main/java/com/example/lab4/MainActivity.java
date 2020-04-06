package com.example.lab4;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Random rand = null;
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayList=new ArrayList<String>();
    private static final String TAG = "Main";
    private Integer progress = 0;
    private static final int TIMEOUT_MS = 100;
    Button button;
    TextView textView;
    ListView listView;
    ProgressBar progressBar;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        rand = new Random();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, arrayList);
        listView.setAdapter(adapter);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                progress++;
                progressBar.setProgress(progress);
                JSONObject messageText = (JSONObject) inputMessage.obj;
                Integer pr = 0;
                try {
                    pr = messageText.getInt("Progress");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(pr == 100) {
                    try {
                        End(messageText.getString("ThreadNr"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Progress();
                Integer iterator = 0;
                while(iterator < 10){
                    Runnable runnable =new Runnable() {
                        @Override
                        public void run() {
                            Log.d("onClick", "In a different thread " + Thread.currentThread().getName());
                            Integer progress = 0;
                            SystemClock.sleep((rand.nextInt(TIMEOUT_MS)+1));
                            while (progress <= 100){
                                Message message = handler.obtainMessage();
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("ThreadNr", Thread.currentThread().getName().substring(Thread.currentThread().getName().length()-1));
                                    json.put("Progress", progress);
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                message.obj = json;
                                message.sendToTarget();
                                progress++;
                            }
                        }
                    };
                    Thread thread = new Thread(runnable);
                    thread.start();
                    iterator++;
                }
            }
        });
    }
    private void Progress(){
        Toast.makeText(this, "Downloading",Toast.LENGTH_SHORT).show();
    }
    private void End(String i){
        Toast.makeText(this, "Image "+ i +" Downloaded",Toast.LENGTH_SHORT).show();
        arrayList.add("Image " + i + ": done");
        adapter.notifyDataSetChanged();
    }
}
