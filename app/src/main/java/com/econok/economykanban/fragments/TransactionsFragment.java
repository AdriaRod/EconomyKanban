package com.econok.economykanban.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.econok.economykanban.CardAdapter;
import com.econok.economykanban.CardItem;
import com.econok.economykanban.R;

import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private List<CardItem> cardList;
    private Button addTransaction;
    private Dialog dialog;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cardList = new ArrayList<>();
        cardList.add(new CardItem("Título 1", "Income", "Comida","342"));
        cardList.add(new CardItem("Título 2", "Expense", "Viaje","213"));
        cardList.add(new CardItem("Título 3", "Income", "Comida","534"));
        cardList.add(new CardItem("Título 4", "Expense", "Comida","432"));
        cardList.add(new CardItem("Título 5", "Income", "Viaje","123"));
        cardList.add(new CardItem("Titulo 6","Income","Comida","293"));
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

        // Inicializar el botón closeDialog dentro del Dialog
        Button closeDialog = dialog.findViewById(R.id.closeDialog);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        addTransaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        return view;
    }
}
