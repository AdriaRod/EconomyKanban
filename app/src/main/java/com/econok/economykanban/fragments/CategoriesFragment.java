package com.econok.economykanban.fragments;

import static android.content.ContentValues.TAG;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.econok.economykanban.CardAdapter;
import com.econok.economykanban.CardItem;
import com.econok.economykanban.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CategoriesFragment extends Fragment {


    //************************+ VARIABLES ************************
    //fecha
    private TextView currentDateTextView;

    private double balance = 0.0;
    private FirebaseAuth mAuth;
    private TextView balanceTextView;

    //selector de meses
    private RadioButton previousMonthButton;
    private RadioButton currentMonthButton;
    private RadioButton nextMonthButton;
    private int currentMonthIndex = 5; // Junio por defecto
    private ImageView nextButton, previousButton;

    //PopUp Menu para seleccionar (ADD, EDITAR, ELIMINAR)
    ImageView three_dots_btn;
    TextView btnAdd, btnEdit, btnDelete;
    private Boolean isClicked;

    //SPINNER DE FILTROS
    TextView btnFilters;

    //HORIZONTAL SCROLL DE LAS CATEGORIAS
    private RadioGroup radioGroupCategories;
    private RadioButton btnGlobal, btnFood, btnHome, btnHealth, btnEntertainment, btnSaves, btnOthers, btnGym, btnTransport, btnEducation, btnClothes, btnDebts, btnNa;
    private RadioButton lastSelectedButton;

    // RECYCLER VIEW
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    public CategoriesFragment() {
        // Required empty public constructor
    }

    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cardList = new ArrayList<>();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        //______________________________ FECHA _______________________
        currentDateTextView = view.findViewById(R.id.currentDate);


        //********** PARA EL RECYCLER VIEW ************
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardAdapter(getContext(), cardList);
        recyclerView.setAdapter(adapter);

        balanceTextView = view.findViewById(R.id.balance);

        //******************************* PARA LOS  MESES *****************************
        // Inicialización de los RadioButtons
        previousMonthButton = view.findViewById(R.id.previous_month);
        currentMonthButton = view.findViewById(R.id.current_month);
        nextMonthButton = view.findViewById(R.id.next_month);

        //Inicializamos los ImageView (que nos sirven de botones de adelante y atras tambien)
        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        // Configura el texto inicial de los RadioButtons
        updateMonthsText();

        // Configura los listeners de los RadioButtons
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex--;
                if (currentMonthIndex < 0) currentMonthIndex = 11;
                updateMonthsText();
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex++;
                if (currentMonthIndex > 11) currentMonthIndex = 0;
                updateMonthsText();
            }
        });

        // Configura los listeners de los botones
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex--;
                if (currentMonthIndex < 0) currentMonthIndex = 11;
                updateMonthsText();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentMonthIndex++;
                if (currentMonthIndex > 11) currentMonthIndex = 0;
                updateMonthsText();
            }
        });

        //***********************************+++ POP UP PARA EL TYPE ****************************
        //Inicializamos el button de los 3 puntos
        three_dots_btn = view.findViewById(R.id.btn_popUpMenu);
        btnAdd = view.findViewById(R.id.addBtn);
        btnEdit= view.findViewById(R.id.editBtn);
        btnDelete = view.findViewById(R.id.removeBtn);
        isClicked = false;

        //Inicializamos el spinner (que no es spinner es un textView que queda mejor)
        btnFilters = view.findViewById(R.id.btnTypes);


        //Lanzamos el onclick al pop-up
        btnFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });


        three_dots_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isClicked) {
                    // Cambiar el color del SVG del botón a un azul más claro
                    int color = ContextCompat.getColor(getContext(), R.color.light_blue);
                    three_dots_btn.setColorFilter(color, PorterDuff.Mode.SRC_IN);

                    // Mostrar los botones
                    btnAdd.setVisibility(View.VISIBLE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnDelete.setVisibility(View.VISIBLE);

                    isClicked = true;
                } else {
                    // Cambiar el color del SVG del botón a su color original
                    three_dots_btn.setColorFilter(null);

                    // Ocultar los botones
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnEdit.setVisibility(View.INVISIBLE);
                    btnDelete.setVisibility(View.INVISIBLE);

                    isClicked = false;
                }
            }
        });



        //*********************** RADIO GROUP DE CATEGORIAS *************************
        btnGlobal = view.findViewById(R.id.radioButtonGlobal);
        btnFood = view.findViewById(R.id.radioButtonFood);
        btnHome = view.findViewById(R.id.radioButtonHome);
        btnHealth = view.findViewById(R.id.radioButtonHealth);
        btnEntertainment = view.findViewById(R.id.radioButtonEntertainment);
        btnSaves = view.findViewById(R.id.radioButtonSaves);
        btnOthers = view.findViewById(R.id.radioButtonOthers);
        btnGym = view.findViewById(R.id.radioButtonGym);
        btnTransport = view.findViewById(R.id.radioButtonTransport);
        btnEducation = view.findViewById(R.id.radioButtonEducation);
        btnClothes = view.findViewById(R.id.radioButtonClothes);
        btnDebts = view.findViewById(R.id.radioButtonDebts);
        btnNa = view.findViewById(R.id.radioButtonNa);

        // Establecer los estilos por defecto
        setButtonStyle(btnGlobal, true);
        setButtonStyle(btnFood, false);
        setButtonStyle(btnHome, false);
        setButtonStyle(btnHealth, false);
        setButtonStyle(btnEntertainment, false);
        setButtonStyle(btnSaves, false);
        setButtonStyle(btnOthers, false);
        setButtonStyle(btnGym, false);
        setButtonStyle(btnTransport, false);
        setButtonStyle(btnEducation, false);
        setButtonStyle(btnClothes, false);
        setButtonStyle(btnDebts, false);
        setButtonStyle(btnNa, false);

        btnGlobal.setOnClickListener(radioButtonClickListener);
        btnFood.setOnClickListener(radioButtonClickListener);
        btnHome.setOnClickListener(radioButtonClickListener);
        btnHealth.setOnClickListener(radioButtonClickListener);
        btnEntertainment.setOnClickListener(radioButtonClickListener);
        btnSaves.setOnClickListener(radioButtonClickListener);
        btnOthers.setOnClickListener(radioButtonClickListener);
        btnGym.setOnClickListener(radioButtonClickListener);
        btnTransport.setOnClickListener(radioButtonClickListener);
        btnEducation.setOnClickListener(radioButtonClickListener);
        btnClothes.setOnClickListener(radioButtonClickListener);
        btnDebts.setOnClickListener(radioButtonClickListener);
        btnNa.setOnClickListener(radioButtonClickListener);

        // Establecer lastSelectedButton como el botón de comida por defecto
        lastSelectedButton = btnGlobal;

        return view;
    }

    private void setButtonStyle(RadioButton button, boolean isSelected) {
        button.setBackgroundResource(isSelected ? R.drawable.button_category_selected : R.drawable.button_category_normal);
        button.setChecked(isSelected);
        int textColor = isSelected ? R.color.black : R.color.black;
        button.setTextColor(getResources().getColor(textColor));
        button.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
    }

    private final View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            mAuth= FirebaseAuth.getInstance();
            DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
            // Obtener la referencia a la subcolección "transacciones" del usuario
            CollectionReference transaccionesRef = usuarioRef.collection("transacciones");
            RadioButton selectedButton = (RadioButton) v;
            setButtonStyle(selectedButton, true);
            if (lastSelectedButton != null && lastSelectedButton != selectedButton) {
                setButtonStyle(lastSelectedButton, false);
            }
            lastSelectedButton = selectedButton;
            if(v.getId()==R.id.radioButtonNa){
                transaccionesRef.whereEqualTo("etiqueta","n/a").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            cardList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extraer los datos de la transacción
                                String concepto = document.getString("concepto");
                                String tipo = document.getString("tipo");
                                String cantidad = document.getString("cantidad");

                                // Crear un objeto de tarjeta (Card) con los datos de la transacción y añadirlo a la lista de tarjetas
                                cardList.add(new CardItem(concepto,tipo,concepto,cantidad));
                            }
                            // Actualizar la interfaz de usuario con la nueva lista de tarjetas
                            adapter.notifyDataSetChanged();
                            calcularBalance(); // Calcular el nuevo saldo
                            actualizarBalanceTextView(); // Actualizar el texto del balanceTextView
                        } else {
                            Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                        }
                    }
                });
            }
        }
    };

    private void calcularBalance() {
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (CardItem item : cardList) {
            String transactionAmount = item.getTransactionNumber();
            if (transactionAmount.matches("[-+]?[0-9]*\\.?[0-9]+")) {
                double amount = Double.parseDouble(transactionAmount);
                if (item.getTransactionType().equals("Income")) {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
            } else {
                Log.e("TransactionsFragment", "Invalid transaction amount: " + transactionAmount);
            }
        }
        balance = totalIncome - totalExpense;
        actualizarBalanceTextView();
    }

    private void actualizarBalanceTextView() {
        String formattedBalance = String.format(Locale.getDefault(), "%d", (int) balance);
        balanceTextView.setText(formattedBalance);
    }


    //****************************** PARA MOSTRAR LOS POP UP MENU ************************
    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.categories_popup_menu);
        // variables de Strings
        int str_all = R.string.all;
        int str_income = R.string.income;
        int str_expense = R.string.expense;

        // Maneja los clics de los elementos del menú
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item_1) {
                    // Código para la acción de All
                    btnFilters.setText(getString(str_all));
                    return true;
                } else if (itemId == R.id.item_2) {
                    // Código para la acción de Income
                    btnFilters.setText(getString(str_income));
                    return true;
                } else if (itemId == R.id.item_3) {
                    // Código para la acción de Expense
                    btnFilters.setText(getString(str_expense));
                    return true;
                }
                return false;
            }
        });

        // Muestra el menú emergente
        popupMenu.show();
    }




    //______________________________________ ON VIEW CREATED _________________________________
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //*********************** CURRENT DATE *******************
        // Get the current date
        Date currentDate = new Date();
        int v_date = R.string.date_format;

        // Format the date to "MM/dd/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(v_date), Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);

        // Capitalize the first letter of the Month
        String capitalizedDate = formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1);

        // Set the formatted date to the TextView
        currentDateTextView.setText(capitalizedDate);



    }

    //FUNCIONES CUSTOM PARA LOS MESES
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

    // Método para obtener la abreviatura del nombre del mes a partir de su número
    private String getMonthAbbreviation(int month) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        return months[month];
    }



}