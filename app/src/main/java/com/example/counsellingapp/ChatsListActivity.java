package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatsListActivity extends AppCompatActivity {

    private RecyclerView userRecyclerView;
    private String userType;
    private String name;
    private String id;
    private boolean isMatched = false;
    private ArrayList<User> users = new ArrayList<>();
    private UserAdapter adapter;
    private static CountDownLatch latch = new CountDownLatch(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_list);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        userRecyclerView = findViewById(R.id.userRecyclerView);
        userRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Bundle bundle = getIntent().getExtras();
        userType = bundle.getString("userType");
        name = bundle.getString("name");
        id = bundle.getString("ID");

        setUpUsers();

        try {
            latch.await();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println(users.size());
        adapter = new UserAdapter(this, users, userType, id);
        userRecyclerView.setAdapter(adapter);

    }



    public void setUpUsers() {
        if (userType.equals("user")) {
            RequestBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2542012/checkForCounsellor.php")
                    .post(formBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        System.out.println(responseBody);
                        if (!responseBody.equals("No counsellor")) {
                             try {
                                 JSONObject jsonObject = new JSONObject(responseBody);
                                 String id = String.valueOf(jsonObject.getInt("counsellor_id"));
                                 String name = jsonObject.getString("counsellor_name");
                                 String userType = "counsellor";
                                 insertUser(name, id, userType);
                             } catch (JSONException e) {
                                 throw new RuntimeException(e);
                             }
                        }
                        else {
                            insertUser(null, "0", "empty");
                        }
                        latch.countDown();
                    }
                    else {
                        System.out.println(response.message());
                        throw new IOException();
                        }
                    }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });
        } else if (userType.equals("counsellor")) {
            RequestBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .build();
            Request request = new Request.Builder()
                    .url("https://lamp.ms.wits.ac.za/home/s2542012/checkForUsers.php")
                    .post(formBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        String responseBody = response.body().string();
                        System.out.println(responseBody);
                        if (!responseBody.equals("No users")) {
                            ArrayList<String> idList = new ArrayList<>();
                            ArrayList<String> nameList = new ArrayList<>();
                            try {
                                JSONArray jsonArray = new JSONArray(responseBody);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                                    String id = jsonObject.getString("id");
                                    String name = jsonObject.getString("name");

                                    idList.add(id);
                                    nameList.add(name);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            for (int i=0; i<idList.size(); i++) {
                                insertUser(nameList.get(i), idList.get(i), "user");
                            }

                        }
                        else {
                            insertUser(null, "0", "empty");
                        }
                        latch.countDown();
                    }
                    else {
                        System.out.println(response.message());
                        throw new IOException();
                    }
                }

                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {

                }
            });
        }
    }

    public void insertUser(String name, String id, String userType) {
        User user = new User(name, id, userType);
        users.add(user);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ChatsListActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        finish();
    }
}