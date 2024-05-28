package com.econok.economykanban;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<CardItem> cardList;
    private Context context;
    private List<CardItem> selectedItems = new ArrayList<>();
    private boolean isEditModeEnabled = false;
    private String currencySymbol; // Variable para almacenar el símbolo de moneda

    public CardAdapter(Context context, List<CardItem> cardList) {
        this.context = context;
        this.cardList = cardList;
    }

    public void setEditModeEnabled(boolean enabled) {
        isEditModeEnabled = enabled;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_card_layout, parent, false);
        return new CardViewHolder(view);


    }

    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        CardItem currentItem = cardList.get(position);

        //MONEDA





        holder.titleTextView.setText(currentItem.getTitle());

        String transactionTypeText;
        if (currentItem.getTransactionType().equals("Income")) {
            transactionTypeText = context.getString(R.string.income);
            holder.transactionTypeTextView.setTextColor(Color.parseColor("#32DA6E"));
        } else {
            transactionTypeText = context.getString(R.string.expense);
            holder.transactionTypeTextView.setTextColor(Color.parseColor("#ED918A"));
        }
        holder.transactionTypeTextView.setText(transactionTypeText);

        holder.transactionTextView.setText(String.valueOf(currentItem.getTransactionNumber()));

        holder.fechaTextview.setText(String.valueOf(currentItem.getFecha()));
        holder.monedaTextView.setText(currencySymbol);



        // Configurar la selección del elemento si está en modo de edición
        if (isEditModeEnabled) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleSelection(currentItem, holder);
                }
            });

            // Cambiar el color de fondo si el elemento está seleccionado
            if (selectedItems.contains(currentItem)) {
                holder.itemView.setBackgroundColor(Color.parseColor("#FF4081"));
            } else {
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        } else {
            // Cuando no está en modo de edición, eliminar el listener y restablecer el color de fondo
            holder.itemView.setOnClickListener(null);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    private void toggleSelection(CardItem item, CardViewHolder holder) {
        if (selectedItems.contains(item)) {
            // Si el elemento ya está seleccionado, quítalo de la lista de elementos seleccionados y cambia el color de fondo
            selectedItems.remove(item);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        } else {
            // Si el elemento no está seleccionado, agrégalo a la lista de elementos seleccionados y cambia el color de fondo
            selectedItems.add(item);
            holder.itemView.setBackgroundColor(Color.parseColor("#FF4081"));
        }
    }

    // Restablecer la lista de elementos seleccionados cuando salgas del modo de edición
    public void resetSelectedItems() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public List<CardItem> getSelectedItems() {
        return selectedItems;
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView transactionTypeTextView;
        public TextView transactionTextView;
        public TextView fechaTextview;
        public TextView monedaTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.currentDateTransactions);
            transactionTypeTextView = itemView.findViewById(R.id.tagTextView);
            transactionTextView = itemView.findViewById(R.id.transactionTextView);
            fechaTextview=itemView.findViewById(R.id.small_date);
            monedaTextView=itemView.findViewById(R.id.divisaTextView);
        }
    }

    // Método para actualizar el símbolo de la moneda
    public void updateCurrencySymbol(String currencySymbol) {
        this.currencySymbol = currencySymbol;
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }


}
