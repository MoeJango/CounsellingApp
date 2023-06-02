package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.service.controls.actions.FloatAction;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

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
            main.setText("Select the issues that you struggle with");
        }
        if (bundle.getString("userType").equals("counsellor")) {
            main.setText("Select the issues that you are comfortable dealing with");
        }

        View.OnClickListener chipListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Chip c = (Chip) v;
                String issue = c.getText().toString();
                issues.add(issue);
            }
        };

        relChallenges.setOnClickListener(chipListener);
        trauma.setOnClickListener(chipListener);
        selfEsteem.setOnClickListener(chipListener);
        motivation.setOnClickListener(chipListener);
        conflict.setOnClickListener(chipListener);
        life.setOnClickListener(chipListener);
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
                    process(name, password, issues.toString());
                }
            }
        });

    }

    private void process(String name, String password, String issues) {

    }


}