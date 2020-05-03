package com.example.lab8;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText editText;
    public static SecretKey key;
    private String hmacSha256;
    private String hmac;
    private String extra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();
        KeyInit();
        ViewActionInit();
    }

    private void Init() {
        hmac = "hmac";
        extra = "extra";
        hmacSha256 = "HmacSha256";
        button = findViewById(R.id.button);
        editText = findViewById(R.id.textView2);
    }

    private void ViewActionInit() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                String someText = editText.getText().toString();
                intent.putExtra(extra, someText);
                try {
                    Mac mac = Mac.getInstance(hmacSha256);
                    mac.init(key);
                    byte[] macData = mac.doFinal(someText.getBytes());
                    intent.putExtra(hmac, macData);
                    startActivity(intent);
                } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void KeyInit() {
        try {
            key = KeyGenerator.getInstance("HmacSha256").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
