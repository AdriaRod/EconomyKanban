// CategoryAdapter.java
package com.econok.economykanban;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<String> categories;

    public CategoryAdapter(List<String> categories) {
        this.categories = categories != null ? categories : new ArrayList<>();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_select_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryName.setText(category);
        holder.categoryCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Handle category selection here
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void addCategory(String category) {
        categories.add(category);
        notifyItemInserted(categories.size() - 1);
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        CheckBox categoryCheckBox;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            categoryCheckBox = itemView.findViewById(R.id.categoryCheckBox);
        }
    }
}
