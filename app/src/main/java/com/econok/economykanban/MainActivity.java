package com.econok.economykanban;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private GoogleSignInClient mGoogleSignInClient;
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;
    private boolean doubleBackToExitPressedOnce = false;
    private Drawable profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirestore= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606138593322-qmo8r77q8faabttijt0tj9e6aiai0rtm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cardList = new ArrayList<>();
        cardList.add(new CardItem("Título 1", "Income", "Comida","342"));
        cardList.add(new CardItem("Título 2", "Expense", "Viaje","213"));
        cardList.add(new CardItem("Título 3", "Income", "Comida","534"));
        cardList.add(new CardItem("Título 4", "Expense", "Comida","432"));
        cardList.add(new CardItem("Título 5", "Income", "Viaje","123"));
        cardList.add(new CardItem("Titulo 6","Income","Comida","293"));

        adapter = new CardAdapter(this, cardList);
        recyclerView.setAdapter(adapter);

        // Obtener el usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Verificar si el usuario está autenticado
        if (currentUser != null) {
            // Obtener el nombre de usuario (este sería el nombre de la imagen en Storage)
            String userName = currentUser.getUid(); // o el campo que contenga el nombre único

            // Construir la referencia a la imagen en Firebase Storage
            StorageReference profileImageRef = FirebaseStorage.getInstance().getReference().child("perfil/").child(userName);

            // Descargar la imagen y establecerla como ícono del perfil en el menú
            profileImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Utilizar Glide u otra biblioteca para cargar la imagen desde la URL y establecerla en el ícono del menú
                    Glide.with(getApplicationContext())
                            .load(uri)
                            .circleCrop()
                            .into(new CustomTarget<Drawable>() {
                                @Override
                                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                    // Establecer la imagen como ícono del perfil en el menú
                                    profileImage=resource;
                                    invalidateOptionsMenu();
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                    // No es necesario hacer nada aquí
                                }
                            });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // Manejar el caso en el que no se pueda descargar la imagen
                    Log.e(TAG, "Error al descargar la imagen del perfil: " + e.getMessage());
                }
            });
        }
    }
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(MainActivity.this, "Presiona ATRÁS de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void irLogin(){
        Intent intent=new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }
}
