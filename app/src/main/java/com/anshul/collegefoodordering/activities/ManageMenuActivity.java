package com.anshul.collegefoodordering.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.adapters.MenuManagementAdapter;
import com.anshul.collegefoodordering.models.MenuItem;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ManageMenuActivity extends AppCompatActivity {

    private RecyclerView rvMenuItems;
    private MenuManagementAdapter adapter;
    private List<MenuItem> menuItems;
    private ProgressBar progressBar;
    private TextView tvNoItems;
    private Button btnAddItem;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_menu);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("College Food Ordering");


        rvMenuItems = findViewById(R.id.rv_menu_items);
        progressBar = findViewById(R.id.progress_bar);
        tvNoItems = findViewById(R.id.tv_no_items);
        btnAddItem = findViewById(R.id.btn_add_item);

        vendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        menuItems = new ArrayList<>();
        adapter = new MenuManagementAdapter(this, menuItems);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.setAdapter(adapter);

        loadMenuItems();

        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddItemDialog();
            }
        });

        // Set up item management listeners
        adapter.setOnItemActionListener(new MenuManagementAdapter.OnItemActionListener() {
            @Override
            public void onEdit(MenuItem item) {
                showEditItemDialog(item);
            }

            @Override
            public void onToggleAvailability(MenuItem item, boolean isAvailable) {
                updateItemAvailability(item.getId(), isAvailable);
            }

            @Override
            public void onDelete(MenuItem item) {
                showDeleteConfirmationDialog(item);
            }
        });
    }

    private void loadMenuItems() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtil.openFbReference("menuItems");
        DatabaseReference menuRef = FirebaseUtil.getDatabaseReference();

        menuRef.orderByChild("vendorId").equalTo(vendorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menuItems.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    MenuItem item = snapshot.getValue(MenuItem.class);
                    if (item != null) {
                        menuItems.add(item);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (menuItems.isEmpty()) {
                    tvNoItems.setVisibility(View.VISIBLE);
                } else {
                    tvNoItems.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageMenuActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        final EditText etItemName = dialogView.findViewById(R.id.et_item_name);
        final EditText etItemDescription = dialogView.findViewById(R.id.et_item_description);
        final EditText etItemPrice = dialogView.findViewById(R.id.et_item_price);
        final Switch switchAvailable = dialogView.findViewById(R.id.switch_available);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);

        tvTitle.setText("Add New Menu Item");
        switchAvailable.setChecked(true);

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etItemName.getText().toString().trim();
                String description = etItemDescription.getText().toString().trim();
                String priceStr = etItemPrice.getText().toString().trim();

                if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(ManageMenuActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ManageMenuActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean available = switchAvailable.isChecked();
                String itemId = UUID.randomUUID().toString();

                MenuItem newItem = new MenuItem(itemId, vendorId, name, description, price, available);
                addMenuItem(newItem);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showEditItemDialog(final MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_edit_item, null);
        builder.setView(dialogView);

        final EditText etItemName = dialogView.findViewById(R.id.et_item_name);
        final EditText etItemDescription = dialogView.findViewById(R.id.et_item_description);
        final EditText etItemPrice = dialogView.findViewById(R.id.et_item_price);
        final Switch switchAvailable = dialogView.findViewById(R.id.switch_available);
        Button btnSave = dialogView.findViewById(R.id.btn_save);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        TextView tvTitle = dialogView.findViewById(R.id.tv_dialog_title);

        tvTitle.setText("Edit Menu Item");
        etItemName.setText(item.getName());
        etItemDescription.setText(item.getDescription());
        etItemPrice.setText(String.valueOf(item.getPrice()));
        switchAvailable.setChecked(item.isAvailable());

        final AlertDialog dialog = builder.create();
        dialog.show();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etItemName.getText().toString().trim();
                String description = etItemDescription.getText().toString().trim();
                String priceStr = etItemPrice.getText().toString().trim();

                if (name.isEmpty() || description.isEmpty() || priceStr.isEmpty()) {
                    Toast.makeText(ManageMenuActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceStr);
                } catch (NumberFormatException e) {
                    Toast.makeText(ManageMenuActivity.this, "Please enter a valid price", Toast.LENGTH_SHORT).show();
                    return;
                }

                boolean available = switchAvailable.isChecked();

                // Update the item
                item.setName(name);
                item.setDescription(description);
                item.setPrice(price);
                item.setAvailable(available);

                updateMenuItem(item);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void showDeleteConfirmationDialog(final MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Item");
        builder.setMessage("Are you sure you want to delete " + item.getName() + "?");

        builder.setPositiveButton("Delete", (dialog, which) -> {
            deleteMenuItem(item.getId());
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.dismiss();
        });

        builder.create().show();
    }

    private void addMenuItem(MenuItem item) {
        FirebaseUtil.openFbReference("menuItems");
        DatabaseReference menuRef = FirebaseUtil.getDatabaseReference();

        menuRef.child(item.getId()).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageMenuActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageMenuActivity.this, "Failed to add item: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateMenuItem(MenuItem item) {
        FirebaseUtil.openFbReference("menuItems");
        DatabaseReference menuRef = FirebaseUtil.getDatabaseReference();

        menuRef.child(item.getId()).setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageMenuActivity.this, "Item updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageMenuActivity.this, "Failed to update item: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateItemAvailability(String itemId, boolean available) {
        FirebaseUtil.openFbReference("menuItems");
        DatabaseReference menuRef = FirebaseUtil.getDatabaseReference();

        menuRef.child(itemId).child("available").setValue(available).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    String status = available ? "available" : "unavailable";
                    Toast.makeText(ManageMenuActivity.this, "Item marked as " + status, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageMenuActivity.this, "Failed to update item: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void deleteMenuItem(String itemId) {
        FirebaseUtil.openFbReference("menuItems");
        DatabaseReference menuRef = FirebaseUtil.getDatabaseReference();

        menuRef.child(itemId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ManageMenuActivity.this, "Item deleted successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ManageMenuActivity.this, "Failed to delete item: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
