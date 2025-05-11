package com.anshul.collegefoodordering.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.adapters.OrderAdapter;
import com.anshul.collegefoodordering.models.Order;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class VendorDashboardActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private List<Order> orderList;
    private ProgressBar progressBar;
    private TextView tvNoOrders;
    private String vendorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rvOrders = findViewById(R.id.rv_orders);
        progressBar = findViewById(R.id.progress_bar);
        tvNoOrders = findViewById(R.id.tv_no_orders);

        vendorId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        orderList = new ArrayList<>();
        adapter = new OrderAdapter(this, orderList, true); // true for vendor view
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);

        loadOrders();

        // Set up action listeners
        adapter.setOnOrderActionListener(new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(Order order) {
                updateOrderStatus(order.getId(), "accepted");
            }

            @Override
            public void onReject(Order order) {
                updateOrderStatus(order.getId(), "rejected");
            }

            @Override
            public void onPrepared(Order order) {
                updateOrderStatus(order.getId(), "prepared");
            }

            @Override
            public void onDelivered(Order order) {
                updateOrderStatus(order.getId(), "delivered");
            }
        });
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtil.openFbReference("orders");
        DatabaseReference ordersRef = FirebaseUtil.getDatabaseReference();

        ordersRef.orderByChild("vendorId").equalTo(vendorId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        orderList.add(order);
                    }
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (orderList.isEmpty()) {
                    tvNoOrders.setVisibility(View.VISIBLE);
                } else {
                    tvNoOrders.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateOrderStatus(String orderId, String status) {
        FirebaseUtil.openFbReference("orders");
        DatabaseReference orderRef = FirebaseUtil.getDatabaseReference().child(orderId);

        orderRef.child("status").setValue(status);

        if (status.equals("accepted") || status.equals("rejected")) {
            orderRef.child("responseTime").setValue(new Date());
        } else if (status.equals("delivered")) {
            orderRef.child("deliveryTime").setValue(new Date());
        }

        Toast.makeText(this, "Order " + status, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.vendor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_manage_menu) {
            startActivity(new Intent(this, ManageMenuActivity.class));
            return true;
        } else if (id == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
