package com.econok.economykanban.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.econok.economykanban.CardAdapter;
import com.econok.economykanban.CardItem;
import com.econok.economykanban.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;
    private MaterialButton addTransaction, button1, button2;
    private Dialog dialog;
    private EditText conceptEditText, quantityEditText;
    private Button acceptButton;
    private Boolean isIncome = null;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardList = new ArrayList<>();
        cardList.add(new CardItem("Título 1", "Income", "Comida", "342"));
        cardList.add(new CardItem("Título 2", "Expense", "Viaje", "213"));
        cardList.add(new CardItem("Título 3", "Income", "Comida", "534"));
        cardList.add(new CardItem("Título 4", "Expense", "Comida", "432"));
        cardList.add(new CardItem("Título 5", "Income", "Viaje", "123"));
        cardList.add(new CardItem("Titulo 6", "Income", "Comida", "293"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        addTransaction = view.findViewById(R.id.addBtn);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new CardAdapter(getContext(), cardList);
        recyclerView.setAdapter(adapter);

        // Inicializar el Dialog
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_add_item);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

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
                button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonIncome));
                button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonsExpense));
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
                button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonsExpense));
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
                button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonIncome));
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
                    String type = isIncome ? "Income" : "Expense";
                    cardList.add(new CardItem(concept, type, concept, quantity));
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                    // Limpiar los campos del diálogo al guardar
                    conceptEditText.setText("");
                    quantityEditText.setText("");
                    // Resetear el estado del botón
                    isIncome = null;
                    button1.setEnabled(true);
                    button2.setEnabled(true);
                    button1.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonIncome));
                    button2.setBackgroundTintList(getResources().getColorStateList(R.color.dialogButtonsExpense));
                    button1.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                    button2.setTextColor(getResources().getColor(R.color.dialogTextNotPressed));
                } else {
                    Toast.makeText(getContext(), getString(R.string.noContentToast), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
