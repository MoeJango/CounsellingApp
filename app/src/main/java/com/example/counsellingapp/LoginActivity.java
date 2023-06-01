package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private Button loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        name = findViewById(R.id.editTextName);
        password = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.cirLoginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();

                // Perform login authentication
                authenticateUser(strName, strPassword);
            }
        });
    }


    private void authenticateUser(String name, String password) {
        // Implement your authentication logic here
        // e.g., send the credentials to the server and check if they are valid

        // If the authentication is successful, proceed to the next activity
        //Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        //startActivity(intent);
        //overridePendingTransition(android.R.anim.slide_in_left,android.R.anim.slide_out_right);
        //finish();
    }


    public void onLoginClick(View view) {
    }


    @Override
    public void onBackPressed() {
        finish();
    }
}