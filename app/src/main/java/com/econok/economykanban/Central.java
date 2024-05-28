package com.econok.economykanban;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.econok.economykanban.fragments.CustomViewPager;
import com.econok.economykanban.fragments.SectionsPagerAdapter;
import com.econok.economykanban.fragments.TransactionsFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.fragment.app.FragmentManager;



public class Central extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private MenuItem prevMenuItem;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private Drawable profileImage;
    private GoogleSignInClient mGoogleSignInClient;
    private boolean doubleBackToExitPressedOnce = false;
    private static final String PREFS_NAME = "settings_prefs";
    private static final String DARK_MODE_KEY = "dark_mode";

    private CustomViewPager viewPager1;

    private SharedPreferences sharedPreferences;
    private TransactionsFragment transactionsFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = sharedPreferences.getBoolean(DARK_MODE_KEY, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        setContentView(R.layout.activity_central);

        // FIREBASE
        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("606138593322-qmo8r77q8faabttijt0tj9e6aiai0rtm.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        FirebaseApp.initializeApp(/*context=*/ this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance());

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
                                    profileImage = resource;
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

        // Leer el símbolo de la moneda seleccionada de SharedPreferences
        sharedPreferences = getSharedPreferences("CurrencyPrefs", MODE_PRIVATE);
        String selectedCurrencySymbol = sharedPreferences.getString("selectedCurrencySymbol", "€"); // Valor por defecto: €

        // Inicializar y pasar el símbolo de la moneda al fragmento
        transactionsFragment = new TransactionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("currencySymbol", selectedCurrencySymbol);
        transactionsFragment.setArguments(bundle);

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, transactionsFragment, "transactionsFragmentTag")
                .commit();

        // Configurar el adaptador del ViewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager1 = findViewById(R.id.view_pager);
        viewPager1.setSwipeEnabled(false);
        viewPager1.setAdapter(sectionsPagerAdapter);

        BottomNavigationView mybottomNavView = findViewById(R.id.bottomNavigationView);
        mybottomNavView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.transactions) {
                    item.setChecked(true);
                    removeBadge(mybottomNavView, itemId);
                    viewPager1.setCurrentItem(0);
                } else if (itemId == R.id.categories) {
                    item.setChecked(true);
                    removeBadge(mybottomNavView, itemId);
                    viewPager1.setCurrentItem(1);
                } else if (itemId == R.id.graphics) {
                    item.setChecked(true);
                    removeBadge(mybottomNavView, itemId);
                    viewPager1.setCurrentItem(2);
                } else if (itemId == R.id.settings) {
                    item.setChecked(true);
                    removeBadge(mybottomNavView, itemId);
                    viewPager1.setCurrentItem(3);
                }
                return false;
                }
        });

        viewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    mybottomNavView.getMenu().getItem(0).setChecked(false);
                }
                mybottomNavView.getMenu().getItem(position).setChecked(true);
                removeBadge(mybottomNavView, mybottomNavView.getMenu().getItem(position).getItemId());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



        // Revisar si se debe abrir el SettingsFragment
        if (getIntent().getBooleanExtra("openSettings", false)) {
            loadSettingsFragment();
        }


        //Para obtener la moneda
            //Obtenemos el bundle del intent
        Bundle bundle2 = getIntent().getExtras();
        if (bundle2 != null) {
            String moneda = bundle2.getString("monedaFromCurrencyActivity");
            Log.i(TAG, "Monedilla1: " + moneda);
            if (moneda != null) {
                // Crear un nuevo Bundle para pasar la moneda al fragmento
                Bundle args = new Bundle();
                args.putString("MonedaFromCentral", moneda);

                // Crear una instancia de TransactionsFragment y establecer los argumentos
                TransactionsFragment fragment = new TransactionsFragment();
                fragment.setArguments(args);

                // Iniciar el fragmento utilizando FragmentManager y FragmentTransaction
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fm.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.commit();
            }
            Log.i(TAG, "Monedilla2: " + moneda);
        }



    }

    private void loadSettingsFragment() {
        viewPager1.setCurrentItem(3);
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(Central.this, "Presiona ATRÁS de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        if (itemView.getChildCount() == 4) {
            itemView.removeViewAt(3);
        }
    }
}
