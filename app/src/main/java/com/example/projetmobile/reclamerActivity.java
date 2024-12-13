package com.example.projetmobile;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.messaging.RemoteMessage;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class reclamerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamer);

        // UI Elements
        TextView absenceIdTextView = findViewById(R.id.absenceIdTextView);
        TextView reclamationIdTextView = findViewById(R.id.reclamationIdTextView);
        EditText detailsEditText = findViewById(R.id.detailsEditText);
        Button sendButton = findViewById(R.id.sendButton);

        // Fetch the absence details
        String absenceId = getIntent().getStringExtra("absenceId");
        String reclamationId = "REC" + System.currentTimeMillis(); // Unique reclamation ID

        // Set TextView values
        absenceIdTextView.setText("Absence ID: " + (absenceId != null ? absenceId : "N/A"));
        reclamationIdTextView.setText("Reclamation ID: " + reclamationId);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String details = detailsEditText.getText().toString();
                if (details.isEmpty()) {
                    Toast.makeText(reclamerActivity.this, "Please enter details!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save the reclamation to Firebase Realtime Database
                DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("reclamations").push();
                dbRef.setValue(new Reclamation(absenceId, reclamationId, details)).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(reclamerActivity.this, "Reclamation sent!", Toast.LENGTH_SHORT).show();
                        sendNotificationToAdmin(reclamationId, details);
                    } else {
                        Toast.makeText(reclamerActivity.this, "Failed to send reclamation!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void sendNotificationToAdmin(String reclamationId, String details) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
        userRef.orderByChild("role").equalTo("Admin").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    String adminToken = snapshot.child("fcmToken").getValue(String.class);
                    if (adminToken != null) {
                        // Send notification to admin
                        FirebaseMessaging.getInstance().send(
                                new RemoteMessage.Builder(adminToken + "@gcm.googleapis.com")
                                        .setMessageId(String.valueOf(System.currentTimeMillis()))
                                        .addData("title", "New Reclamation: " + reclamationId)
                                        .addData("body", details)
                                        .build()
                        );
                    }
                }
            }
        });
    }
}
