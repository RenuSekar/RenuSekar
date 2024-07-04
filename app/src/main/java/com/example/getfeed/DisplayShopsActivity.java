package com.example.getfeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class DisplayShopsActivity extends AppCompatActivity implements ShopAdapter.OnItemClickListener {

    private static final String TAG = "DisplayShopsActivity";

    private RecyclerView recyclerView;
    private ShopAdapter shopAdapter;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_shops_activity);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();

        fetchShopDetails();
    }

    private void fetchShopDetails() {
        firestore.collection("shops")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<Shop> shops = new ArrayList<>();
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                String shopId = document.getId();
                                String shopName = document.getString("shop_name");
                                if (shopName != null) {
                                    Shop shop = new Shop(shopName);
                                    shops.add(shop);
                                }
                            }
                            shopAdapter = new ShopAdapter(shops, DisplayShopsActivity.this);
                            recyclerView.setAdapter(shopAdapter);
                        } else {
                            Toast.makeText(DisplayShopsActivity.this, "No shop details found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving shop details", e);
                        Toast.makeText(DisplayShopsActivity.this, "Error retrieving shop details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        // Handle item click: Get the selected shop and navigate to ShopDetailsActivity
        Shop selectedShop = shopAdapter.getShop(position);
        if (selectedShop != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("selectedShopName", selectedShop.getShopName());
            editor.apply();
            Intent intent = new Intent(DisplayShopsActivity.this, user_question_display.class);
            intent.putExtra("shopName", selectedShop.getShopName());
            startActivity(intent);
        }
    }
}
