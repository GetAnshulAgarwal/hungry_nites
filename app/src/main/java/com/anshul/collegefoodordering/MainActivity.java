// MainActivity.java
package com.anshul.collegefoodordering;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anshul.collegefoodordering.activities.LoginActivity;
import com.anshul.collegefoodordering.activities.StudentDashboardActivity;
import com.anshul.collegefoodordering.activities.VendorDashboardActivity;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.anshul.collegefoodordering.utils.NotificationHelper;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 100; // You can use any integer value


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (BuildConfig.DEBUG) {
            // Connect to Firebase emulators
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 19199);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9399);
        }

        // Create notification channel
        NotificationHelper.createNotificationChannel(this);

        // Request notification permission for Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION);
            }
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FCM", "Token: " + token);

                    // Save token to Firebase if user is logged in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        FirebaseDatabase.getInstance().getReference("fcmTokens")
                                .child(user.getUid())
                                .setValue(token);
                    }
                });


            mAuth = FirebaseUtil.getFirebaseAuth();

        // Check if user is already logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is logged in, check user type
            checkUserTypeAndNavigate(currentUser.getUid());
        } else {
            // User is not logged in, navigate to login screen
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void checkUserTypeAndNavigate(String userId) {
        FirebaseUtil.openFbReference("users");
        DatabaseReference userRef = FirebaseUtil.getDatabaseReference().child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userType = dataSnapshot.child("userType").getValue(String.class);

                    if (userType != null) {
                        if (userType.equals("student")) {
                            startActivity(new Intent(MainActivity.this, StudentDashboardActivity.class));
                        } else if (userType.equals("vendor")) {
                            startActivity(new Intent(MainActivity.this, VendorDashboardActivity.class));
                        } else {
                            // Unknown user type, log out and go to login
                            mAuth.signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        }
                    } else {
                        // User type not found, log out and go to login
                        mAuth.signOut();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                } else {
                    // User data not found, log out and go to login
                    mAuth.signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }

                finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error occurred, log out and go to login
                mAuth.signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
    }
}
