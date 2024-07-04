package com.example.getfeed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class shop_register extends AppCompatActivity {

    private Button con_tinue;
    private TextView login;
    private EditText email,password,confirm_password;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String shop_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shop_register_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.shop_register_email);
        password = findViewById(R.id.shop_register_pass);
        confirm_password = findViewById(R.id.shop_register_cpass);

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();


        con_tinue = findViewById(R.id.shop_continue_btn);
        con_tinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation fadeIn = AnimationUtils.loadAnimation(shop_register.this,R.anim.animation1);
                con_tinue.startAnimation(fadeIn);

                String textEmail = email.getText().toString().trim();
                String textPass = password.getText().toString().trim();
                String textCPass = confirm_password.getText().toString().trim();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(shop_register.this,"Please enter your email!",Toast.LENGTH_LONG).show();
                    email.setError("email is required!");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(shop_register.this,"Please re-enter your email!",Toast.LENGTH_LONG).show();
                    email.setError("Valid email is required!");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(textPass)){
                    Toast.makeText(shop_register.this,"Please enter your password!",Toast.LENGTH_LONG).show();
                    password.setError("password is required!");
                    password.requestFocus();
                }
                else if(textPass.length()<6){
                    Toast.makeText(shop_register.this,"Password is too  short!",Toast.LENGTH_LONG).show();
                    password.setError("Password should be atleast 6 digits!");
                    password.requestFocus();
                }
                else if(TextUtils.isEmpty(textCPass)){
                    Toast.makeText(shop_register.this,"Please confirm your password!",Toast.LENGTH_LONG).show();
                    confirm_password.setError("confirmation of your password is required!");
                    confirm_password.requestFocus();
                }
                else if(!textPass.equals(textCPass)){
                    Toast.makeText(shop_register.this,"Your password doesn't match!",Toast.LENGTH_LONG).show();
                    confirm_password.setError("Re-type the password correctly!");
                    confirm_password.requestFocus();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(textEmail,textPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                shop_id = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection("shops").document(shop_id);
                                Map<String, Object> user = new HashMap<>();
                                user.put("email",textEmail);
                                user.put("password",textPass);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(shop_register.this,"Inserted to collection",Toast.LENGTH_SHORT).show();
                                    }
                                });

                                email.setText("");
                                password.setText("");
                                confirm_password.setText("");
                                Toast.makeText(shop_register.this,"Signup Successful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(shop_register.this,"Signup Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    Intent intent = new Intent(shop_register.this,shop_details.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        login = findViewById(R.id.shop_reg_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(shop_register.this,R.anim.animation1);
                login.startAnimation(fadeIn);
                Intent intent = new Intent(shop_register.this, shop_login.class);
                startActivity(intent);

            }
        });

    }
}