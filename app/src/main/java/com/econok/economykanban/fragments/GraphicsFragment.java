package com.econok.economykanban.fragments;

import static android.content.ContentValues.TAG;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.econok.economykanban.CardItem;
import com.econok.economykanban.MyValueFormatter;
import com.econok.economykanban.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GraphicsFragment extends Fragment {

    private TextView currentDateTextView;
    private RadioButton previousMonthButton, currentMonthButton, nextMonthButton;
    private ImageView previousButton, nextButton;
    private int currentMonthIndex = Calendar.getInstance().get(Calendar.MONTH);
    private List<CardItem> cardList;
    private BarChart barChart;
    private FirebaseAuth mAuth;
    private TextView balanceTextView;

    public GraphicsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graphics, container, false);
        currentDateTextView = view.findViewById(R.id.titleTextView);
        barChart = view.findViewById(R.id.barChart);
        balanceTextView = view.findViewById(R.id.balanceTextView);

        previousMonthButton = view.findViewById(R.id.previous_month);
        currentMonthButton = view.findViewById(R.id.current_month);
        nextMonthButton = view.findViewById(R.id.next_month);

        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        updateMonthsText();

        previousMonthButton.setOnClickListener(v -> {
            currentMonthIndex--;
            if (currentMonthIndex < 0) currentMonthIndex = 11;
            updateMonthsText();
            updateChart();
        });

        nextMonthButton.setOnClickListener(v -> {
            currentMonthIndex++;
            if (currentMonthIndex > 11) currentMonthIndex = 0;
            updateMonthsText();
            updateChart();
        });

        previousButton.setOnClickListener(v -> {
            currentMonthIndex--;
            if (currentMonthIndex < 0) currentMonthIndex = 11;
            updateMonthsText();
            updateChart();
        });

        nextButton.setOnClickListener(v -> {
            currentMonthIndex++;
            if (currentMonthIndex > 11) currentMonthIndex = 0;
            updateMonthsText();
            updateChart();
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //*********************** CURRENT DATE *******************
        Date currentDate = new Date();
        int v_date = R.string.date_format;

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(v_date), Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        String capitalizedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);
        currentDateTextView.setText(capitalizedDate);

        // Initialize the list
        cardList = new ArrayList<>();

        // Update the bar chart
        updateChart();  // Call updateChart here to refresh data when the fragment is created
    }

    private void updateBarChart(float totalIncome, float totalExpense) {
        if (!isAdded()) {
            return;
        }

        // Get current month
        Calendar calendar = Calendar.getInstance();
        int currentMonthIndex = calendar.get(Calendar.MONTH);

        Log.d(TAG, "Total INC: " + totalIncome);
        Log.d(TAG, "Total EXP: " + totalExpense);

        // Verificar si el tema actual es oscuro o claro
        int currentNightMode = requireContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES;

        // Establecer el color del texto según el modo de noche
        int textColor = isNightMode ? Color.WHITE : Color.BLACK;

        //Fonts tests
        Typeface typeface = ResourcesCompat.getFont(requireContext(), R.font.poppins_regular);
        MyValueFormatter formatter = new MyValueFormatter(typeface);

        // Prepare data for the bar chart
        ArrayList<BarEntry> entriesIncome = new ArrayList<>();
        ArrayList<BarEntry> entriesExpense = new ArrayList<>();

        // Datos de prueba para ingresos (Income)
        entriesIncome.add(new BarEntry(0, totalIncome));

        // Datos de prueba para gastos (Expense)
        entriesExpense.add(new BarEntry(1, totalExpense));

        // Create bar data sets
        BarDataSet dataSetIncome = new BarDataSet(entriesIncome, "Income");
        dataSetIncome.setValueTypeface(typeface); // Set the typeface for the data labels
        dataSetIncome.setColor(Color.parseColor("#32DA6E"));
        dataSetIncome.setValueTextSize(11f);
        dataSetIncome.setValueTextColor(textColor); // Establecer color del texto

        BarDataSet dataSetExpense = new BarDataSet(entriesExpense, "Expense");
        dataSetExpense.setValueTypeface(typeface);
        dataSetExpense.setColor(Color.parseColor("#ED918A"));
        dataSetExpense.setValueTextSize(11f);
        dataSetExpense.setValueTextColor(textColor); // Establecer color del texto

        // Group the data sets
        BarData barData = new BarData(dataSetIncome, dataSetExpense);
        barData.setBarWidth(0.3f); // set custom bar width
        barData.setHighlightEnabled(false);
        barData.setDrawValues(true);

        // Setup X axis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{String.valueOf(currentMonthIndex)}));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTypeface(typeface); // Set the typeface for the X axis
        xAxis.setDrawGridLines(false); // remove grid lines
        xAxis.setDrawLabels(false); // show labels

        // Setup Y axis
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setValueFormatter(formatter);
        yAxis.setTypeface(typeface); // Set the typeface for the Y axis
        yAxis.setAxisMinimum(0);
        yAxis.setDrawGridLines(false); // remove grid lines
        yAxis.setDrawLabels(true);
        yAxis.setTextSize(12f);
        yAxis.setTextColor(textColor); // Establecer color del texto
        YAxis yAxisRight = barChart.getAxisRight();
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawGridLines(false);

        // Apply data to the chart
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true); // make the x-axis fit exactly all bars

        // Configure legend
        Legend legend = barChart.getLegend();
        legend.setTextColor(textColor);
        legend.setTypeface(typeface);
        legend.setTextSize(10f);
        legend.setXEntrySpace(15f); //Margen entre textos de leyenda

        barChart.invalidate(); // refresh
    }

    private void updateChart() {
        FirebaseFirestore db;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");

        // Obtener todas las transacciones del usuario
        transaccionesRef.orderBy("fecha", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    float totalIncome = 0;
                    float totalExpense = 0;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String type = document.getString("tipo");
                        String quantity = document.getString("cantidad");

                        if (type != null && quantity != null) {
                            double amount = Double.parseDouble(quantity);
                            if (type.equals("Income")) {
                                totalIncome += amount;
                            } else if (type.equals("Expense")) {
                                totalExpense += amount;
                            }
                        }
                    }
                    totalExpense = Math.abs(totalExpense);

                    Log.d(TAG, "Total Income: " + totalIncome);
                    Log.d(TAG, "Total Expense: " + totalExpense);
                    updateBarChart(totalIncome, totalExpense);

                } else {
                    Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                }
            }
        });
    }

    private void updateMonthsText() {
        int previousMonthIndex = currentMonthIndex - 1;
        int nextMonthIndex = currentMonthIndex + 1;

        if (previousMonthIndex < 0) previousMonthIndex = 11;
        if (nextMonthIndex > 11) nextMonthIndex = 0;

        previousMonthButton.setText(getMonthAbbreviation(previousMonthIndex));
        currentMonthButton.setText(getMonthAbbreviation(currentMonthIndex));
        nextMonthButton.setText(getMonthAbbreviation(nextMonthIndex));

        previousMonthButton.setChecked(false);
        currentMonthButton.setChecked(true);
        nextMonthButton.setChecked(false);
    }

    private String getMonthAbbreviation(int month) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        return months[month];
    }
}
