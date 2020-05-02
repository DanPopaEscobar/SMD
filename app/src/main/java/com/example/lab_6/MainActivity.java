package com.example.lab6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lab_6.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PERMISSION = 0;
    private Button downloadButton, loadButton;
    private ImageView imageView;
    private EditText urlEditText;
    private String Stype= "";
    private Spinner spinner;
    private SharedPreferences sharedChoice;
    public static final String sharedFile = "Lab6_Shared_Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        downloadButton = findViewById(R.id.downloadButton);
        loadButton = findViewById(R.id.loadButton);
        urlEditText = findViewById(R.id.urlEditText);
        imageView = findViewById(R.id.imageView);

        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.StorageSpinnerValues, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        setInitialSpinnerValue();
        spinner.setOnItemSelectedListener(this);
        sharedChoice = getSharedPreferences(sharedFile, 0);

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Stype = spinner.getSelectedItem().toString();
                Log.d("tag", "Storage type is " + Stype);
                if (Stype.equals("Undefined")) {
                    Toast.makeText(MainActivity.this, "Please select a storage location", Toast.LENGTH_SHORT).show();
                    return;
                }
                permissionCheck();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String URL = urlEditText.getText().toString();
                        if (URL != "") {
                            DownloadFromURL(URL, Stype);
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Stype = spinner.getSelectedItem().toString();
                Log.d("tag","Storage type is "+Stype);
                if (Stype.equals("Undefined")){
                    Toast.makeText(MainActivity.this, "Please select a storage location", Toast.LENGTH_SHORT).show();
                    return;
                }
                permissionCheck();
                String URL = urlEditText.getText().toString();
                if (URL != "") {
                    Load(URL, Stype);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
        SharedPreferences.Editor store = sharedChoice.edit();
        store.putString("0", text);
        store.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void setInitialSpinnerValue(){
        sharedChoice = getSharedPreferences(sharedFile, 0);
        String spinnerValue = sharedChoice.getString("0", "");
        int spinnerIndex = 0;
        switch (spinnerValue) {
            case "Internal":
                spinnerIndex = 0;
                break;
            case "External":
                spinnerIndex = 1;
                break;
            default:
                spinnerIndex = 2;
                break;
        }
        spinner.setSelection(spinnerIndex);
    }

    public void Load(String pastedURL, String storageType){
        try {
            String[] substr = pastedURL.split("/");
            String file_name = substr[substr.length - 1];
            File path = getFilesDir();
            if (storageType.equals("External"))
                path = Environment.getExternalStorageDirectory();
            Log.d("tag", path.toString());
            File imgFile = new File(path.toString() + File.separator + file_name);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }catch (Exception e) {
            Log.d("tag", e.toString());
            e.printStackTrace();
        }
    }

    public void DownloadFromURL(String pastedURL, String storageType) {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String[] substr = pastedURL.split("/");
        String file_name = substr[substr.length - 1];
        if (networkInfo != null && networkInfo.isConnected() &&
                (networkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                        networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(pastedURL);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();
                File path = getFilesDir();
                if (storageType.equals("External"))
                    path = Environment.getExternalStorageDirectory();
                Log.d("tag", path.toString());
                InputStream input = connection.getInputStream();
                OutputStream output = new FileOutputStream(path + File.separator + file_name);
                byte data[] = new byte[1024];
                int length;
                while ((length = input.read(data)) != -1) {
                    output.write(data, 0, length);
                }

            } catch (IOException e) {
                Log.d("tag", e.toString());
                e.printStackTrace();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission granted: " + PERMISSION, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission NOT granted: " + PERMISSION, Toast.LENGTH_SHORT).show();
                }

                return;
            }
        }
    }

    private void permissionCheck() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.INTERNET};
                requestPermissions(permissions, PERMISSION);
            }
        }
    }

    @Override
    public void onStop() {
        String text = spinner.getSelectedItem().toString();
        SharedPreferences.Editor store = sharedChoice.edit();
        store.putString("0",text);
        store.apply();
        super.onStop();
        Log.d("tag", text);
    }
}
