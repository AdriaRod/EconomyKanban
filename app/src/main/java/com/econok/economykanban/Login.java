package com.econok.economykanban;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    Button loginButton, registerButton, googleButton;
    private EditText pass,mail;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;

    private static final int RC_SIGN_IN = 123;
    //private Button forgetPass;

    //Message Resourse for the toast
    int MessageResourse1 = R.string.toast_fill_all_fields;
    int MessageResourse2 = R.string.toast_welcome;
    int MessageResourse3 = R.string.toast_incorrect;
    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirestore= FirebaseFirestore.getInstance();
        loginButton=findViewById(R.id.btn_login);
        mail=findViewById(R.id.email_editText);
        pass=findViewById(R.id.passwd_editText);
        mAuth=FirebaseAuth.getInstance();
        googleButton=findViewById(R.id.btn_google);
        //forgetPass=findViewById(R.id.forgetPass);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("159235896101-tbr4e83cc1tp77pnjj7er02gtm17ldc2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

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

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int MessageResourse5 = R.string.toast_login_error;

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(Login.this, getString(MessageResourse5), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        int MessageResourse6 = R.string.toast_sing_up_failed;
                        if (task.isSuccessful()) {
                            FirebaseUser user=mAuth.getCurrentUser();
                            Uri photoUrl=user.getPhotoUrl();

                            uploadProfileImage(user.getUid(),photoUrl,new OnSuccessListener<Uri>(){
                                public void onSuccess(Uri imageUrl){
                                    Map<String,Object> datosUsuario=new HashMap<>();
                                    datosUsuario.put("id",user.getUid());
                                    datosUsuario.put("correo",user.getEmail());
                                    datosUsuario.put("foto",imageUrl.toString());

                                    mFirestore.collection("usuarios").document(user.getUid()).set(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            openMain();
                                            // Sign in success, update UI with the signed-in user's information
                                            updateUI(user);
                                            Toast.makeText(Login.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Login.this, getString(MessageResourse6), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, getString(MessageResourse6), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void uploadProfileImage(String userId, Uri profileImageUri, OnSuccessListener<Uri> onSuccessListener) {
                        int MessageResourse7 = R.string.toast_error_uploading_file;
                        // Descargar la imagen desde la URL antes de cargarla en Firebase Storage
                        Glide.with(Login.this)
                                .asBitmap()
                                .load(profileImageUri)
                                .into(new SimpleTarget<Bitmap>() {
                                    @Override
                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                                        // Convertir el Bitmap a un ByteArrayOutputStream
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                                        // Crear un InputStream desde el ByteArrayOutputStream
                                        InputStream inputStream = new ByteArrayInputStream(baos.toByteArray());

                                        // Subir la imagen al Storage
                                        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
                                        StorageReference imageRef = storageRef.child("perfil/" + userId);

                                        imageRef.putStream(inputStream)
                                                .addOnSuccessListener(taskSnapshot -> {
                                                    // Obtener la URL de la imagen después de subirla
                                                    imageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.e("UploadError", getString(MessageResourse7), e);
                                                    Toast.makeText(Login.this, getString(MessageResourse7) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                });
                    }
                });
    }


    public void openSignup() {
        Intent intent = new Intent(Login.this, Register.class);
        startActivity(intent);
    }
    public void openMain() {
        Intent intent = new Intent(Login.this, Central.class);
        startActivity(intent);
    }
    private void updateUI(FirebaseUser user) {
        user=mAuth.getCurrentUser();
        if(user!=null){
            openMain();
        }
    }
}