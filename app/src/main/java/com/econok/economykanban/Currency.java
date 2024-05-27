package com.econok.economykanban;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        //Inicializar
        currencyRadioGroup = findViewById(R.id.currency_radio_group);
        done = findViewById(R.id.btn_done);

        //Click en el boton "done"
        done.setOnClickListener(v -> {

            // Obtener el ID de la moneda seleccionada
            int checkedId = currencyRadioGroup.getCheckedRadioButtonId();
            String selectedCurrencySymbol = "";

            // Asignar el símbolo de la moneda seleccionada
            if (checkedId == R.id.euro) {
                selectedCurrencySymbol = "€";
            } else if (checkedId == R.id.dolar) {
                selectedCurrencySymbol = "$";
            } else if (checkedId == R.id.libra) {
                selectedCurrencySymbol = "£";
            } else if (checkedId == R.id.yuan) {
                selectedCurrencySymbol = "¥";
            }

            Bundle bundle = new Bundle();
            bundle.putString("monedaFromCurrencyActivity", selectedCurrencySymbol);

            Intent intent = new Intent(this, Central.class);
            intent.putExtras(bundle);
            startActivity(intent);



            Log.i(TAG, "Simbolo de moneda: " + selectedCurrencySymbol);


//            irASettingsFragment();
        });







    }//fin del OnCreate

    private void irASettingsFragment() {

        Intent intent = new Intent(this, Central.class);
        intent.putExtra("openSettings", true);
        startActivity(intent);

    }


}