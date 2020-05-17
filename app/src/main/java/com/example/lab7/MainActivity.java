package com.example.lab7;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class MainActivity extends AppCompatActivity {
    private KeyguardManager _keyguardManager;
    private CryptographySecurity _cryptography;
    private Button _save, _show;
    private EditText textView2,textView1;
    private TextView textView;
    private SharedPreferences _sharedPreferences;
    private String fileName = "Lab7_SecretPasswordFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _sharedPreferences = getSharedPreferences(fileName,0);
        VisualInit();
        KeyguardInit();
        CryptoInit();
        VisualActionInit();
    }

    private void CryptoInit() {
        try {
            _cryptography = new CryptographySecurity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void VisualActionInit() {
        _save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Save();
                } catch (Exception e) {
                    Log.d("TAG",e.toString());
                    e.printStackTrace();
                }
            }
        });
        _show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Show();
                } catch (Exception e) {
                    Log.d("TAG",e.toString());
                    e.printStackTrace();
                }
            }
        });
    }

    private void Save() throws IllegalBlockSizeException, InvalidKeyException, KeyStoreException, BadPaddingException {
        String pass = textView2.getText().toString();
        String desc = textView1.getText().toString();
        if(!TextUtils.isEmpty(pass)){
            SharedPreferences.Editor store = _sharedPreferences.edit();
            String encodedValue = _cryptography.Encrypt("Pass: " + pass +"; Description: " + desc+".\n");
            Integer size =_sharedPreferences.getAll().size();
            store.putString(size.toString(),encodedValue);
            store.apply();
        }else{
            Toast.makeText(MainActivity.this,"Please enter a Password",Toast.LENGTH_SHORT).show();
        }
    }

    public void Show()throws BadPaddingException, IllegalBlockSizeException, KeyStoreException, InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException{
        Map<String, ?> ecrypted = _sharedPreferences.getAll();
        String decrypted = "";
        for (Map.Entry<String, ?> entry: ecrypted.entrySet()) {
            decrypted += "Decrypted Data ("+entry.getKey()+") : " +_cryptography.Decrypt(entry.getValue().toString());
        }
        textView.setText(decrypted);
    }

    private void VisualInit() {
        _save = findViewById(R.id.button);
        _show = findViewById(R.id.button2);
        textView2 = findViewById(R.id.textView2);
        textView1 = findViewById(R.id.textView1);
        textView = findViewById(R.id.textView);
    }

    private void KeyguardInit(){
        try {
            _keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (!_keyguardManager.isDeviceSecure()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("There isn't a password set on this device.\n" +
                        "You must set a protection mechanism.\n" +
                        "Do you want to set up one now?")
                        .setPositiveButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        })
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD));
                            }
                        });
                Dialog dialog = builder.create();
                dialog.show();
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
}

    @Override
    protected void onResume() {
        KeyguardInit();
        super.onResume();
    }
}
