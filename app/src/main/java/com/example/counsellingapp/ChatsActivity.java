package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatsActivity extends AppCompatActivity {

    private LinearLayout scrollLinearLayout;
    private String userType;
    private String name;
    private String password;
    private boolean isMatching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        Bundle bundle = getIntent().getExtras();
        userType = bundle.getString("userType");
        name = bundle.getString("name");
        password = bundle.getString("password");
        scrollLinearLayout = findViewById(R.id.scrollLayout);

        if (userType.equals("user")) {
            startUserMatching(name, password);
        }
    }


    public void startUserMatching(String name, String password) {
        if (!isMatching) {
            isMatching = true;
            initiateUserMatching(name, password);
        }
    }


    private void initiateUserMatching(String name, String password) {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/matchUser.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // Handle the failure scenario
                isMatching = false;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    if (responseBody.equals("null")) {
                        // No counselor assigned, start periodic matching
                        //startPeriodicUserMatching(name, password);
                    } else {
                        // User already has a counselor, proceed as normal
                        String counselorId = responseBody;
                        // Proceed with further operations
                        // ...
                        isMatching = false;
                    }
                } else {
                    // Handle the unsuccessful response scenario
                    System.out.println("Request unsuccessful. Response code: " + response.code());
                    isMatching = false;
                }
                response.close();
            }
        });
    }
}