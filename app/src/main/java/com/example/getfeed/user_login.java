package com.example.getfeed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class user_login extends AppCompatActivity {
    private Button login;
    private TextView register;
    private EditText email;
    private EditText password;
    private static final String TAG = "user_login";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_login_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = FirebaseFirestore.getInstance();
        email = findViewById(R.id.user_login_email);
        password = findViewById(R.id.user_login_pass);

        login = findViewById(R.id.user_login_btn);
        register = findViewById(R.id.user_login_reg);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(user_login.this, R.anim.animation1);
                register.startAnimation(fadeIn);
                Intent intent = new Intent(user_login.this, user_register.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(user_login.this, R.anim.animation1);
                login.startAnimation(fadeIn);

                String textEmail = email.getText().toString().trim();
                String textpass = password.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(user_login.this, "Please enter your email!", Toast.LENGTH_LONG).show();
                    email.setError("Email is required!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(user_login.this, "Please re-enter your email!", Toast.LENGTH_LONG).show();
                    email.setError("Enter a valid email");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(textpass)) {
                    Toast.makeText(user_login.this, "Please enter your password!", Toast.LENGTH_LONG).show();
                    password.setError("Password is required!");
                    password.requestFocus();
                } else if (textpass.length() < 6) {
                    Toast.makeText(user_login.this, "Please enter a 6-digit password!", Toast.LENGTH_LONG).show();
                    password.setError("Password is too short!");
                    password.requestFocus();
                } else {
                    checkUserCredentials(textEmail, textpass);
                }
            }
        });
    }

    private void checkUserCredentials(String email, String password) {
        db.collection("users")
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            // User found with matching credentials
                            Log.d(TAG, "Login success");
                            Toast.makeText(user_login.this, "Login successful", Toast.LENGTH_SHORT).show();
                            navigateToNextActivity(email);
                        } else {
                            // User not found or incorrect credentials
                            Log.w(TAG, "Login failed: User not found or incorrect credentials");
                            Toast.makeText(user_login.this, "Login failed: Incorrect email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void navigateToNextActivity(String email) {
        Intent intent = new Intent(user_login.this,DisplayShopsActivity.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}