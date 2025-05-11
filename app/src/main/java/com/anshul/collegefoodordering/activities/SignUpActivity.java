package com.anshul.collegefoodordering.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.anshul.collegefoodordering.R;
import com.anshul.collegefoodordering.models.Student;
import com.anshul.collegefoodordering.models.Vendor;
import com.anshul.collegefoodordering.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword, etStudentId, etVendorId, etDescription;
    private RadioGroup rgUserType;
    private Button btnSignUp;
    private TextView tvLogin;
    private View studentFields, vendorFields;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etStudentId = findViewById(R.id.et_student_id);
        etVendorId = findViewById(R.id.et_vendor_id);
        etDescription = findViewById(R.id.et_description);
        rgUserType = findViewById(R.id.rg_user_type);
        btnSignUp = findViewById(R.id.btn_sign_up);
        tvLogin = findViewById(R.id.tv_login);
        studentFields = findViewById(R.id.student_fields);
        vendorFields = findViewById(R.id.vendor_fields);

        mAuth = FirebaseUtil.getFirebaseAuth();

        // Show/hide fields based on user type selection
        rgUserType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_student) {
                    studentFields.setVisibility(View.VISIBLE);
                    vendorFields.setVisibility(View.GONE);
                } else {
                    studentFields.setVisibility(View.GONE);
                    vendorFields.setVisibility(View.VISIBLE);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login activity
            }
        });
    }

    private void registerUser() {
        final String name = etName.getText().toString().trim();
        final String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        int selectedId = rgUserType.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        final String userType = radioButton.getText().toString().toLowerCase();

        // Additional validation based on user type
        if (userType.equals("student")) {
            final String studentId = etStudentId.getText().toString().trim();
            if (studentId.isEmpty()) {
                Toast.makeText(this, "Please enter student ID", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            final String vendorId = etVendorId.getText().toString().trim();
            final String description = etDescription.getText().toString().trim();
            if (vendorId.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all vendor fields", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Create user with email and password
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                saveUserToDatabase(user.getUid(), name, email, userType);
                            }
                        } else {
                            Toast.makeText(SignUpActivity.this, "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserToDatabase(String uid, String name, String email, String userType) {
        FirebaseUtil.openFbReference("users");
        DatabaseReference userRef = FirebaseUtil.getDatabaseReference().child(uid);

        if (userType.equals("student")) {
            String studentId = etStudentId.getText().toString().trim();
            Student student = new Student(uid, email, name, studentId);
            userRef.setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Student registered successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to login activity
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            String vendorId = etVendorId.getText().toString().trim();
            String description = etDescription.getText().toString().trim();
            Vendor vendor = new Vendor(uid, email, name, vendorId, description);
            userRef.setValue(vendor).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Vendor registered successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to login activity
                    } else {
                        Toast.makeText(SignUpActivity.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
