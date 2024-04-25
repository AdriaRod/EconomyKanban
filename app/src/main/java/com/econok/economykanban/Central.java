package com.econok.economykanban;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.view.MenuItem;

import com.econok.economykanban.databinding.ActivityCentralBinding;
import com.econok.economykanban.fragments.SectionsPagerAdapter;
import com.econok.economykanban.fragments.TransactionsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class Central extends AppCompatActivity {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private MenuItem prevMenuItem;

    ActivityCentralBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central);

        FragmentManager fragmentManager = getSupportFragmentManager();
        TransactionsFragment transactionsFragment = new TransactionsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, transactionsFragment, "transactionsFragmentTag");
        transaction.commit();

        //2
        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());

        //3
        ViewPager viewPager1 = findViewById(R.id.view_pager);
        viewPager1.setAdapter(sectionsPagerAdapter);

        //4
        BottomNavigationView mybottomNavView = findViewById(R.id.bottomNavigationView);

        //6
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

        //para cambiar de fragment haciendo slide
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
                    mybottomNavView.getMenu().getItem(position).setChecked(true);
                    removeBadge(mybottomNavView, mybottomNavView.getMenu().getItem(position).getItemId());
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }//final del OnCreate

    //5
    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId); //Esto aparece subrayado en rojo pero funciona (asi que si no saben no toquen gracias xd)
        if (itemView.getChildCount() == 4) {
            itemView.removeViewAt(3);
        }
    }

}