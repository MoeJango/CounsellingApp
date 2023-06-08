package com.example.counsellingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtName;
    private EditText edtPassword;
    private EditText edtPasswordConfirm;
    private Button registerButtonUser;
    private Button registerButtonCounsellor;
    private TextView error;
    private boolean userClicked = false;
    private boolean counsellorClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edtName = findViewById(R.id.editTextName);
        edtPassword = findViewById(R.id.editTextPassword);
        edtPasswordConfirm = findViewById(R.id.editTextPasswordConfirm);
        registerButtonUser = findViewById(R.id.cirRegisterButtonUser);
        registerButtonCounsellor = findViewById(R.id.cirRegisterButtonCounsellor);
        error = findViewById(R.id.textViewError);

        registerButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userClicked = true;
                String strName = edtName.getText().toString();
                String strPassword = edtPassword.getText().toString();
                String strPasswordConfirm  = edtPasswordConfirm.getText().toString();

                // Perform user registration
                startRegister(strName, strPassword, strPasswordConfirm);

            }
        });

        registerButtonCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counsellorClicked = true;
                String strName = edtName.getText().toString();
                String strPassword = edtPassword.getText().toString();
                String strPasswordConfirm  = edtPasswordConfirm.getText().toString();

                // Perform counsellor registration
                startRegister(strName, strPassword, strPasswordConfirm);

            }
        });
    }


    private void startRegister(String name, String password, String passwordConfirm) {
        if ((0 < name.length()&&name.length() <= 25) && (0 < password.length()&&password.length() <= 25)) {
            if (password.equals(passwordConfirm)) {
                error.setText("");
                String userType = "";
                if (userClicked) {
                    userType = "user";
                }
                if (counsellorClicked) {
                    userType = "counsellor";
                }
                register(name, password, userType);
            }
            else {
                error.setText("Passwords do not match");
                edtName.setText("");
                edtPassword.setText("");
                edtPasswordConfirm.setText("");
            }
        }
        else {
            if (name.length() > 25 || password.length() > 25) {
                error.setText("Name or password exceed the maximum length of 25 characters");
                edtName.setText("");
                edtPassword.setText("");
                edtPasswordConfirm.setText("");
            }
            if (name.length() == 0 || password.length() == 0) {
                error.setText("Name or password cannot be empty");
                edtName.setText("");
                edtPassword.setText("");
                edtPasswordConfirm.setText("");
            }
        }

    }


    private void register(String name, String password, String userType) {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("password", password)
                .add("tableName", userType+"s")
                .build();
        Request request = new Request.Builder()
                .url("https://lamp.ms.wits.ac.za/home/s2542012/register.php")
                .post(formBody)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();
                    System.out.println(responseBody);
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (responseBody.equals("error")) {
                                error.setText("An error has occurred. Please try again");
                                edtName.setText("");
                                edtPassword.setText("");
                                edtPasswordConfirm.setText("");
                            }
                            else {
                                Intent intent = new Intent(RegisterActivity.this, ChoicesActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("userType", userType);
                                bundle.putString("ID", responseBody);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }
                        }
                    });
                }
                else {
                    RegisterActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Error inserting record: " + response.message());
                            error.setText("An error has occurred. Please try again");
                            edtName.setText("");
                            edtPassword.setText("");
                            edtPasswordConfirm.setText("");
                        }
                    });
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


    public void onRegisterClick(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);
        finish();
    }

}