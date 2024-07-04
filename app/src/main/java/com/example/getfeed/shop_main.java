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

public class shop_main extends AppCompatActivity {

    public RelativeLayout register;
    public RelativeLayout login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.shop_main_activity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        register =  findViewById(R.id.shop_register_main);
        login = findViewById(R.id.shop_login_main);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Animation fadeIn = AnimationUtils.loadAnimation(shop_main.this,R.anim.animation1);
                register.startAnimation(fadeIn);

                Intent intent = new Intent(shop_main.this,shop_register.class);
                startActivity(intent);

            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(shop_main.this,R.anim.animation1);
                login.startAnimation(fadeIn);
                Intent intent = new Intent(shop_main.this,shop_login.class);
                startActivity(intent);

            }
        });
    }
}