package com.econok.economykanban;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
        holder.tagTextView.setText(currentItem.getTag());
        holder.transactionTextView.setText(String.valueOf(currentItem.getTransaction()));
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }

    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView tagTextView;
        public TextView transactionTextView;

        public CardViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            tagTextView = itemView.findViewById(R.id.tagTextView);
            transactionTextView = itemView.findViewById(R.id.transactionTextView);
        }
    }
}

