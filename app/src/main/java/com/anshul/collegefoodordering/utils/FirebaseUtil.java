// FirebaseUtil.java
package com.anshul.collegefoodordering.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseUtil {
    private static FirebaseDatabase mFirebaseDatabase;
    private static DatabaseReference mDatabaseReference;
    private static FirebaseAuth mFirebaseAuth;

    public static void openFbReference(String ref) {
        if (mFirebaseDatabase == null) {
            mFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        mDatabaseReference = mFirebaseDatabase.getReference().child(ref);
    }

    public static DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    public static FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            mFirebaseAuth = FirebaseAuth.getInstance();
        }
        return mFirebaseAuth;
    }
}
