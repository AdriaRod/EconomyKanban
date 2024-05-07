package com.econok.economykanban.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.econok.economykanban.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategoriesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategoriesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //************************+ VARIABLES ************************
    //fecha
    private TextView currentDateTextView;

    //selector de meses
    private RadioButton previousMonthButton;
    private RadioButton currentMonthButton;
    private RadioButton nextMonthButton;
    private int currentMonthIndex = 5; // Junio por defecto
    private ImageView nextButton, previousButton;

    //PopUp Menu para seleccionar (ADD, EDITAR, ELIMINAR)
    ImageView btnPopUp;


    public CategoriesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoriesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategoriesFragment newInstance(String param1, String param2) {
        CategoriesFragment fragment = new CategoriesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_categories, container, false);
        currentDateTextView = view.findViewById(R.id.currentDate);


        //******************************* PARA LOS  MESES *****************************
        // Inicialización de los RadioButtons
        previousMonthButton = view.findViewById(R.id.previous_month);
        currentMonthButton = view.findViewById(R.id.current_month);
        nextMonthButton = view.findViewById(R.id.next_month);

        //Inicializamos los ImageView (que nos sirven de botones de adelante y atras tambien)
        previousButton = view.findViewById(R.id.previousButton);
        nextButton = view.findViewById(R.id.nextButton);

        //Inicializamos el popUp button
        btnPopUp = view.findViewById(R.id.btn_popUpMenu);
        btnPopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });

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

//        //******************** PARA EL POP-UP MENU DE LAS OPCIONES *********************
//        registerForContextMenu(btnPopUp);


        return view;
    }

//    //ESTO SIGUE SIENDO PARA EL POP-UP MENU
//    @Override
//    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        requireActivity().getMenuInflater().inflate(R.menu.categories_popup_menu, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        if (item.getItemId() == R.id.item_1) {
//            Toast.makeText(requireContext(), "Item 1", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (item.getItemId() == R.id.item_2) {
//            Toast.makeText(requireContext(), "Item 2", Toast.LENGTH_SHORT).show();
//            return true;
//        } else if (item.getItemId() == R.id.item_3) {
//            Toast.makeText(requireContext(), "Item 3", Toast.LENGTH_SHORT).show();
//            return true;
//        }
//        return super.onContextItemSelected(item);
//    }

    public void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.inflate(R.menu.categories_popup_menu);

        // Maneja los clics de los elementos del menú
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.item_1) {
                    // Código para la acción de editar
                    return true;
                } else if (itemId == R.id.item_2) {
                    // Código para la acción de eliminar
                    return true;
                } else if (itemId == R.id.item_3) {
                    // Código para la acción de compartir
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