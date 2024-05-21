package com.econok.economykanban.fragments;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.econok.economykanban.CardAdapter;
import com.econok.economykanban.CardItem;
import com.econok.economykanban.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionsFragment extends Fragment {

    //******************* VARIABLES *********************
    //fecha
    private TextView currentDateTextView;

    //selector de meses
    private RadioButton previousMonthButton;
    private RadioButton currentMonthButton;
    private RadioButton nextMonthButton;
    private int currentMonthIndex = 5; // Junio por defecto
    private ImageView nextButton, previousButton;

    //PopUp Menu para seleccionar (ADD, EDITAR, ELIMINAR)
    ImageView three_dots_btn;
    TextView btnAdd, btnEdit, btnDelete, btnFilters;
    private Boolean isClicked;



    //_____________________estas de abajo estaban de antes xd________________________
    private RecyclerView recyclerView;
    private CardAdapter adapter;

    private FirebaseAuth mAuth;
    private List<CardItem> cardList;
    private Button addTransaction, button1, button2;
    private Dialog dialog;
    private EditText conceptEditText, quantityEditText;
    private Button acceptButton;
    private TextView balanceTextView;
    private double balance = 0.0;
    private Boolean isIncome = null;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardList = new ArrayList<>();
        visualizarTransacciones();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        //______________________________ FECHA (current date) _______________________
        currentDateTextView = view.findViewById(R.id.currentDateTransactions);
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

        //*************************** PARA EL POP UP DEL TYPE ****************
        //***********************************+++ POP UP PARA EL TYPE ****************************
        //Inicializamos el button de los 3 puntos
        btnFilters = view.findViewById(R.id.btnTypes);
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

        //___________________________ TRANSACCIONES ___________________________

        addTransaction = view.findViewById(R.id.addBtn);
        balanceTextView = view.findViewById(R.id.balanceTextView);



        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardAdapter(getContext(), cardList);
        recyclerView.setAdapter(adapter);

        // Inicializar el Dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        //esto es para que el fondo sea transparente
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        conceptEditText = dialog.findViewById(R.id.concept);
        quantityEditText = dialog.findViewById(R.id.quantity);
        acceptButton = dialog.findViewById(R.id.accept);

        button1 = dialog.findViewById(R.id.button1);
        button2 = dialog.findViewById(R.id.button2);

        // Inicializar el botón closeDialog dentro del Dialog
        View closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // Limpiar los campos del diálogo al cerrar
                conceptEditText.setText("");
                quantityEditText.setText("");
                // Resetear el estado del botón
                isIncome = null;
                button1.setEnabled(true);
                button2.setEnabled(true);
                button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                button1.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                button2.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
            }
        });

        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIncome = true;
                button1.setEnabled(false);
                button2.setEnabled(true);
                button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonIncomePressed));
                button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                button1.setTextColor(getResources().getColor(R.color.dialogTextIncome));
                button2.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isIncome = false;
                button1.setEnabled(true);
                button2.setEnabled(false);
                button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonsExpensePressed));
                button1.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                button2.setTextColor(getResources().getColor(R.color.dialogTextExpense));
            }
        });

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String concept = conceptEditText.getText().toString().trim();
                String quantity = quantityEditText.getText().toString().trim();

                if (isIncome == null) {
                    Toast.makeText(getContext(), getString(R.string.noContentToast), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!concept.isEmpty() && !quantity.isEmpty()) {
                    cardList.clear(); // Limpiar la lista actual de tarjetas antes de cargar nuevas transacciones
                    adapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
                    String type = isIncome ? "Income" : "Expense";
                    dialog.dismiss();
                    // Obtener la fecha y hora actual
                    Date fechaActual = Calendar.getInstance().getTime();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String fechaFormateada = sdf.format(fechaActual);

                    if (isIncome) {
                        quantity = "+" + quantity;
                    } else {
                        quantity = "-" + quantity;
                    }

                    crearTransaccion(concept, quantity, type, fechaFormateada);

                    visualizarTransacciones();
                    // Limpiar los campos del diálogo al guardar
                    conceptEditText.setText("");
                    quantityEditText.setText("");
                    // Resetear el estado del botón
                    isIncome = null;
                    button1.setEnabled(true);
                    button2.setEnabled(true);
                    button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                    button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtons_Expense_Income));
                    button1.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                    button2.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                } else {
                    Toast.makeText(getContext(), getString(R.string.noContentToast), Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;
    }

    public void crearTransaccion(String concept,String quantity,String type,String fecha){
        //Instanciar firebase,buscar el usuario actual y crear la coleccion de transacciones dentro del usuario
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");

        // Crear el mapa de datos para la transacción
        Map<String, Object> transaccion = new HashMap<>();
        transaccion.put("fecha", fecha);
        transaccion.put("concepto", concept);
        transaccion.put("cantidad", quantity);
        transaccion.put("tipo", type);
        transaccion.put("etiqueta","n/a");

        // Añadir la transacción a la subcolección "transacciones"
        transaccionesRef.add(transaccion)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getActivity(), "Transaction added succesfully", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Transacción agregada correctamente");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error adding transaction", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Error al agregar transacción", e);
                    }
                });
    }

    private void visualizarTransacciones(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        // Obtener la referencia a la subcolección "transacciones" del usuario
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");

        // Obtener todas las transacciones del usuario
        transaccionesRef.orderBy("fecha", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }

                    calcularBalance(); // Calcular el nuevo saldo
                    actualizarBalanceTextView(); // Actualizar el texto del balanceTextView
                } else {
                    Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                }
            }
        });

    }

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
                    totalExpense -= amount;
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

        switch (currentMonthButton.getText().toString()){
            case "ene":
                obtenerTransaccionesPorMes("01");
                break;
            case "feb":
                obtenerTransaccionesPorMes("02");
                break;
            case "mar":
                obtenerTransaccionesPorMes("03");
                break;
            case "abr":
                obtenerTransaccionesPorMes("04");
                break;
            case "may":
                obtenerTransaccionesPorMes("05");
                break;
            case "jun":
                obtenerTransaccionesPorMes("06");
                break;
            case "jul":
                obtenerTransaccionesPorMes("07");
                break;
            case "ago":
                obtenerTransaccionesPorMes("08");
                break;
            case "sept":
                obtenerTransaccionesPorMes("09");
                break;
            case "oct":
                obtenerTransaccionesPorMes("10");
                break;
            case "nov":
                obtenerTransaccionesPorMes("11");
                break;
            case "dic":
                obtenerTransaccionesPorMes("12");
                break;
        }
    }

    // Método para obtener la abreviatura del nombre del mes a partir de su número
    private String getMonthAbbreviation(int month) {
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getShortMonths();
        return months[month];
    }

    //****************************** PARA MOSTRAR LOS POP UP MENU ************************
    public void showPopupMenu(View view) {

        //-----------------------------------------------------------------------------------------------------------------------------
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
                    //Mostrar todas
                    visualizarTransacciones();
                    return true;
                } else if (itemId == R.id.item_2) {
                    // Código para la acción de Income
                    btnFilters.setText(getString(str_income));
                    //Mostrar income
                    mostrarIncome("Income");
                    return true;
                } else if (itemId == R.id.item_3) {
                    // Código para la acción de Expense
                    btnFilters.setText(getString(str_expense));
                    //Mostrar expense
                    mostrarExpense("Expense");
                    return true;
                }
                return false;
            }
        });

        // Muestra el menú emergente
        popupMenu.show();
    }

    private void mostrarIncome(String tipo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        // Obtener la referencia a la subcolección "transacciones" del usuario
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");
        // Obtener todas las transacciones del usuario con el tipo especificado, ordenadas por fecha de creación
        transaccionesRef.whereEqualTo("tipo", tipo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Limpiar la lista actual de tarjetas antes de cargar nuevas transacciones
                            cardList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extraer los datos de la transacción
                                String concepto = document.getString("concepto");
                                String cantidad = document.getString("cantidad");
                                String fecha=document.getString("fecha");

                                // Crear un objeto de tarjeta (Card) con los datos de la transacción y añadirlo a la lista de tarjetas
                                cardList.add(new CardItem(concepto,tipo,fecha,cantidad));
                            }
                            // Definir un comparador personalizado para ordenar por fecha de cada tarjeta
                            Comparator<CardItem> comparator = new Comparator<CardItem>() {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                                @Override
                                public int compare(CardItem card1, CardItem card2) {
                                    // Convertir las fechas de los CardItem a objetos Date para compararlas
                                    String fecha1 = card1.getTag();
                                    String fecha2 = card2.getTag();
                                    // Comparar las fechas y devolver el resultado
                                    return fecha2.compareTo(fecha1); // Orden descendente, cambia a fecha1.compareTo(fecha2) para orden ascendente
                                }
                            };
                            // Ordenar cardList usando el comparador personalizado
                            Collections.sort(cardList, comparator);
                            // Actualizar la interfaz de usuario con la nueva lista de tarjetas
                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                        }
                    }
                });
    }

    private void mostrarExpense(String tipo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        // Obtener la referencia a la subcolección "transacciones" del usuario
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");
        // Obtener todas las transacciones del usuario con el tipo especificado, ordenadas por fecha de creación
        transaccionesRef.whereEqualTo("tipo", tipo)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Limpiar la lista actual de tarjetas antes de cargar nuevas transacciones
                            cardList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Extraer los datos de la transacción
                                String concepto = document.getString("concepto");
                                String cantidad = document.getString("cantidad");
                                String fecha=document.getString("fecha");

                                // Crear un objeto de tarjeta (Card) con los datos de la transacción y añadirlo a la lista de tarjetas
                                cardList.add(new CardItem(concepto,tipo,fecha,cantidad));
                            }
                            // Definir un comparador personalizado para ordenar por fecha de cada tarjeta
                            Comparator<CardItem> comparator = new Comparator<CardItem>() {
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                                @Override
                                public int compare(CardItem card1, CardItem card2) {
                                    // Convertir las fechas de los CardItem a objetos Date para compararlas
                                    String fecha1 = card1.getTag();
                                    String fecha2 = card2.getTag();
                                    // Comparar las fechas y devolver el resultado
                                    return fecha2.compareTo(fecha1); // Orden descendente, cambia a fecha1.compareTo(fecha2) para orden ascendente
                                }
                            };
                            // Ordenar cardList usando el comparador personalizado
                            Collections.sort(cardList, comparator);
                            // Actualizar la interfaz de usuario con la nueva lista de tarjetas
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                        }
                    }
                });
    }

    private void obtenerTransaccionesPorMes(final String mesFiltrado) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");

        // Obtener todas las transacciones del usuario
        transaccionesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    cardList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String fecha = document.getString("fecha");
                        if (fecha != null && obtenerMesDeFecha(fecha).equals(mesFiltrado)) {
                            String concepto = document.getString("concepto");
                            String tipo = document.getString("tipo");
                            String cantidad = document.getString("cantidad");

                            cardList.add(new CardItem(concepto, tipo, fecha, cantidad));
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Log.d(TAG, "Error obteniendo transacciones: ", task.getException());
                }
            }
        });
    }

    // Método para extraer el mes de una fecha en formato "dd/MM/yyyy HH:mm:ss"
    private String obtenerMesDeFecha(String fecha) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        try {
            Date date = sdf.parse(fecha);
            SimpleDateFormat sdfMes = new SimpleDateFormat("MM", Locale.getDefault());
            return sdfMes.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }




    @Override
    public void onStart() {
        super.onStart();
        visualizarTransacciones();
        actualizarBalanceTextView();
    }

}
