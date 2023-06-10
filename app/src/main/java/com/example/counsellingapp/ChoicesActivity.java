package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

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
            tableName = "user_issues";
            main.setText("Select the issues that you struggle with");
        }
        if (bundle.getString("userType").equals("counsellor")) {
            tableName = "counsellor_issues";
            main.setText("Select the issues that you are comfortable dealing with");
        }

        CompoundButton.OnCheckedChangeListener chipListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String issue = compoundButton.getText().toString();
                if (issue.equals("Self-esteem")) {
                    issue = issue.replace("-", "_");
                }
                else {
                    issue = issue.replace(" ", "_");
                }
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
                    String ID = bundle.getString("ID");
                    String userType = bundle.getString("userType");
                    try {
                        FormBody.Builder builder = new FormBody.Builder();
                        builder.add("id", ID);
                        builder.add("tableName", tableName);
                        for (int i=0; i<issues.size(); i++) {
                            builder.add("column"+i, issues.get(i));
                        }
                        RequestBody formBody = builder.build();
                        process(name, ID, userType, formBody);

                    } catch (IOException e) {
                        error.setText("An error has occurred. Please try again");
                        throw new RuntimeException(e);

                    }

                }
            }
        });
    }

    private void process(String name, String ID, String userType, RequestBody formBody) throws IOException {
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/issues.php")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    System.out.println(responseBody);
                    ChoicesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBody.equals("error")) {
                                error.setText("An error has occurred. Please try again");
                            }
                            else {
                                if (userType.equals("user")) {
                                    matchUser(name, ID, userType);
                                }
                                else if (userType.equals("counsellor")) {
                                    matchCounsellor(name, ID, userType);
                                }
                            }
                        }
                    });;
                } else {
                    System.out.println("Error inserting record: " + response.message());
                    error.setText("An error has occurred. Please try again");
                    throw new IOException(String.valueOf(response));
                }

                // Close the response
                response.close();
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });

    }


    private void matchUser(String name, String ID, String userType) {
        RequestBody formBody = new FormBody.Builder()
                .add("id", ID)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/matchUser.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    System.out.println(responseBody);
                    ChoicesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(responseBody);
                            Intent intent = new Intent(ChoicesActivity.this, ChatsListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("userType", userType);
                            bundle.putString("name", name);
                            bundle.putString("ID", ID);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    });
                } else {
                    System.out.println("Error matching: " + response.message());
                    error.setText("An error has occurred. Please try again");
                    throw new IOException(String.valueOf(response));
                }

                // Close the response
                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });

    }


    private void matchCounsellor(String name, String ID, String userType) {
        RequestBody formBody = new FormBody.Builder()
                .add("id", ID)
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/matchCounsellor.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    System.out.println(responseBody);
                    ChoicesActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println(responseBody);
                            Intent intent = new Intent(ChoicesActivity.this, ChatsListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("userType", userType);
                            bundle.putString("name", name);
                            bundle.putString("ID", ID);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            finish();
                        }
                    });
                } else {
                    System.out.println("Error matching: " + response.message());
                    error.setText("An error has occurred. Please try again");
                    throw new IOException(String.valueOf(response));
                }

                // Close the response
                response.close();
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }
        });

    }
}