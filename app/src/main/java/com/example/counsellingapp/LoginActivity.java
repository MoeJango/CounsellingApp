package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class LoginActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private Button loginButtonUser;
    private Button loginButtonCounsellor;
    private TextView error;
    private boolean userClicked = false;
    private boolean counsellorClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        name = findViewById(R.id.editTextName);
        password = findViewById(R.id.editTextPassword);
        error = findViewById(R.id.textViewError);
        loginButtonUser = findViewById(R.id.cirLoginButtonUser);
        loginButtonCounsellor = findViewById(R.id.cirLoginButtonCounsellor);

        loginButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userClicked = true;
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();

                // Perform login authentication
                authenticateUser(strName, strPassword);
            }
        });

        loginButtonCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counsellorClicked = true;
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();

                // Perform login authentication
                authenticateUser(strName, strPassword);
            }
        });
    }


    private void authenticateUser(String name, String password) {
        String userType = "";
        if (userClicked) {
            userType = "user";
        }
        if (counsellorClicked) {
            userType = "counsellor";
        }

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("tableName", userType+"s")
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/login.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        String finalUserType = userType;
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    error.setText("An error has occurred. Please try again");
                    throw new IOException("Unexpected code " + response);
                }

                // Read data on the worker thread
                else {
                    final String responseData = response.body().string();

                    // Run view-related code back on the main thread
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseData.equals("No matching records found")) {
                                error.setText("Account doesn't exist");
                            } else if (responseData.equals("error")) {
                                error.setText("An error has occurred. Please try again");
                            } else {
                                System.out.println(responseData);
                                Intent intent = new Intent(LoginActivity.this, ChatsActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("userType", finalUserType);
                                bundle.putString("id", responseData);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }


    public void onLoginClick(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        finish();
    }
}