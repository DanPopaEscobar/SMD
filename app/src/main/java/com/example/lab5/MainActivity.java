package com.example.lab5;

import androidx.appcompat.app.AppCompatActivity;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public class MainActivity extends AppCompatActivity {
    EditText url_input;
    TextView show_result;
    private View button;
    private Handler handler;
    private String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url_input = findViewById(R.id.url_input);
        show_result = findViewById(R.id.show_result);
        button = findViewById(R.id.get_result);
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message inputMessage) {
                String messageText = (String) inputMessage.obj;
                show_result.setText(messageText);
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String url = url_input.getText().toString();
                        Message message = handler.obtainMessage();
                        if(url != ""){
                            RetrofitWebSearch(url);
                            message.obj = msg;
                        }
                        message.sendToTarget();
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });
    }
    public void WebSearch(String baseURL){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String res = "";
        if (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(baseURL);
                connection = (HttpsURLConnection) url.openConnection();

                InputStream is = connection.getInputStream();
                ByteArrayOutputStream result = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) != -1) {
                    result.write(buffer, 0, length);
                }
                res = result.toString("UTF-8");
            } catch (IOException e) {
                res = e.toString();
                Log.e("Error", e.toString());
            } finally {
                if(connection != null) connection.disconnect();
            }
        }
        Display(res);

    }
    public void RetrofitWebSearch(String baseURL){
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() &&
                networkInfo.getType() == ConnectivityManager.TYPE_WIFI &&
                networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
            try {
                Retrofit retrofit = new Retrofit.Builder()
                        .addConverterFactory(GsonConverterFactory.create())
                        .baseUrl(baseURL)
                        .build();
                Service service = retrofit.create(Service.class);
                Call<ResponseBody> weatherCall = service.getStringResponse();
                weatherCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Display(response.toString());
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Display(t.toString());
                    }
                });
            } catch (Exception e) {
                Display("Base err: " + e.toString());

            }
            finally {
            }
        }
    }
    public void Display(String result){
        msg= result;

    }
    public interface Service {
        @GET("/")
        Call<ResponseBody> getStringResponse();
    }
}
