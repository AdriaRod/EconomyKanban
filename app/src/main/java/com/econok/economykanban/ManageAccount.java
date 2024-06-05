package com.econok.economykanban;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ManageAccount extends AppCompatActivity {

    private Button btn_done;
    private TextView logout,borrar,mail,cambiar_pass;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    private ImageView user2;
    private UCrop.Options options;
    private Drawable persona;
    private static final int REQUEST_GALLERY_PERMISSION=2020;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manage_account);

        logout = findViewById(R.id.logout);
        borrar=findViewById(R.id.delete);
        mail=findViewById(R.id.mail_locked);
        cambiar_pass=findViewById(R.id.cambiar_pass);
        user2 = findViewById(R.id.picture);
        options = new UCrop.Options();
        user2.setScaleType(ImageView.ScaleType.FIT_XY);
        options.setCircleDimmedLayer(true);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);

        int idImagenPredeterminada = getResources().getIdentifier("profile_picture_settings", "drawable", getPackageName());

        if (idImagenPredeterminada != 0) {
            persona = getResources().getDrawable(idImagenPredeterminada);
        } else {
            Toast.makeText(this, "Imagen no dispobible", Toast.LENGTH_SHORT).show();
        }

        user2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, REQUEST_GALLERY_PERMISSION);
            }
        });

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance());


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("159235896101-tbr4e83cc1tp77pnjj7er02gtm17ldc2.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btn_done = findViewById(R.id.btn_done);
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                irASettingsFragment();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ManageAccount.this)
                        .setTitle("Cerrar sesion")
                        .setMessage("¿Esta seguro que desea cerrar la sesion actual?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logout();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        borrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ManageAccount.this)
                        .setTitle("Borrar cuenta")
                        .setMessage("¿Esta seguro que desea borrar la cuenta? Esta acción es irreversible")
                        .setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                borrarUsuario();
                            }
                        })
                        .setNegativeButton("No", null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        cambiar_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarContraseña();
            }
        });

    }//fin del onCreate

    private void logout() {
        mAuth.signOut();
        mGoogleSignInClient.signOut();
        Intent i = this.getPackageManager().getLaunchIntentForPackage(this.getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        irLogin();
    }

    private void borrarUsuario(){
        FirebaseUser user=mAuth.getCurrentUser();
        String idUser=user.getUid();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference imageRef = storageRef.child("perfil/" + idUser);

        imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "User image deleted.");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d(TAG, "Error deleting user image: ", exception);
            }
        });


        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "User account deleted.");
                }
            }
        });

        mFirestore.collection("usuarios").document(idUser)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ManageAccount.this,"Usuario eliminado con exito", Toast.LENGTH_SHORT).show();
                        logout();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageAccount.this,"Error al eliminar usuario", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void irLogin(){
        Intent intent=new Intent(ManageAccount.this, Login.class);
        startActivity(intent);
    }

    protected void onStart() {
        super.onStart();
        FirebaseUser Fuser = mAuth.getCurrentUser();
        if (Fuser == null) {
            irLogin();
        } else {
            cargarCorreo();
            if(Fuser.getPhotoUrl()!=null){
                loadFirebaseImage(Fuser.getPhotoUrl());
            }else {
                Uri imagenUri=obtenerNuevaImagenSeleccionada();
                if(imagenUri!=null){
                    cargarNuevaImagen(imagenUri);
                }else {
                    if(Fuser.getPhotoUrl()!=null){
                        loadFirebaseImage(Fuser.getPhotoUrl());
                    }
                }
            }

            // Aquí es donde añades el código para descargar la imagen de Firebase Storage
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference pathReference = storageRef.child("perfil/"+Fuser.getUid());

            File localFile = null;
            try {
                localFile = File.createTempFile("images", "jpg");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            File finalLocalFile = localFile;
            pathReference.getFile(localFile)
                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            // El archivo se ha descargado con éxito
                            Uri downloadedImageUri = Uri.fromFile(finalLocalFile);
                            cargarNuevaImagen(downloadedImageUri);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // El archivo no se ha descargado correctamente
                        }
                    });
        }
    }


    private void cargarNuevaImagen(Uri imagenUri) {
        Glide.with(this)
                .load(imagenUri)
                .circleCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(user2);
        Glide.get(getApplicationContext()).clearMemory();
    }


    private Uri obtenerNuevaImagenSeleccionada() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String imagenUriStr = sharedPref.getString("imagenUri", null);
        if (imagenUriStr != null) {
            return Uri.parse(imagenUriStr);
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        options.setCircleDimmedLayer(true);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_GALLERY_PERMISSION) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    user2.setImageURI(imageUri);

                    Uri destinoUri = Uri.fromFile(new File(getCacheDir(), "imagenRecortada"));
                    UCrop.of(imageUri, destinoUri).withOptions(options).start(this);
                } else {
                    // Puede que haya casos donde la imagenUri sea nula, debes manejarlo adecuadamente
                    Toast.makeText(this, "Error al obtener la imagen de la camara", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            // Manejar el error del recorte
            final Throwable cropError = UCrop.getError(data);
            if (cropError != null) {
                Log.e("UCrop", "Error cropping the image", cropError);
                Toast.makeText(this, "Error al recortar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);

        if (resultUri != null) {
            Glide.with(this)
                    .load(resultUri)
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .skipMemoryCache(true)
                    .into(user2);

            cambiarFotoPerfil(resultUri);
        } else {
            Toast.makeText(this, "Error al obtener la imagen recortada", Toast.LENGTH_SHORT).show();
        }
    }



    private void subirFoto(Uri resultUri) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        StorageReference storageRef = storage.getReference();
        StorageReference fotoRef = storageRef.child("perfil/" + userId);

        // Eliminar la foto anterior
        fotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("Delete image", "Successful deletion");

                // Subir la nueva foto después de eliminar la anterior
                UploadTask uploadTask = fotoRef.putFile(resultUri);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        fotoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String fotoUrl = uri.toString();
                                Map<String, Object> datosUsuario = new HashMap<>();
                                datosUsuario.put("foto", fotoUrl);

                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                if (currentUser != null && mFirestore!=null) {
                                    mFirestore.collection("usuarios").document(currentUser.getUid()).update(datosUsuario).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(ManageAccount.this, "Foto guardada correctamente", Toast.LENGTH_SHORT).show();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                    .setPhotoUri(Uri.parse(fotoUrl)) // Aquí debes poner la URL de la nueva foto
                                                    .build();

                                            currentUser.updateProfile(profileUpdates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.d(TAG, "User profile updated.");
                                                            }
                                                        }
                                                    });

                                            Intent refresh = new Intent(ManageAccount.this, ManageAccount.class);
                                            finish(); // Finaliza la actividad actual
                                            startActivity(refresh); // Inicia la misma actividad
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ManageAccount.this, "Error al actualizar la foto", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ManageAccount.this, "Error al subir la nueva foto", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Delete Image", "Error deleting image:" + e.getMessage());
                Toast.makeText(ManageAccount.this, "Error al eliminar la foto anterior", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void cambiarFotoPerfil(Uri nuevaImagenUri) {
        subirFoto(nuevaImagenUri);
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null){
            guardarImagen(nuevaImagenUri,currentUser.getUid());
        }
    }

    private void guardarImagen(Uri imagenUri,String userId) {
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser!=null && currentUser.getUid().equals(userId)) {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("imagenUri", imagenUri.toString());
            editor.apply();
        }
    }

    private void loadFirebaseImage(Uri photoUrl){
        Glide.with(this).load(photoUrl).circleCrop().diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(user2);
    }


    private void irASettingsFragment() {
        Intent intent = new Intent(this, Central.class);
        intent.putExtra("openSettings", true);
        startActivity(intent);
    }

    private void cargarCorreo(){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseFirestore mFirestore = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();


        String userEmail = currentUser.getEmail(); // Obtener el correo del usuario autenticado
        mFirestore.collection("usuarios")
                .whereEqualTo("correo", userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                String correo = document.getString("correo");
                                mail.setText(correo);
                                Log.d(TAG, "Correo del usuario: " + correo);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void recuperarContraseña() {
        Intent intent = new Intent(ManageAccount.this, ForgotPassword.class);
        startActivity(intent);
    }
}