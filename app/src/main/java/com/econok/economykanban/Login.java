package com.econok.economykanban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    Button loginButton, registerButton, googleButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

      registerButton = findViewById(R.id.btn_register);
      registerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              openSignup();
          }
      });

    }//fin del OnCreate

    public void openSignup() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
    public void openMain(View v) {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
}