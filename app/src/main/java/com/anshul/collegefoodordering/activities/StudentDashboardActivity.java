// StudentDashboardActivity.java
package com.anshul.collegefoodordering.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;


import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.adapters.VendorListAdapter;
import com.anshul.collegefoodordering.models.Vendor;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardActivity extends AppCompatActivity {

    private RecyclerView rvVendors;
    private VendorListAdapter adapter;
    private List<Vendor> vendorList;
    private ProgressBar progressBar;
    private TextView tvNoVendors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("College Food Ordering");


        rvVendors = findViewById(R.id.rv_vendors);
        progressBar = findViewById(R.id.progress_bar);
        tvNoVendors = findViewById(R.id.tv_no_vendors);

        vendorList = new ArrayList<>();
        adapter = new VendorListAdapter(this, vendorList);
        rvVendors.setLayoutManager(new LinearLayoutManager(this));
        rvVendors.setAdapter(adapter);

        loadVendors();

        // Set up item click listener
        adapter.setOnItemClickListener(new VendorListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Vendor vendor) {
                Intent intent = new Intent(StudentDashboardActivity.this, MenuActivity.class);
                intent.putExtra("vendorUid", vendor.getUid());
                intent.putExtra("vendorName", vendor.getName());
                startActivity(intent);
            }
        });
    }

    private void loadVendors() {
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUtil.openFbReference("users");
        DatabaseReference usersRef = FirebaseUtil.getDatabaseReference();

        usersRef.orderByChild("userType").equalTo("vendor").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                vendorList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Vendor vendor = snapshot.getValue(Vendor.class);
                    if (vendor != null) {
                        vendorList.add(vendor);
                    }
                }

                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);

                if (vendorList.isEmpty()) {
                    tvNoVendors.setVisibility(View.VISIBLE);
                } else {
                    tvNoVendors.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_order_history) {
            startActivity(new Intent(this, OrderHistoryActivity.class));
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
