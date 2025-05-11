// VendorListAdapter.java
package com.anshul.collegefoodordering.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.models.Vendor;

import java.util.List;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.VendorViewHolder> {

    private Context context;
    private List<Vendor> vendors;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Vendor vendor);
    }

    public VendorListAdapter(Context context, List<Vendor> vendors) {
        this.context = context;
        this.vendors = vendors;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public VendorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vendor, parent, false);
        return new VendorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendorViewHolder holder, int position) {
        Vendor vendor = vendors.get(position);
        holder.tvVendorName.setText(vendor.getName());
        holder.tvVendorDescription.setText(vendor.getDescription());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onItemClick(vendor);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return vendors.size();
    }

    static class VendorViewHolder extends RecyclerView.ViewHolder {
        TextView tvVendorName, tvVendorDescription;

        VendorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVendorName = itemView.findViewById(R.id.tv_vendor_name);
            tvVendorDescription = itemView.findViewById(R.id.tv_vendor_description);
        }
    }
}
