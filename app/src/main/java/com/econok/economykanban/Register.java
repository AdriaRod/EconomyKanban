package com.econok.economykanban;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    boolean existe;
    private Button botonregistro,googleR;
    //private SignInButton googleR;
    private EditText correo,password,rpassword;
    private FirebaseFirestore mFirestore;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 444;
    private GoogleSignInClient mGoogleSignInClient;

    private Button btn_register, btn_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseApp.initializeApp(this);
        mFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        correo=findViewById(R.id.email_editText);
        password=findViewById(R.id.passwd_editText);
        rpassword=findViewById(R.id.confirm_passwd_editText);
        botonregistro=findViewById(R.id.btn_register);
        googleR=findViewById(R.id.buttonWithIcon);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("159235896101-tbr4e83cc1tp77pnjj7er02gtm17ldc2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        btn_login = findViewById(R.id.btn_login);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        googleR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        botonregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailUser=correo.getText().toString().trim();
                String passUser=password.getText().toString().trim();
                String rpassUser=rpassword.getText().toString().trim();

                // Mensajes de los Toast se tienen que guardar de esta manera para que puedan ser mostrados en formato de String (para que se puedan traducir)
                int messageResourse1 = R.string.toast_fill_all_fields;
                int messageResourse2 = R.string.toast_short_password;

                if(emailUser.isEmpty() || passUser.isEmpty()){
                    Toast.makeText(Register.this,getString(messageResourse1),Toast.LENGTH_SHORT).show();
                } else if (passUser.length()<6) {
                    Toast.makeText(Register.this, getString(messageResourse2), Toast.LENGTH_SHORT).show();
                }
                else if(passUser.equals(rpassUser)==false){
                    Toast.makeText(Register.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                } else if (correo_en_uso(emailUser)==false) {
                    Toast.makeText(Register.this, "El correco electronico proporcionado ya existe", Toast.LENGTH_SHORT).show();
                } else {
                    registerUser(emailUser, passUser);
                }
            }
        });

    }

    private void registerUser(/*String nameUser,*/ String emailUser, String passUser) {
        mAuth.createUserWithEmailAndPassword(emailUser, passUser).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String id = user.getUid();

                    // Guardar la imagen predeterminada en Firebase Storage
                    String imageUrl = obtenerUrlImagenPredeterminada();
                    uploadProfileImage(id, Uri.parse(imageUrl), new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // Aquí puedes realizar cualquier acción adicional después de subir la imagen.
                            // Por ejemplo, guardar otros datos del usuario en Firestore.
                            Map<String, Object> map = new HashMap<>();
                            map.put("id", id);
                            map.put("correo", emailUser);
                            map.put("foto", uri.toString());

                            mFirestore.collection("usuarios").document(id).set(map)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            irMain();
                                            Toast.makeText(Register.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            int MessageResourse3 = R.string.toast_fail_save_user;
                                            Toast.makeText(Register.this, getString(MessageResourse3), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
                } else {
                    // Toast.makeText(getActivity(), "Error al registrar 1", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if(e instanceof FirebaseAuthUserCollisionException){
                    int MessageResourse4 = R.string.toast_email_already_use;
                    Toast.makeText(Register.this, getString(MessageResourse4), Toast.LENGTH_SHORT).show();
                }
                else {
                    //Toast.makeText(getActivity(), "Error al registrar 2", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Register.this, getString(MessageResourse5), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
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
                                            irMain();
                                            // Sign in success, update UI with the signed-in user's information
                                            updateUI(user);
                                            Toast.makeText(Register.this, "Bienvenido", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Register.this, getString(MessageResourse6), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, getString(MessageResourse6), Toast.LENGTH_SHORT).show();
                        }
                    }

                    private void uploadProfileImage(String userId, Uri profileImageUri, OnSuccessListener<Uri> onSuccessListener) {
                        int MessageResourse7 = R.string.toast_error_uploading_file;
                        // Descargar la imagen desde la URL antes de cargarla en Firebase Storage
                        Glide.with(Register.this)
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
                                                    Toast.makeText(Register.this, getString(MessageResourse7) + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }

                                });
                    }
                });
    }

    private void uploadProfileImage(String userId, Uri profileImageUri, OnSuccessListener<Uri> onSuccessListener) {
        int MessageResourse8 = R.string.toast_error_uploading_file;
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imageRef = storageRef.child("perfil/" + userId);

        // Subir la imagen al Storage
        imageRef.putFile(profileImageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    // Obtener la URL de la imagen después de subirla
                    imageRef.getDownloadUrl().addOnSuccessListener(onSuccessListener);
                })
                .addOnFailureListener(e -> {
                    Log.e("UploadError", getString(MessageResourse8), e);
                    Toast.makeText(Register.this, getString(MessageResourse8) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String obtenerUrlImagenPredeterminada() {
        Uri imagenPredeterminadaUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.blank)
                + '/' + getResources().getResourceTypeName(R.drawable.blank)
                + '/' + getResources().getResourceEntryName(R.drawable.blank));

        return imagenPredeterminadaUri.toString();
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

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }

    private void updateUI(FirebaseUser user) {
        user=mAuth.getCurrentUser();
        if(user!=null){
            irMain();
        }
    }

    //CAMBIAR CLASE
    private void irMain() {
        Intent intent=new Intent(this, Central.class);
        startActivity(intent);
    }

    public void openLogin() {
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
    }
}