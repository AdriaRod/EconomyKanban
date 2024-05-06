package com.econok.economykanban;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.ViewHolder> {

    private List<String> monthList;

    public MonthAdapter(List<String> monthList) {
        this.monthList = monthList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.month_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.monthButton.setText(monthList.get(position));
    }

    @Override
    public int getItemCount() {
        return monthList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public Button monthButton;

        public ViewHolder(View itemView) {
            super(itemView);
            monthButton = itemView.findViewById(R.id.monthButton);
            monthButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle button click here
                }
            });
        }
    }
}
