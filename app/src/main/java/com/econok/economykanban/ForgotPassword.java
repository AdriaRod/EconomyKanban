package com.econok.economykanban;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ForgotPassword extends AppCompatActivity {
    private EditText emailInput;
    private Button resetPasswordButton,login;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private String email;
    private boolean existe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        mFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        emailInput = findViewById(R.id.editText_ForgotMail);
        resetPasswordButton = findViewById(R.id.btn_send_recovery);
        login=findViewById(R.id.btn_return_login);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                if (!email.isEmpty() && correo_en_uso(email)==true) {
                    // Implemetar logica de la contraseña
                    resetPassword();
                } else if (correo_en_uso(email)==false) {
                    new AlertDialog.Builder(ForgotPassword.this)
                            .setMessage("Este correo electrónico no existe en nuestra base de datos, por favor proporcione un correo válido o registrese?")
                            .setNegativeButton("Aceptar", null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                } else {
                    Toast.makeText(ForgotPassword.this, "Por favor, introduce tu email", Toast.LENGTH_SHORT).show();
                }
            }
        });


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void resetPassword(){
        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(ForgotPassword.this, "Enlace de nueva contraseña enviado", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Manejar el fallo de enviar el correo de restablecimiento de contraseña
                Toast.makeText(ForgotPassword.this, "Error al enviar el correo de restablecimiento de contraseña", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean correo_en_uso(String email){
        mFirestore.collection("usuarios").whereEqualTo("correo",email).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    existe=true;
                }
                else {
                    existe=false;
                }
            }
        });
        return existe;
    }

    public void openLogin() {
        Intent intent = new Intent(ForgotPassword.this, Login.class);
        startActivity(intent);
    }
}