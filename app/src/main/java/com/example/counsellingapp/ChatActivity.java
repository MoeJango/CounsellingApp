package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatActivity extends AppCompatActivity {

    String myID;
    String receiverID;
    String receiverName;
    String myUserType;
    RecyclerView chatRecyclerView;
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
        sendText = findViewById(R.id.sendText);
        sendButton = findViewById(R.id.sendButton);
        textViewName = findViewById(R.id.textViewName);

        if (myUserType.equals("user")) {
            textViewName.setText("Counsellor " + receiverName);
        }
        else {
            textViewName.setText("Patient ");
        }

    }
}