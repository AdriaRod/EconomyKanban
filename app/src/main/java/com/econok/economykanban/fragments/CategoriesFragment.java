package com.econok.economykanban.fragments;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.core.content.ContextCompat;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    FloatingActionButton openDialog;
    private Dialog dialog;
    private Boolean isClicked;


    //SPINNER DE FILTROS
    TextView btnFilters;

    //HORIZONTAL SCROLL DE LAS CATEGORIAS
    private RadioGroup radioGroupCategories;
    private RadioButton btnGlobal, btnFood, btnHome, btnHealth, btnEntertainment, btnSaves, btnOthers, btnGym, btnTransport, btnEducation, btnClothes, btnDebts, btnNa;
    private RadioButton lastSelectedButton;
    private final RadioButton[] newLastSelectedButton={null};

    // RECYCLER VIEW
    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;

    private Boolean editSelec;
    private LinearLayout layout_cat;
    private ArrayList<String> transaccionList = new ArrayList<>();
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
        //visualizarCategorias();
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);

        visualizarCategorias();
        int str_cancel = R.string.cancel;

        editSelec=true;
        //______________________________ FECHA _______________________
        currentDateTextView = view.findViewById(R.id.currentDate);


        //********** PARA EL RECYCLER VIEW ************
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        cardList=new ArrayList<>();
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

        // Inicializar el Dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_categories);
        layout_cat=dialog.findViewById(R.id.layout_cat);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        //esto es para que el fondo sea transparente
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

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
        openDialog = view.findViewById(R.id.openDialog);
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
                    openDialog.setVisibility(View.VISIBLE);

                    isClicked = true;

                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String boton=lastSelectedButton.getText().toString();
                            new AlertDialog.Builder(getActivity())
                                    .setTitle("Borrar categoria")
                                    .setMessage("¿Esta seguro que desea borrar la categoria "+boton+"?")
                                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getActivity(), "Categoria borrada", Toast.LENGTH_SHORT).show();;
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .setIcon(android.R.drawable.ic_dialog_alert)
                                    .show();
                        }
                    });

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Instanciar firebase,buscar el usuario actual y crear la coleccion de categorias dentro del usuario
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            mAuth= FirebaseAuth.getInstance();
                            DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
                            CollectionReference categoriasRef = usuarioRef.collection("categorias");
                            int str_new_category = R.string.new_category;
                            int str_add = R.string.add;

                            // Crear un cuadro de diálogo de entrada
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle(getString(str_new_category));

                            // Establecer el diseño del cuadro de diálogo
                            final EditText input = new EditText(getContext());
                            builder.setView(input);

                            // Configurar los botones del cuadro de diálogo
                            builder.setPositiveButton(getString(str_add), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String radioButtonName = input.getText().toString();
                                    int str_success = R.string.cat_add_succesfully;
                                    int str_error = R.string.cat_add_error;

                                    // Crear el mapa de datos para la transacción
                                    Map<String, Object> categoria = new HashMap<>();
                                    categoria.put("Nombre", radioButtonName);

                                    // Añadir la transacción a la subcolección "categorias"
                                    categoriasRef.add(categoria)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(getActivity(), getString(str_success), Toast.LENGTH_SHORT).show();
                                                    Log.d(TAG, "Categoria agregada correctamente");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity(), getString(str_error), Toast.LENGTH_SHORT).show();
                                                    Log.w(TAG, "Error al agregar categoria  ", e);
                                                }
                                            });

                                    // Crear un nuevo RadioButton con el nombre proporcionado
                                    ContextThemeWrapper newRadioButtonContext = new ContextThemeWrapper(getContext(), R.style.categories_radio_button);
                                    final RadioButton newRadioButton = new RadioButton(newRadioButtonContext);

                                    // Establecer el texto del nuevo RadioButton
                                    newRadioButton.setText(radioButtonName);

                                    // Desseleccionar todos los demás RadioButtons antes de seleccionar este
                                    radioGroupCategories.clearCheck();

                                    // Agregar el nuevo RadioButton al RadioGroup
                                    radioGroupCategories.addView(newRadioButton);

                                    // Opcional: seleccionar el nuevo RadioButton
                                    newRadioButton.setChecked(false);
                                    newRadioButton.setOnClickListener(radioButtonClickListener);

                                    lastSelectedButton=newRadioButton;

                                    // Aplicar el estilo al nuevo RadioButton
                                    setButtonStyle(newRadioButton, false); // Establecer como seleccionado inicialmente

                                    newRadioButton.setButtonDrawable(null);

                                    // Escuchar el cambio de estado de selección del botón
                                    newRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                        @Override
                                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                            // Aplicar el estilo cuando cambie el estado de selección
                                            setButtonStyle(newRadioButton, isChecked);
                                        }
                                    });
                                }

                            });

                            builder.setNegativeButton(getString(str_cancel), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            // Mostrar el cuadro de diálogo
                            builder.show();
                        }
                    });


                    btnEdit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openDialog.setVisibility(View.VISIBLE);
                            editSelec = !editSelec;

                            if (editSelec) {
                                btnEdit.setBackgroundColor(Color.parseColor("#FFFFFF")); // Color blanco
                            } else {
                                btnEdit.setBackgroundColor(Color.TRANSPARENT); // Transparente
                                adapter.resetSelectedItems();
                            }

                            adapter.setEditModeEnabled(editSelec);
                        }
                    });

                    openDialog.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            dialog.show();
                            cargarCategorias();
                            showSelectedItems();
                        }
                    });

                    Button acceptButton = dialog.findViewById(R.id.categoriesDialogAccept);
                    acceptButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Cerrar el diálogo de momento
                            dialog.dismiss();
                        }
                    });

                } else {
                    // Cambiar el color del SVG del botón a su color original
                    three_dots_btn.setColorFilter(null);
                    editSelec=false;
                    adapter.resetSelectedItems();
                    adapter.setEditModeEnabled(editSelec);
                    btnEdit.setBackgroundColor(Color.TRANSPARENT);
                    // Ocultar los botones
                    btnAdd.setVisibility(View.INVISIBLE);
                    btnEdit.setVisibility(View.INVISIBLE);
                    btnDelete.setVisibility(View.INVISIBLE);
                    openDialog.setVisibility(View.INVISIBLE);

                    isClicked = false;
                }
            }
        });



        //*********************** RADIO GROUP DE CATEGORIAS *************************
        radioGroupCategories=view.findViewById(R.id.radioGroup);
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
        setButtonStyle(btnGlobal, false);
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

    // Método para obtener los elementos seleccionados desde el adaptador
    public List<CardItem> getSelectedItemsFromAdapter() {
        if (adapter != null) {
            return adapter.getSelectedItems();
        }
        return new ArrayList<>(); // O manejar el caso de null apropiadamente
    }

    // Ejemplo de cómo usar el método en algún lugar de tu fragmento
    public void showSelectedItems() {
        List<CardItem> selectedItems = getSelectedItemsFromAdapter();
        // Aquí puedes manejar los elementos seleccionados como necesites
        for (CardItem item : selectedItems) {
            Log.d("Selected Item", item.getTitle());
            Toast.makeText(getActivity(), "Has seleccionado "+selectedItems.stream().count()+" elementos", Toast.LENGTH_SHORT).show();
        }
    }

    private void setButtonStyle(RadioButton button, boolean isSelected) {
        if (isSelected) {
            if (isDarkMode()) {
                button.setBackgroundResource(R.drawable.button_category_selected_dark);
                button.setTextColor(getResources().getColor(R.color.white));
            } else {
                button.setBackgroundResource(R.drawable.button_category_selected);
                button.setTextColor(getResources().getColor(R.color.black));
            }
            button.setTypeface(null, Typeface.BOLD);
        } else {
            if (isDarkMode()) {
                button.setBackgroundResource(R.drawable.category_radio_button_background_dark);
                button.setTextColor(getResources().getColor(R.color.white));
            } else {
                button.setBackgroundResource(R.drawable.button_category_normal);
                button.setTextColor(getResources().getColor(R.color.black));
            }
            button.setTypeface(null, Typeface.NORMAL);
        }
    }

    private boolean isDarkMode() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
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
            newLastSelectedButton[0]=selectedButton;
            if(v.getId()==R.id.radioButtonNa){
                visualizarTransacciones("n/a");
            } else if (newLastSelectedButton[0].isChecked()) {
                String categoria=newLastSelectedButton[0].getText().toString();
                visualizarTransacciones(categoria);
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

        calcularBalance();

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


    private void visualizarCategorias(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        // Obtener la referencia a la subcolección "categorias" del usuario
        CollectionReference categoriasRef = usuarioRef.collection("categorias");

        // Obtener todas las categorias del usuario
        categoriasRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Itera sobre cada documento en la colección
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    // Accede al valor del atributo "Nombre" de cada documento
                    String nombreCategoria = document.getString("Nombre");

                    ContextThemeWrapper newRadioButtonContext = new ContextThemeWrapper(getContext(), R.style.categories_radio_button);
                    final RadioButton newRadioButton = new RadioButton(newRadioButtonContext);

                    // Establecer el texto del nuevo RadioButton
                    newRadioButton.setText(nombreCategoria);

                    // Desseleccionar todos los demás RadioButtons antes de seleccionar este
                    radioGroupCategories.clearCheck();

                    // Agregar el nuevo RadioButton al RadioGroup
                    radioGroupCategories.addView(newRadioButton);

                    lastSelectedButton=newRadioButton;

                    // Configurar los márgenes (por ejemplo, 16dp a la derecha)
                    int marginInDp = 14; // Cambia esto según tus necesidades
                    int marginInPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, marginInDp, getResources().getDisplayMetrics());

// Obtener los parámetros de diseño del nuevo RadioButton
                    RadioGroup.LayoutParams params = (RadioGroup.LayoutParams) newRadioButton.getLayoutParams();

// Establecer el margen a la derecha
                    params.setMargins(0, 0, marginInPixels, 0);

// Aplicar los parámetros de diseño actualizados al RadioButton
                    newRadioButton.setLayoutParams(params);

                    // Opcional: seleccionar el nuevo RadioButton
                    newRadioButton.setChecked(false);

                    newRadioButton.setOnClickListener(radioButtonClickListener);

                    lastSelectedButton=newRadioButton;

                    // Aplicar el estilo al nuevo RadioButton
                    setButtonStyle(newRadioButton, false); // Establecer como seleccionado inicialmente

                    newRadioButton.setButtonDrawable(null);


                    // Escuchar el cambio de estado de selección del botón
                    newRadioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            // Aplicar el estilo cuando cambie el estado de selección
                            setButtonStyle(newRadioButton, isChecked);
                        }
                    });

                    // Haz lo que necesites con el nombre de la categoría
                    Log.d("Nombre de la categoría", nombreCategoria);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Maneja los errores si la consulta falla
                Log.e("Error", "Error al obtener las categorías", e);
            }
        });
    }

    private void cargarCategorias() {
        // Limpiar el layout para evitar duplicados
        layout_cat.removeAllViews();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        CollectionReference categoriasRef = usuarioRef.collection("categorias");
        CollectionReference transRef=usuarioRef.collection("transacciones");
        List<CardItem> selectedItems = getSelectedItemsFromAdapter();


        for (CardItem item : selectedItems) {
            transRef.whereEqualTo("concepto",item.getTitle()).get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    transaccionList.add(document.getId());
                                }
                            } else {
                                Log.d("Firebase", "Error obteniendo transacciones: ", task.getException());
                            }
                        }
                    });
        }

        categoriasRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String nombreCategoria = document.getString("Nombre");

                        // Crear un nuevo botón para cada categoría
                        Button categoriaButton = new Button(getActivity());
                        categoriaButton.setText(nombreCategoria);
                        categoriaButton.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        categoriaButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // Acciones al hacer clic en una categoría
                                Toast.makeText(getActivity(), "Categoría seleccionada: " + nombreCategoria, Toast.LENGTH_SHORT).show();
                                actualizarCategoria(transaccionList,nombreCategoria);
                                dialog.dismiss();
                                visualizarTransacciones(lastSelectedButton.getText().toString());
                            }
                        });

                        // Añadir el botón al layout
                        layout_cat.addView(categoriaButton);
                    }
                } else {
                    Log.d("Firebase", "Error obteniendo categorías: ", task.getException());
                }
            }
        });
    }

    private void actualizarCategoria(ArrayList<String> transaccionList,String nombreCategoria){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());

        for (String transaccionId : transaccionList) {
            DocumentReference transaccionRef = usuarioRef.collection("transacciones").document(transaccionId);
            transaccionRef.update("etiqueta", nombreCategoria)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d("Firebase", "Categoría actualizada correctamente para la transacción " + transaccionId);
                            newLastSelectedButton[0].setOnClickListener(radioButtonClickListener);
                            transaccionList.clear();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("Firebase", "Error actualizando categoría para la transacción " + transaccionId, e);
                        }
                    });
        }

    }

    private void visualizarTransacciones(String categoria){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        mAuth= FirebaseAuth.getInstance();
        DocumentReference usuarioRef = db.collection("usuarios").document(mAuth.getCurrentUser().getUid());
        // Obtener la referencia a la subcolección "transacciones" del usuario
        CollectionReference transaccionesRef = usuarioRef.collection("transacciones");
        transaccionesRef.whereEqualTo("etiqueta",categoria).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    Log.d(TAG, "Error obteniendo categorias: ", task.getException());
                }
            }
        });
    }
}