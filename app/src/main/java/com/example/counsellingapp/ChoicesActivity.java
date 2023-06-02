package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class ChoicesActivity extends AppCompatActivity {

    private Chip relChallenges;
    private Chip trauma;
    private Chip selfEsteem;
    private Chip motivation;
    private Chip conflict;
    private Chip life;
    private FloatingActionButton next;
    private TextView main;
    private TextView error;
    private ArrayList<String> issues = new ArrayList<>();
    private String tableName;
    OkHttpClient client = new OkHttpClient();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choices);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle bundle = getIntent().getExtras();
        relChallenges = findViewById(R.id.chipRelChallenges);
        trauma = findViewById(R.id.chipTrauma);
        selfEsteem = findViewById(R.id.chipSelfEsteem);
        motivation = findViewById(R.id.chipMotivation);
        conflict = findViewById(R.id.chipConflict);
        life = findViewById(R.id.chipLife);
        next = findViewById(R.id.nextButton);
        main = findViewById(R.id.textViewMain);
        error = findViewById(R.id.textViewNoChoices);

        if (bundle.getString("userType").equals("user")) {
            tableName = "users";
            main.setText("Select the issues that you struggle with");
        }
        if (bundle.getString("userType").equals("counsellor")) {
            tableName = "counsellors";
            main.setText("Select the issues that you are comfortable dealing with");
        }

        CompoundButton.OnCheckedChangeListener chipListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String issue = compoundButton.getText().toString();
                if (b) {
                    issues.add(issue);
                }
                else {
                    issues.remove(issue);
                }
            }
        };

        relChallenges.setOnCheckedChangeListener(chipListener);
        trauma.setOnCheckedChangeListener(chipListener);
        selfEsteem.setOnCheckedChangeListener(chipListener);
        motivation.setOnCheckedChangeListener(chipListener);
        conflict.setOnCheckedChangeListener(chipListener);
        life.setOnCheckedChangeListener(chipListener);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (issues.size() == 0) {
                    error.setText("Must select at least one issue before proceeding");
                }
                else {
                    error.setText("");
                    String name = bundle.getString("name");
                    String password = bundle.getString("password");
                    issues.sort(String::compareToIgnoreCase);
                    try {
                        process(name, password, issues.toString(), tableName);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });

    }

    private void process(String name, String password, String issues, String tableName) throws IOException {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("issues", issues)
                .add("tableName", tableName)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/register.php")
                .post(formBody)
                .build();

        //try (Response response = client.newCall(request).execute()) {
        //    if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        //}

    }


}