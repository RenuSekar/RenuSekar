package com.example.getfeed;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class setQuestions extends AppCompatActivity {

    private static final String TAG = "setQuestionsActivity";

    private LinearLayout container;
    private ImageView addButton;
    private LinearLayout ratingContainer;
    private ImageView ratingButton;
    private Button submit;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firestore;
    private String shopId;

    private EditText initialQuestionField;
    private EditText initialRatingField;
    private Button response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_questions_activity);

        response = findViewById(R.id.see_response_btn);

        response.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setQuestions.this, ResponseActivity.class);
                startActivity(intent);
            }
        });

        // Initialize Firestore
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Retrieve shop ID from FirebaseAuth
        if (mAuth.getCurrentUser() != null) {
            shopId = mAuth.getCurrentUser().getUid();
        }

        // Find views by ID
        container = findViewById(R.id.question_container);
        addButton = findViewById(R.id.add_button);
        ratingContainer = findViewById(R.id.ratingQn_container);
        ratingButton = findViewById(R.id.add_ratingQn_button);
        submit = findViewById(R.id.submit_btn);

        initialQuestionField = findViewById(R.id.initial_question_field);
        initialRatingField = findViewById(R.id.initial_rating_field);

        // Set click listeners
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(setQuestions.this, R.anim.animation1);
                addButton.startAnimation(fadeIn);
                addQuestion();
            }
        });

        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation fadeIn = AnimationUtils.loadAnimation(setQuestions.this, R.anim.animation1);
                ratingButton.startAnimation(fadeIn);
                addRating();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuestionsToFirestore();
                NotificationHelper.createNotificationChannel(setQuestions.this);
                NotificationHelper.showNotification(setQuestions.this,"Explore more and get notified when your shop gets reviews");
                Intent intent = new Intent(setQuestions.this,End.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void addQuestion() {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        editText.setHint("Add Questions Here!");
        container.addView(editText);
        // Set focus on the new EditText
        editText.requestFocus();
    }

    private void addRating() {
        EditText editText = new EditText(this);
        editText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        editText.setHint("Add Rating Field Here!");
        ratingContainer.addView(editText);
        // Set focus on the new EditText
        editText.requestFocus();
    }

    private void saveQuestionsToFirestore() {
        List<String> questions = new ArrayList<>();
        List<String> ratings = new ArrayList<>();

        // Add initial question and rating fields to the lists
        String initialQuestion = initialQuestionField.getText().toString().trim();
        if (!TextUtils.isEmpty(initialQuestion)) {
            questions.add(initialQuestion);
        }

        String initialRating = initialRatingField.getText().toString().trim();
        if (!TextUtils.isEmpty(initialRating)) {
            ratings.add(initialRating);
        }

        // Retrieve questions from the container
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                String question = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(question)) {
                    questions.add(question);
                }
            }
        }

        // Retrieve rating questions from the rating container
        for (int i = 0; i < ratingContainer.getChildCount(); i++) {
            View view = ratingContainer.getChildAt(i);
            if (view instanceof EditText) {
                EditText editText = (EditText) view;
                String rating = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(rating)) {
                    ratings.add(rating);
                }
            }
        }

        // Log the retrieved questions and ratings for debugging
        Log.d(TAG, "Questions: " + questions.toString());
        Log.d(TAG, "Ratings: " + ratings.toString());

        // Check if shopId is valid
        if (shopId == null || shopId.isEmpty()) {
            Toast.makeText(this, "Invalid shop ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare data to save
        Map<String, Object> data = new HashMap<>();
        //data.put("shopId", shopId); // Add shopId to the data
        data.put("questions", questions); // Add questions to the data
        data.put("ratings", ratings); // Add ratings to the data

        // Save data to Firestore in the 'shopDetails' collection under the shopId document
        firestore.collection("shops").document(shopId)
                .update(data) // Use set() instead of add() to specify the document ID (shopId)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(setQuestions.this, "Questions saved successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(setQuestions.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(setQuestions.this, "Error saving questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}