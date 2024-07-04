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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class shop_login extends AppCompatActivity {

    private static final String TAG = "shop_login";
    private Button login;
    private TextView register;
    private EditText email;
    private EditText password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shop_login_activity);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Link UI elements
        email = findViewById(R.id.shop_login_email);
        password = findViewById(R.id.shop_login_pass);
        login = findViewById(R.id.shop_login_btn);
        register = findViewById(R.id.shop_login_reg);

        // Set register button click listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(shop_login.this, R.anim.animation1);
                register.startAnimation(fadeIn);
                Intent intent = new Intent(shop_login.this, shop_register.class);
                startActivity(intent);
            }
        });

        // Set login button click listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(shop_login.this, R.anim.animation1);
                login.startAnimation(fadeIn);

                String textEmail = email.getText().toString().trim();
                String textPass = password.getText().toString().trim();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(shop_login.this, "Please enter your email!", Toast.LENGTH_LONG).show();
                    email.setError("Email is required!");
                    email.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(shop_login.this, "Please re-enter your email!", Toast.LENGTH_LONG).show();
                    email.setError("Enter a valid email");
                    email.requestFocus();
                } else if (TextUtils.isEmpty(textPass)) {
                    Toast.makeText(shop_login.this, "Please enter your password!", Toast.LENGTH_LONG).show();
                    password.setError("Password is required!");
                    password.requestFocus();
                } else if (textPass.length() < 6) {
                    Toast.makeText(shop_login.this, "Please enter a 6-digit password!", Toast.LENGTH_LONG).show();
                    password.setError("Password is too short!");
                    password.requestFocus();
                } else {
                    signInWithEmailAndPassword(textEmail, textPass);
                }
            }
        });
    }

    private void signInWithEmailAndPassword(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            navigateToNextActivity(user.getEmail());
                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(shop_login.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToNextActivity(String email) {
        Intent intent = new Intent(shop_login.this, setQuestions.class);
        intent.putExtra("email", email);
        startActivity(intent);
        finish();
    }
}