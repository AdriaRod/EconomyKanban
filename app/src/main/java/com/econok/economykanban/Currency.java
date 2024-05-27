package com.econok.economykanban;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class Currency extends AppCompatActivity {

    private Button done;
    private RadioGroup currencyRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency);

        // BTN DONE
        done = findViewById(R.id.btn_done);
        done.setOnClickListener(v -> {
            irASettingsFragment();
        });


        // PARA CAMBIAR LA MONEDA
        currencyRadioGroup = findViewById(R.id.currency_radio_group);
        currencyRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String selectedCurrencySymbol = "";
                if (checkedId == R.id.euro) {
                    selectedCurrencySymbol = "€";
                } else if (checkedId == R.id.dolar) {
                    selectedCurrencySymbol = "$";
                } else if (checkedId == R.id.libra) {
                    selectedCurrencySymbol = "£";
                } else if (checkedId == R.id.yuan) {
                    selectedCurrencySymbol = "¥";
                }




            }
        });



    }//fin del OnCreate

    private void irASettingsFragment() {

        Intent intent = new Intent(this, Central.class);
        intent.putExtra("openSettings", true);
        startActivity(intent);

    }

}