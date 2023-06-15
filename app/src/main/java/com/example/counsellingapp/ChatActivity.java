package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
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
    private static final int INITIAL_DELAY = 5;
    private static final int INTERVAL = 5;


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
                if ((!sendText.getText().toString().isEmpty()) && (sendText.getText().toString().length() <= 255)) {
                    CountDownLatch latch = new CountDownLatch(1);
                    insert(myID, receiverID, sendText.getText().toString());
                    sendText.setText("");
                    messages.clear();
                    initialUpdateView(latch);
                    try {
                        latch.await();
                        messageAdapter.notifyDataSetChanged();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

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

        CountDownLatch latch = new CountDownLatch(1);
        initialUpdateView(latch);
        try {
            latch.await();
            messageAdapter = new MessageAdapter(this, messages, myID);
            chatRecyclerView.setAdapter(messageAdapter);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        /*
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        Handler handler = new Handler();
        CountDownLatch latchNew = new CountDownLatch(1);
        // refresh messages every 5 seconds
        ScheduledUpdater scheduledUpdater = new ScheduledUpdater(messageAdapter, handler, latchNew);
        executor.scheduleAtFixedRate(scheduledUpdater, INITIAL_DELAY, INTERVAL, TimeUnit.SECONDS);
        */
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
                    System.out.println("Insert into database success");
                }
                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }

    public void initialUpdateView(CountDownLatch latch) {
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
                    latch.countDown();
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
                            System.out.println("Insert into messages success");
                        }
                    }
                    else {

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


    public void scheduledUpdateView() {
        messages.clear();
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

    public class ScheduledUpdater implements Runnable{

        private MessageAdapter messageAdapter;
        private Handler handler;
        private CountDownLatch latch;

        public ScheduledUpdater(MessageAdapter messageAdapter, Handler handler, CountDownLatch latch) {
            this.messageAdapter = messageAdapter;
            this.handler = handler;
            this.latch = latch;
        }

        @Override
        public void run() {
            // Perform your task here
            scheduledUpdateView();
            latch.countDown();
            System.out.println("scheduledUpdateView Successful");

            // Wait for updateView() to complete before notifying data set changed
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                    System.out.println("scheduledNotifySet successful");
                }
            });

        }
    }
}