package com.econok.economykanban;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager.widget.ViewPager;

import com.econok.economykanban.fragments.CustomViewPager;
import com.econok.economykanban.fragments.SectionsPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Central extends AppCompatActivity {

    private boolean doubleBackToExitPressedOnce = false;
    private SectionsPagerAdapter sectionsPagerAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private SharedPreferences sharedPreferences;
    private CustomViewPager viewPager1;
    private CardAdapter cardAdapter;
    private String currencySymbol; // Variable para almacenar el símbolo de moneda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        sharedPreferences = getSharedPreferences("settings_prefs", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_central);

        mFirestore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtener el símbolo de la moneda
        String moneda = getIntent().getStringExtra("monedaFromCurrencyActivity");
        if (moneda == null) {
            moneda = sharedPreferences.getString("selectedCurrencySymbol", "€"); // Valor por defecto: €
        }

        




        // Configurar el adaptador del ViewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), moneda);
        viewPager1 = findViewById(R.id.view_pager);
        viewPager1.setSwipeEnabled(false);
        viewPager1.setAdapter(sectionsPagerAdapter);

        BottomNavigationView mybottomNavView = findViewById(R.id.bottomNavigationView);
        mybottomNavView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.transactions) {
                item.setChecked(true);
                viewPager1.setCurrentItem(0);
            } else if (itemId == R.id.categories) {
                item.setChecked(true);
                viewPager1.setCurrentItem(1);
            } else if (itemId == R.id.graphics) {
                item.setChecked(true);
                viewPager1.setCurrentItem(2);
            } else if (itemId == R.id.settings) {
                item.setChecked(true);
                viewPager1.setCurrentItem(3);
            }
            return false;
        });

        viewPager1.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                BottomNavigationView navigation = findViewById(R.id.bottomNavigationView);
                navigation.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            moveTaskToBack(true);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Presiona ATRÁS de nuevo para salir", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

}
