package com.example.getfeed;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout shop;
    private RelativeLayout user;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        shop = findViewById(R.id.shop_card);
        user = findViewById(R.id.user_card);


        shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation1);
                shop.startAnimation(fadeIn);

                Intent intent = new Intent(MainActivity.this, shop_main.class);
                startActivity(intent);

            }
        });

        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation fadeIn = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation1);
                user.startAnimation(fadeIn);

                Intent intent = new Intent(MainActivity.this, user.class);
                startActivity(intent);

            }
        });


    }
}