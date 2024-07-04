package com.example.getfeed;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class user_question_display extends AppCompatActivity {

    private static final String TAG = "user_question_display";
    private TextView shopNameView;
    private LinearLayout questionsContainer;
    private LinearLayout ratingsContainer;
    private Button save;
    private FirebaseFirestore firestore;

    private List<EditText> answerEditTexts = new ArrayList<>();
    private List<RatingBar> ratingBars = new ArrayList<>();
    private List<String> ratingTopics = new ArrayList<>();
    private String shopEmail; // Variable to store the shop email

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_question_display_activity);

        firestore = FirebaseFirestore.getInstance();

        shopNameView = findViewById(R.id.shop_name);
        questionsContainer = findViewById(R.id.questions_container);
        ratingsContainer = findViewById(R.id.ratings_container);
        save = findViewById(R.id.save_button);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String shopName = sharedPreferences.getString("selectedShopName", "No Shop Selected");

        shopNameView.setText(shopName);

        fetchShopDetails(shopName);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveResponse();
                sendEmail(shopEmail);
                NotificationHelper.createNotificationChannel(user_question_display.this);
                NotificationHelper.showNotification(user_question_display.this, "Comeback and get in touch with us");
                Intent intent = new Intent(user_question_display.this,End.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void fetchShopDetails(String shopName) {
        firestore.collection("shops")
                .whereEqualTo("shop_name", shopName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);

                            List<String> questions = (List<String>) document.get("questions");
                            ratingTopics = (List<String>) document.get("ratings");
                            shopEmail = document.getString("email"); // Retrieve the email

                            displayQuestionsAndRatings(questions, ratingTopics);
                        } else {
                            Toast.makeText(user_question_display.this, "No shop details found", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "Error retrieving shop details", e);
                        Toast.makeText(user_question_display.this, "Error retrieving shop details", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void displayQuestionsAndRatings(List<String> questions, List<String> ratings) {
        questionsContainer.removeAllViews();
        ratingsContainer.removeAllViews();

        if (questions != null && !questions.isEmpty()) {
            for (String question : questions) {
                // Add question text view
                TextView questionView = new TextView(this);
                questionView.setText(question);
                questionsContainer.addView(questionView);

                // Add answer edit text
                EditText answerEditText = new EditText(this);
                answerEditText.setHint("Write your answer here");
                questionsContainer.addView(answerEditText);
                answerEditTexts.add(answerEditText);
            }
        } else {
            TextView noQuestionsView = new TextView(this);
            noQuestionsView.setText("No questions available");
            questionsContainer.addView(noQuestionsView);
        }

        if (ratings != null && !ratings.isEmpty()) {
            for (String ratingTopic : ratings) {
                // Add rating topic text view
                TextView ratingTopicView = new TextView(this);
                ratingTopicView.setText(ratingTopic);
                ratingsContainer.addView(ratingTopicView);

                // Add rating bar
                RatingBar ratingBar = new RatingBar(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                ratingBar.setLayoutParams(layoutParams);
                ratingBar.setNumStars(5);
                ratingBar.setStepSize(1.0f);
                ratingsContainer.addView(ratingBar);
                ratingBars.add(ratingBar);
            }
        } else {
            TextView noRatingsView = new TextView(this);
            noRatingsView.setText("No ratings available");
            ratingsContainer.addView(noRatingsView);
        }
    }

    private void saveResponse() {
        String shopName = shopNameView.getText().toString();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);

        List<String> answers = new ArrayList<>();
        for (EditText answerEditText : answerEditTexts) {
            answers.add(answerEditText.getText().toString());
        }
        response.put("answers", answers);

        Map<String, Float> ratings = new HashMap<>();
        for (int i = 0; i < ratingBars.size(); i++) {
            float rating = ratingBars.get(i).getRating();
            ratings.put(ratingTopics.get(i), rating);
        }
        response.put("ratings", ratings);

        firestore.collection("shops")
                .whereEqualTo("shop_name", shopName)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot document = queryDocumentSnapshots.getDocuments().get(0);
                        document.getReference().collection("responses").add(response)
                                .addOnSuccessListener(aVoid -> Toast.makeText(user_question_display.this, "Response saved successfully", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(user_question_display.this, "Error saving response", Toast.LENGTH_SHORT).show());
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(user_question_display.this, "Error retrieving shop details", Toast.LENGTH_SHORT).show());
    }
    private void sendEmail(String email) {
        String subject = "New rating has arrived!";
        String body = "Check out the new rating in our GETFEED";
        EmailUtil.sendEmailInBackground(this, email, subject, body);
    }

}
