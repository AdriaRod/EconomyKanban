package com.econok.economykanban;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Language extends AppCompatActivity {
    private Button btnCancel;
    private RadioGroup languageRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_language);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(v -> {
            irASettingsFragment();
        });


    }

    private void irASettingsFragment() {
        Intent intent = new Intent(this, Central.class);
        intent.putExtra("openSettings", true);
        startActivity(intent);
    }
}