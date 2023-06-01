package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private EditText passwordConfirm;
    private Button registerButtonUser;
    private Button registerButtonCounsellor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        name = findViewById(R.id.editTextName);
        password = findViewById(R.id.editTextPassword);
        passwordConfirm = findViewById(R.id.editTextPasswordConfirm);
        registerButtonUser = findViewById(R.id.cirRegisterButtonUser);
        registerButtonCounsellor = findViewById(R.id.cirRegisterButtonCounsellor);

        registerButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordConfirm  = passwordConfirm.getText().toString();

                // Perform user registration
                registerUser(strName, strPassword, strPasswordConfirm);
            }
        });

        registerButtonCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordConfirm  = passwordConfirm.getText().toString();

                // Perform counsellor registration
                registerUser(strName, strPassword, strPasswordConfirm);
            }
        });
    }

    private void registerUser(String name, String email, String password) {
        // Implement your registration logic here
        // e.g., send the user details to the server and save them in the database

        // After successful registration, you can proceed with the login process
        authenticateUser(email, password);
    }

    private void authenticateUser(String email, String password) {
        // Perform login authentication after registration
        // This can be the same logic as in the LoginActivity.authenticateUser()

        // If the authentication is successful, proceed to the next activity
        //Intent intent = new Intent(RegistrationActivity.this, HomeActivity.class);
        //startActivity(intent);
        //finish();
    }
}