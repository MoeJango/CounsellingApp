package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatActivity extends AppCompatActivity {

    String senderID;
    String receiverID;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        senderID = getIntent().getStringExtra("senderID");
        receiverID = getIntent().getStringExtra("receiverID");
        name = getIntent().getStringExtra("name");
    }
}