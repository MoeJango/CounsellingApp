package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    String myID;
    String receiverID;
    String receiverName;
    String myUserType;
    RecyclerView chatRecyclerView;
    ArrayList<Message> messages = new ArrayList<>();
    MessageAdapter messageAdapter;
    EditText sendText;
    ImageView sendButton;
    TextView textViewName;
    private CountDownLatch latch = new CountDownLatch(1);
    private static final int INITIAL_DELAY = 1;
    private static final int INTERVAL = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        myID = getIntent().getStringExtra("senderID");
        receiverID = getIntent().getStringExtra("receiverID");
        receiverName = getIntent().getStringExtra("name");
        myUserType = getIntent().getStringExtra("callerType");

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        sendText = findViewById(R.id.sendText);
        sendButton = findViewById(R.id.sendButton);
        textViewName = findViewById(R.id.textViewName);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((!sendText.getText().toString().isEmpty()) && (sendText.getText().toString().length() < 255)) {
                    insert(myID, receiverID, sendText.getText().toString());
                    messages.clear();
                    initialUpdateView();
                    sendText.setText("");
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    messageAdapter.notifyDataSetChanged();
                    System.out.println("Notify success");

                }
            }
        });


        if (myUserType.equals("user")) {
            textViewName.setText("Counsellor " + receiverName);
        }
        else {
            String patientNumber = getIntent().getStringExtra("patientNumber");
            textViewName.setText("Patient " + patientNumber);
        }

        initialUpdateView();
        try {
            latch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        messageAdapter = new MessageAdapter(this, messages, myID);
        chatRecyclerView.setAdapter(messageAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(this::scheduledUpdater, INITIAL_DELAY, INTERVAL, TimeUnit.SECONDS);
    }

    public void insert(String senderID, String receiverID, String message) {
        String key = senderID+"@"+receiverID;
        RequestBody formBody = new FormBody.Builder()
                .add("id", key)
                .add("message", message)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/insertMessage.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    throw new IOException();
                }
                else {
                    ChatActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println();
                        }
                    });
                }
                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }

    public void initialUpdateView() {
        String sentKey = myID+"@"+receiverID;
        String receivedKey = receiverID+"@"+myID;
        RequestBody formBody = new FormBody.Builder()
                .add("sent_id", sentKey)
                .add("received_id", receivedKey)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/checkMessages.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    throw new IOException();
                }
                else {
                    String responseBody = response.body().string();
                    if (!responseBody.equals("No messages")) {
                        ArrayList<String> messagesResponse = new ArrayList<>();
                        ArrayList<String> ids = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String message = jsonObject.getString("message");

                                ids.add(id);
                                messagesResponse.add(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i=0; i<messagesResponse.size(); i++) {
                            int index = ids.get(i).indexOf('@');
                            String senderID = ids.get(i).substring(0, index);
                            insertMessage(messagesResponse.get(i), senderID);
                            System.out.println("Insert success");
                        }
                    }
                }
                latch.countDown();
                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });

    }


    public void updateView() {
        String sentKey = myID+"@"+receiverID;
        String receivedKey = receiverID+"@"+myID;
        RequestBody formBody = new FormBody.Builder()
                .add("sent_id", sentKey)
                .add("received_id", receivedKey)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/checkMessages.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    System.out.println(response.message());
                    throw new IOException();
                }
                else {
                    String responseBody = response.body().string();
                    if (!responseBody.equals("No messages")) {
                        ArrayList<String> messagesResponse = new ArrayList<>();
                        ArrayList<String> ids = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(responseBody);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String message = jsonObject.getString("message");
                                ids.add(id);
                                messagesResponse.add(message);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        for (int i=0; i<messagesResponse.size(); i++) {
                            int index = ids.get(i).indexOf('@');
                            String senderID = ids.get(i).substring(0, index);
                            insertMessage(messagesResponse.get(i), senderID);
                        }
                    }
                }

                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }


    public void insertMessage(String message, String senderID) {
        Message messageNew = new Message(message, senderID);
        messages.add(messageNew);
    }

    public void scheduledUpdater() {
        messages.clear();
        updateView();
        messageAdapter.notifyDataSetChanged();
    }
}