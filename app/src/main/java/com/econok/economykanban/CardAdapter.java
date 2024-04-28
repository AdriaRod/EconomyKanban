package com.econok.economykanban;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    private List<CardItem> cardList;
    private Context context;

    public CardAdapter(Context context, List<CardItem> cardList) {
        this.context = context;
        this.cardList = cardList;
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

        holder.titleTextView.setText(currentItem.getTitle());
        holder.transactionTypeTextView.setText(currentItem.getTransactionType());
        holder.transactionTextView.setText(String.valueOf(currentItem.getTransactionNumber()));

        // Cambiar el color del texto según el tipo de transacción
        if (currentItem.getTransactionType().equals("Income")) {
            holder.transactionTypeTextView.setTextColor(Color.parseColor("#32DA6E"));
        } else {
            holder.transactionTypeTextView.setTextColor(Color.parseColor("#ED918A"));
        }
    }



    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView transactionTypeTextView;
        public TextView transactionTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            transactionTypeTextView = itemView.findViewById(R.id.tagTextView);
            transactionTextView = itemView.findViewById(R.id.transactionTextView);
        }
    }
}

