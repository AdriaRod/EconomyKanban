package com.econok.economykanban;

import android.graphics.Typeface;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MyValueFormatter extends ValueFormatter {
    private final Typeface mTypeface;

    public MyValueFormatter(Typeface typeface) {
        this.mTypeface = typeface;
    }

    @Override
    public String getFormattedValue(float value) {
        // Aquí puedes formatear el valor como quieras
        return "$" + String.valueOf((int) value);
    }

    public Typeface getTypeface() {
        return mTypeface;
    }
}
