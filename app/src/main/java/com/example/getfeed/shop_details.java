package com.example.getfeed;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class shop_details extends AppCompatActivity {

    private Button register;
    private EditText shopName;
    private EditText ownerName;
    private EditText address;
    private EditText phno;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String detail_id;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shop_details_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        shopName = findViewById(R.id.shop_name);
        ownerName = findViewById(R.id.shop_owner);
        address = findViewById(R.id.shop_addr);
        phno = findViewById(R.id.shop_phno);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Generate a new document ID
        detail_id = firestore.collection("shopDetails").document().getId();

        register = findViewById(R.id.shop_details_reg_btn);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(shop_details.this, R.anim.animation1);
                register.startAnimation(fadeIn);
                String textShopName = shopName.getText().toString().trim();
                String textOwnerName = ownerName.getText().toString().trim();
                String textAddress = address.getText().toString().trim();
                String textPhno = phno.getText().toString().trim();

                String mobileRegex = "[6-9][0-9]{9}";
                Matcher matcher;
                Pattern pattern = Pattern.compile(mobileRegex);
                matcher = pattern.matcher(textPhno);

                if (TextUtils.isEmpty(textShopName)) {
                    Toast.makeText(shop_details.this, "Please enter your shop name", Toast.LENGTH_LONG).show();
                    shopName.setError("Shop name is required!");
                    shopName.requestFocus();
                } else if (TextUtils.isEmpty(textOwnerName)) {
                    Toast.makeText(shop_details.this, "Please enter owner name!", Toast.LENGTH_LONG).show();
                    ownerName.setError("Owner name is required!");
                    ownerName.requestFocus();
                } else if (TextUtils.isEmpty(textAddress)) {
                    Toast.makeText(shop_details.this, "Please enter your shop address!", Toast.LENGTH_LONG).show();
                    address.setError("Shop address is required!");
                    address.requestFocus();
                } else if (TextUtils.isEmpty(textPhno)) {
                    Toast.makeText(shop_details.this, "Please enter your mobile number", Toast.LENGTH_LONG).show();
                    phno.setError("Mobile number is required!");
                    phno.requestFocus();
                } else if (!matcher.find()) {
                    Toast.makeText(shop_details.this, "Please re-enter your mobile number", Toast.LENGTH_LONG).show();
                    phno.setError("Mobile number is invalid");
                    phno.requestFocus();
                } else {
                    if (mAuth.getCurrentUser() != null) {
                        user_id = mAuth.getCurrentUser().getUid(); // Fetch the user ID of the current user
                        Log.d(TAG, "User ID: " + user_id);
                        Map<String, Object> user = new HashMap<>();
                        user.put("shop_name", textShopName);
                        user.put("owner_name", textOwnerName);
                        user.put("address", textAddress);
                        user.put("shop_phno", textPhno);
                        //user.put("shop_id", user_id); // Include the user ID in the Firestore document

                        if (detail_id == null) {
                            Log.e(TAG, "detail_id is null");
                            Toast.makeText(shop_details.this, "Error: detail_id is null", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        DocumentReference documentReference = firestore.collection("shops").document(user_id);

                        documentReference.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(shop_details.this, "Inserted into Collection", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(shop_details.this, setQuestions.class);
                                startActivity(intent);
                                finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Error inserting document", e);
                                Toast.makeText(shop_details.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Log.e(TAG, "User not authenticated");
                        Toast.makeText(shop_details.this, "User not authenticated", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}