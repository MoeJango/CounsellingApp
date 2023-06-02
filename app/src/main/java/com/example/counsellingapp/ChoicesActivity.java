package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChoicesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}