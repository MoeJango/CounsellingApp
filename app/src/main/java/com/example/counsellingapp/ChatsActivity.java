package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

public class ChatsActivity extends AppCompatActivity {

    private LinearLayout scrollLinearLayout;
    private String userType;
    private String name;
    private String password;
    private boolean matched = false;

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
    }
}