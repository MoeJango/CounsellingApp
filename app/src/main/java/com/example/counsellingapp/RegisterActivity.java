package com.example.counsellingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends AppCompatActivity {
    private EditText name;
    private EditText password;
    private EditText passwordConfirm;
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

        name = findViewById(R.id.editTextName);
        password = findViewById(R.id.editTextPassword);
        passwordConfirm = findViewById(R.id.editTextPasswordConfirm);
        registerButtonUser = findViewById(R.id.cirRegisterButtonUser);
        registerButtonCounsellor = findViewById(R.id.cirRegisterButtonCounsellor);
        error = findViewById(R.id.textViewError);

        registerButtonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userClicked = true;
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordConfirm  = passwordConfirm.getText().toString();

                // Perform user registration
                register(strName, strPassword, strPasswordConfirm);
            }
        });

        registerButtonCounsellor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counsellorClicked = true;
                String strName = name.getText().toString();
                String strPassword = password.getText().toString();
                String strPasswordConfirm  = passwordConfirm.getText().toString();

                // Perform counsellor registration
                register(strName, strPassword, strPasswordConfirm);
            }
        });
    }

    private void register(String name, String password, String passwordConfirm) {
        if ((0 < name.length()&&name.length() <= 25) && (0 < password.length()&&password.length() <= 25)) {
            if (password.equals(passwordConfirm)) {
                String userType = "";
                if (userClicked) {
                    userType = "user";
                }
                if (counsellorClicked) {
                    userType = "counsellor";
                }
                Intent intent = new Intent(RegisterActivity.this, ChoicesActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userType", userType);
                bundle.putString("name", name);
                bundle.putString("password", password);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
            }
            else {
                error.setText("Passwords do not match");
                this.name.setText("");
                this.password.setText("");
                this.passwordConfirm.setText("");
            }
        }
        else {
            if (name.length() > 25 || password.length() > 25) {
                error.setText("Name or password exceed the maximum length of 25 characters");
                this.name.setText("");
                this.password.setText("");
                this.passwordConfirm.setText("");
            }
            if (name.length() == 0 || password.length() == 0) {
                error.setText("Name or password cannot be empty");
                this.name.setText("");
                this.password.setText("");
                this.passwordConfirm.setText("");
            }
        }

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