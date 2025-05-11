// MenuActivity.java
package com.anshul.collegefoodordering.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.adapters.MenuItemAdapter;
import com.anshul.collegefoodordering.models.Order;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.anshul.collegefoodordering.models.MenuItem;
// NOT import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView rvMenuItems;
    private MenuItemAdapter adapter;
    // Change raw types to parameterized types
    private List<com.anshul.collegefoodordering.models.MenuItem> menuItems;
    private List<com.anshul.collegefoodordering.models.MenuItem> selectedItems;
    private ProgressBar progressBar;
    private TextView tvNoItems, tvVendorName, tvTotalAmount;
    private Button btnPlaceOrder;
    private String vendorId, vendorName;
    private double totalAmount = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        vendorId = getIntent().getStringExtra("vendorUid");
        vendorName = getIntent().getStringExtra("vendorName");

        rvMenuItems = findViewById(R.id.rv_menu_items);
        progressBar = findViewById(R.id.progress_bar);
        tvNoItems = findViewById(R.id.tv_no_items);
        tvVendorName = findViewById(R.id.tv_vendor_name);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        btnPlaceOrder = findViewById(R.id.btn_place_order);

        tvVendorName.setText(vendorName);
        tvTotalAmount.setText("Total: $0.00");

        menuItems = new ArrayList<>();
        selectedItems = new ArrayList<>();
        adapter = new MenuItemAdapter(this, menuItems);
        rvMenuItems.setLayoutManager(new LinearLayoutManager(this));
        rvMenuItems.setAdapter(adapter);

        loadMenuItems();

        // Set up item selection listener
        adapter.setOnItemSelectListener(new MenuItemAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelected(com.anshul.collegefoodordering.models.MenuItem item, boolean isSelected) {
                if (isSelected) {
                    selectedItems.add(item);
                    totalAmount += item.getPrice();
                } else {
                    selectedItems.remove(item);
                    totalAmount -= item.getPrice();
                }
                tvTotalAmount.setText(String.format("Total: $%.2f", totalAmount));

                // Enable/disable place order button
                btnPlaceOrder.setEnabled(!selectedItems.isEmpty());
            }
        });

        btnPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_screen_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    if (item != null && item.isAvailable()) {
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
            }
        });
    }

    private void placeOrder() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Please select at least one item", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        btnPlaceOrder.setEnabled(false);

        String orderId = UUID.randomUUID().toString();
        String studentId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Order order = new Order(orderId, studentId, vendorId, selectedItems, totalAmount);

        FirebaseUtil.openFbReference("orders");
        DatabaseReference orderRef = FirebaseUtil.getDatabaseReference();

        orderRef.child(orderId).setValue(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressBar.setVisibility(View.GONE);

                if (task.isSuccessful()) {
                    Toast.makeText(MenuActivity.this, "Order placed successfully", Toast.LENGTH_SHORT).show();

                    // Start a timer to check if the order is accepted within 1 minute
                    startOrderTimer(orderId);

                    // Navigate to order detail activity
                    Intent intent = new Intent(MenuActivity.this, OrderDetailActivity.class);
                    intent.putExtra("orderId", orderId);
                    startActivity(intent);
                    finish();
                } else {
                    btnPlaceOrder.setEnabled(true);
                    Toast.makeText(MenuActivity.this, "Failed to place order: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startOrderTimer(final String orderId) {
        // Set up a timer to check if the order is accepted within 1 minute
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkOrderStatus(orderId);
            }
        }, 60000); // 60 seconds
    }

    private void checkOrderStatus(final String orderId) {
        FirebaseUtil.openFbReference("orders");
        DatabaseReference orderRef = FirebaseUtil.getDatabaseReference().child(orderId);

        orderRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Order order = dataSnapshot.getValue(Order.class);

                    if (order != null && order.getStatus().equals("pending")) {
                        // Order is still pending after 1 minute, cancel it
                        order.setStatus("cancelled");
                        dataSnapshot.getRef().setValue(order);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }
}
