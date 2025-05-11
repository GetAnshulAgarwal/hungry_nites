// MenuItemAdapter.java
package com.anshul.collegefoodordering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.models.MenuItem;

import java.util.List;

public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.MenuItemViewHolder> {

    private Context context;
    private List<MenuItem> menuItems;
    private boolean selectable;
    private OnItemSelectListener listener;

    public interface OnItemSelectListener {
        void onItemSelected(MenuItem item, boolean isSelected);
    }

    public MenuItemAdapter(Context context, List<MenuItem> menuItems) {
        this(context, menuItems, true);
    }

    public MenuItemAdapter(Context context, List<MenuItem> menuItems, boolean selectable) {
        this.context = context;
        this.menuItems = menuItems;
        this.selectable = selectable;
    }

    public void setOnItemSelectListener(OnItemSelectListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new MenuItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        MenuItem item = menuItems.get(position);
        holder.tvItemName.setText(item.getName());
        holder.tvItemDescription.setText(item.getDescription());
        holder.tvItemPrice.setText(String.format("$%.2f", item.getPrice()));

        if (selectable) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            holder.cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (listener != null) {
                        listener.onItemSelected(item, isChecked);
                    }
                }
            });
        } else {
            holder.cbSelect.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvItemName, tvItemDescription, tvItemPrice;
        CheckBox cbSelect;

        MenuItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItemName = itemView.findViewById(R.id.tv_item_name);
            tvItemDescription = itemView.findViewById(R.id.tv_item_description);
            tvItemPrice = itemView.findViewById(R.id.tv_item_price);
            cbSelect = itemView.findViewById(R.id.cb_select);
        }
    }
}
