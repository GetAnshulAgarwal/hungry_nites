package com.anshul.collegefoodordering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.models.MenuItem;

import java.util.List;

public class MenuManagementAdapter extends RecyclerView.Adapter<MenuManagementAdapter.MenuItemViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private OnItemActionListener listener;

    public interface OnItemActionListener {
        void onEdit(MenuItem item);
        void onToggleAvailability(MenuItem item, boolean isAvailable);
        void onDelete(MenuItem item);
    }

    public MenuManagementAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        this.menuItems = menuItems;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu_management, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);

        holder.tvItemName.setText(item.getName());
        holder.tvItemDescription.setText(item.getDescription());
        holder.tvItemPrice.setText(String.format("$%.2f", item.getPrice()));
        holder.switchAvailable.setChecked(item.isAvailable());

        holder.switchAvailable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onToggleAvailability(item, isChecked);
            }
        });

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(item);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDelete(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDescription, tvItemPrice;
        Switch switchAvailable;
        ImageButton btnEdit, btnDelete;

        MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDescription = itemView.findViewById(R.id.tv_item_description);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            switchAvailable = itemView.findViewById(R.id.switch_available);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
