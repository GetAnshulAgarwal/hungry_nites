// OrderDetailActivity.java
package com.anshul.collegefoodordering.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.adapters.MenuItemAdapter;
import com.anshul.collegefoodordering.models.MenuItem;
import com.anshul.collegefoodordering.models.Order;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView tvOrderId, tvOrderStatus, tvOrderTime, tvTotalAmount, tvCountdown;
    private RecyclerView rvOrderItems;
    private ProgressBar progressBar;
    private Button btnAccept, btnReject, btnPrepared, btnDelivered;

    private String orderId;
    private Order currentOrder;
    private String currentUserId;
    private boolean isVendor;
    private CountDownTimer countDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getStringExtra("orderId");
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tvOrderId = findViewById(R.id.tv_order_id);
        tvOrderStatus = findViewById(R.id.tv_order_status);
        tvOrderTime = findViewById(R.id.tv_order_time);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvCountdown = findViewById(R.id.tv_countdown);
        rvOrderItems = findViewById(R.id.rv_order_items);
        progressBar = findViewById(R.id.progress_bar);
        btnAccept = findViewById(R.id.btn_accept);
        btnReject = findViewById(R.id.btn_reject);
        btnPrepared = findViewById(R.id.btn_prepared);
        btnDelivered = findViewById(R.id.btn_delivered);

        rvOrderItems.setLayoutManager(new LinearLayoutManager(this));

        loadOrderDetails();

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus("accepted");
            }
        });

        btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus("rejected");
            }
        });

        btnPrepared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus("prepared");
            }
        });

        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateOrderStatus("delivered");
            }
        });
    }

    private void loadOrderDetails() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtil.openFbReference("orders");
        DatabaseReference orderRef = FirebaseUtil.getDatabaseReference().child(orderId);

        orderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentOrder = dataSnapshot.getValue(Order.class);

                    if (currentOrder != null) {
                        // Determine if current user is the vendor
                        isVendor = currentOrder.getVendorId().equals(currentUserId);

                        // Update UI with order details
                        tvOrderId.setText("Order #" + currentOrder.getId().substring(0, 8));
                        tvOrderStatus.setText("Status: " + currentOrder.getStatus());

                        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm:ss", Locale.getDefault());
                        tvOrderTime.setText("Ordered: " + sdf.format(currentOrder.getOrderTime()));

                        tvTotalAmount.setText(String.format("Total: $%.2f", currentOrder.getTotalAmount()));

                        // Set up items adapter
                        MenuItemAdapter adapter = new MenuItemAdapter(OrderDetailActivity.this,
                                currentOrder.getItems(), false); // false for non-selectable view
                        rvOrderItems.setAdapter(adapter);

                        // Show/hide action buttons based on user role and order status
                        updateActionButtons();

                        // Start countdown if order is pending
                        if (currentOrder.getStatus().equals("pending")) {
                            startCountdown(currentOrder.getOrderTime());
                        } else {
                            tvCountdown.setVisibility(View.GONE);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                        }
                    }
                }

                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void updateActionButtons() {
        // Only show action buttons to vendor
        if (isVendor) {
            // Show/hide buttons based on order status
            String status = currentOrder.getStatus();

            switch (status) {
                case "pending":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnReject.setVisibility(View.VISIBLE);
                    btnPrepared.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    break;
                case "accepted":
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                    btnPrepared.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.GONE);
                    break;
                case "prepared":
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                    btnPrepared.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.VISIBLE);
                    break;
                default:
                    btnAccept.setVisibility(View.GONE);
                    btnReject.setVisibility(View.GONE);
                    btnPrepared.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    break;
            }
        } else {
            // Hide all action buttons for student
            btnAccept.setVisibility(View.GONE);
            btnReject.setVisibility(View.GONE);
            btnPrepared.setVisibility(View.GONE);
            btnDelivered.setVisibility(View.GONE);
        }
    }

    private void updateOrderStatus(String status) {
        FirebaseUtil.openFbReference("orders");
        DatabaseReference orderRef = FirebaseUtil.getDatabaseReference().child(orderId);

        orderRef.child("status").setValue(status);

        if (status.equals("accepted") || status.equals("rejected")) {
            orderRef.child("responseTime").setValue(new Date());
        } else if (status.equals("delivered")) {
            orderRef.child("deliveryTime").setValue(new Date());
        }
    }

    private void startCountdown(Date orderTime) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        long orderTimeMs = orderTime.getTime();
        long currentTimeMs = System.currentTimeMillis();
        long elapsedTimeMs = currentTimeMs - orderTimeMs;
        long remainingTimeMs = 60000 - elapsedTimeMs; // 60 seconds (1 minute)

        if (remainingTimeMs <= 0) {
            tvCountdown.setText("Time expired");
            return;
        }

        tvCountdown.setVisibility(View.VISIBLE);

        countDownTimer = new CountDownTimer(remainingTimeMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000);
                tvCountdown.setText("Time remaining: " + seconds + " seconds");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Time expired");
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
