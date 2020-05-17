package com.example.lab8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Mac;

public class SecondActivity extends AppCompatActivity {
    TextView textView ;
    private String extra;
    private String hmac;
    byte[] receivedMacData;
    private String hmacSha256;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Init();
        Intent intent = getIntent();
        String extra = intent.getStringExtra(this.extra);
        textView.setText(extra);
        HMAC_Init(intent, extra);
    }

    private void HMAC_Init(Intent intent, String extra) {
        receivedMacData = intent.getByteArrayExtra(hmac);
        try {
            Mac mac = Mac.getInstance(hmacSha256);
            mac.init(MainActivity.key);
            byte[] computedMacData = mac.doFinal(extra.getBytes());
            if (Arrays.equals(receivedMacData, computedMacData)) {
                textView.append("\n");
                textView.append("Data is unmodified");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
    }

    private void Init() {
        hmac = "hmac";
        this.extra = "extra";
        hmacSha256 = "HmacSha256";
        textView = findViewById(R.id.textView);
    }
}
