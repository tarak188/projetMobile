package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class UserAbsenceActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseFirestore firestore;
    private LinearLayout absenceListContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_absence);

        // Initialize Firebase Auth and Firestore
        authProfile = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Reference to the LinearLayout inside the ScrollView
        absenceListContainer = findViewById(R.id.absenceListContainer);

        // Fetch and display absences for the authenticated user
        fetchUserAbsences();
    }

    private void fetchUserAbsences() {
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            firestore.collection("Absences")
                    .whereEqualTo("userId", userId) // Fetch absences for the logged-in user
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Toast.makeText(this, "No absences found for this user", Toast.LENGTH_SHORT).show();
                        } else {
                            // Loop through results and add to the UI
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                String fullName = document.getString("fullName");
                                String date = document.getString("date");
                                String classId = document.getString("classId");
                                String agent = document.getString("agent");

                                // Create a LinearLayout to hold absence details and the button
                                LinearLayout absenceLayout = new LinearLayout(this);
                                absenceLayout.setOrientation(LinearLayout.VERTICAL);
                                absenceLayout.setPadding(16, 16, 16, 16);
                                absenceLayout.setBackgroundResource(android.R.color.background_light);

                                // Create a TextView for the absence details
                                TextView textView = new TextView(this);
                                textView.setText("Name: " + fullName +
                                        "\nDate: " + date +
                                        "\nClass: " + classId +
                                        "\nAgent: " + agent);
                                textView.setTextSize(16);

                                // Create the "Réclamer" button
                                Button btnReclamer = new Button(this);
                                btnReclamer.setText("Réclamer");
                                btnReclamer.setOnClickListener(v -> {
                                    // Navigate to ReclamerActivity and pass the absence details
                                    Intent intent = new Intent(UserAbsenceActivity.this, reclamerActivity.class);
                                    intent.putExtra("fullName", fullName);
                                    intent.putExtra("date", date);
                                    intent.putExtra("classId", classId);
                                    intent.putExtra("agent", agent);
                                    startActivity(intent);
                                });

                                // Add the TextView and Button to the LinearLayout
                                absenceLayout.addView(textView);
                                absenceLayout.addView(btnReclamer);

                                // Add the LinearLayout to the container
                                absenceListContainer.addView(absenceLayout);
                            }
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to fetch absences: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }
}
