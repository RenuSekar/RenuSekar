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

public class user_register extends AppCompatActivity {

    private Button register;
    private TextView login;
    private EditText email;
    private EditText password;
    private EditText confirm_password;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.user_register_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        email = findViewById(R.id.user_register_email);
        password = findViewById(R.id.user_register_pass);
        confirm_password = findViewById(R.id.user_register_cpass);
        register = findViewById(R.id.user_register_btn);

        String textemail = email.getText().toString().trim();
        String textPass = password.getText().toString().trim();
        String textCPass = confirm_password.getText().toString().trim();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(user_register.this,R.anim.animation1);
                register.startAnimation(fadeIn);
                Intent intent = new Intent(user_register.this, MainActivity.class);
                startActivity(intent);

                String textemail = email.getText().toString();
                String textPass = password.getText().toString();
                String textCPass = confirm_password.getText().toString();

                if(TextUtils.isEmpty(textemail)){
                    Toast.makeText(user_register.this,"Please enter your email!",Toast.LENGTH_LONG).show();
                    email.setError("email is required!");
                    email.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textemail).matches()){
                    Toast.makeText(user_register.this,"Please re-enter your email!",Toast.LENGTH_LONG).show();
                    email.setError("Valid email is required!");
                    email.requestFocus();
                }
                else if(TextUtils.isEmpty(textPass)){
                    Toast.makeText(user_register.this,"Please enter your password!",Toast.LENGTH_LONG).show();
                    password.setError("password is required!");
                    password.requestFocus();
                }
                else if(textPass.length()<6){
                    Toast.makeText(user_register.this,"Password is too  short!",Toast.LENGTH_LONG).show();
                    password.setError("Password should be atleast 6 digits!");
                    password.requestFocus();
                }
                else if(TextUtils.isEmpty(textCPass)){
                    Toast.makeText(user_register.this,"Please confirm your password!",Toast.LENGTH_LONG).show();
                    confirm_password.setError("confirmation of your password is required!");
                    confirm_password.requestFocus();
                }
                else if(!textPass.equals(textCPass)){
                    Toast.makeText(user_register.this,"Your password doesn't match!",Toast.LENGTH_LONG).show();
                    confirm_password.setError("Re-type the password correctly!");
                    confirm_password.requestFocus();
                }
                else{
                    mAuth.createUserWithEmailAndPassword(textemail,textPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                user_id = mAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = firestore.collection("users").document(user_id);
                                Map<String,Object> user = new HashMap<>();
                                user.put("email",textemail);
                                user.put("password",textPass);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(user_register.this,"Inserted into collection", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                email.setText("");
                                password.setText("");
                                confirm_password.setText("");
                                Toast.makeText(user_register.this,"Signup successful",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                email.setText("");
                                password.setText("");
                                confirm_password.setText("");
                                Toast.makeText(user_register.this,"Signup failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    Intent intent2 = new Intent(user_register.this, DisplayShopsActivity  .class);
                    startActivity(intent2);
                    finish();

                }
            }
        });

        login = findViewById(R.id.user_reg_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(user_register.this,R.anim.animation1);
                login.startAnimation(fadeIn);
                Intent intent = new Intent(user_register.this, user_login.class);
                startActivity(intent);

            }
        });
    }

}