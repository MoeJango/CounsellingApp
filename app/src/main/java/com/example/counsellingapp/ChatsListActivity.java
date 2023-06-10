package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

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

        adapter = new UserAdapter(this, users);
        Snap


    }



    public void setUpUsers(ArrayList<User> users) {

    }
}