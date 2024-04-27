package com.econok.economykanban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;

public class Login extends AppCompatActivity {

    Button loginButton, registerButton, googleButton;
    private EditText pass,mail;
    private FirebaseAuth mAuth;
    //private Button forgetPass;

    //Message Resourse for the toast
    int MessageResourse1 = R.string.toast_fill_all_fields;
    int MessageResourse2 = R.string.toast_welcome;
    int MessageResourse3 = R.string.toast_incorrect;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton=findViewById(R.id.btn_login);
        mail=findViewById(R.id.email_editText);
        pass=findViewById(R.id.passwd_editText);
        mAuth=FirebaseAuth.getInstance();
        //forgetPass=findViewById(R.id.forgetPass);



      registerButton = findViewById(R.id.btn_register);
      registerButton.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              openSignup();
          }
      });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser=mail.getText().toString().trim();
                String passUser=pass.getText().toString().trim();

                if(emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(Login.this, getString(MessageResourse1), Toast.LENGTH_SHORT).show();
                }else{
                    loginUser(emailUser,passUser);
                }
            }
        });

    }//fin del OnCreate

    private void loginUser(String emailUser,String passUser){
        mAuth.signInWithEmailAndPassword(emailUser,passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    openMain();
                    Toast.makeText(Login.this, getString(MessageResourse2), Toast.LENGTH_SHORT).show();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                int FailColor = ContextCompat.getColor(Login.this,R.color.pastel_red);
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    pass.requestFocus();
                    pass.getBackground().setColorFilter(FailColor, PorterDuff.Mode.SRC_ATOP);
                    pass.setHintTextColor(Color.WHITE);
                    mail.requestFocus();
                    mail.getBackground().setColorFilter(FailColor, PorterDuff.Mode.SRC_ATOP);
                    mail.setHintTextColor(Color.WHITE);
                    Toast.makeText(Login.this, getString(MessageResourse3), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void openSignup() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
    public void openMain() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
    }
}