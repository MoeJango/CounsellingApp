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

import java.io.IOException;
import java.util.ArrayList;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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
                if ((!sendText.toString().isEmpty()) && (sendText.toString().length() < 255)) {
                    insert(myID, receiverID, sendText.toString());
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

        messageAdapter = new MessageAdapter(this, messages, myID);
        chatRecyclerView.setAdapter(messageAdapter);

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
                    System.out.println();
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });
    }
}