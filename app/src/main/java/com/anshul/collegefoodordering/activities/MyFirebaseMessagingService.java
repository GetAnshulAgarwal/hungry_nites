package com.anshul.collegefoodordering.activities;

import android.content.SharedPreferences;
import android.util.Log;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.anshul.collegefoodordering.utils.NotificationHelper;
import com.anshul.collegefoodordering.models.Order;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // Handle FCM messages here
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains data
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());

            // Extract order information
            String orderId = remoteMessage.getData().get("orderId");
            String status = remoteMessage.getData().get("status");

            // Show notification based on order status
            if (orderId != null && status != null) {
                Order order = new Order();
                order.setId(orderId);
                order.setStatus(status);
                NotificationHelper.sendOrderStatusNotification(getApplicationContext(), order);
            }
        }

        // Check if message contains notification payload
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Notification: " + remoteMessage.getNotification().getBody());
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();

            NotificationHelper.showBasicNotification(getApplicationContext(), title, body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        // If you need to send messages to this device, store this token in Firebase
        Log.d(TAG, "New token: " + token);
        saveTokenToDatabase(token);
    }

    private void saveTokenToDatabase(String token) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is signed in, save the token
            String userId = currentUser.getUid();
            FirebaseDatabase.getInstance().getReference("fcmTokens")
                    .child(userId)
                    .setValue(token);
        } else {
            // User is not signed in, store token temporarily or handle appropriately
            // You could save it to SharedPreferences and update Firebase when user logs in
            saveTokenLocally(token);
        }
    }

    private void saveTokenLocally(String token) {
        SharedPreferences sharedPreferences = getSharedPreferences("FCM", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

}
