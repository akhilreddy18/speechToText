package com.example.speechtotext;

import android.content.ActivityNotFoundException;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.TextView;
import android.view.View;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private TextView textOutput;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textOutput= (TextView) findViewById(R.id.textOutput);
        client = new OkHttpClient();
    }


    public void onClick(View v)
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        try {
            startActivityForResult(intent, REQUEST_CODE);
        } catch (ActivityNotFoundException a) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(result.get(0).contains("on") && (result.get(0).contains("lights") || result.get(0).contains("light"))){
                        textOutput.setText(result.get(0));
                        Request request = new Request.Builder()
                                .url("http://192.168.1.237:1880/on")
                                .build();

                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                textOutput.setText("Failed to turn on the lights!");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful()) {
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textOutput.setText("Now turning on lights!" );
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else if(result.get(0).contains("off") && (result.get(0).contains("lights") || result.get(0).contains("light"))){
                        textOutput.setText(result.get(0));
                        Request request = new Request.Builder()
                                .url("http://192.168.1.237:1880/off")
                                .build();
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                textOutput.setText("Failed to turn off the lights!");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                if(response.isSuccessful()) {
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            textOutput.setText("Now turning off lights!" );
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else {
                        textOutput.setText(result.get(0));
                    }
                }
                break;
            }

        }
    }
}