package com.example.projetmobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Absence_add_Activity extends AppCompatActivity {

    private EditText editTextFullName, editTextDate, editTextClass, editTextAgent;
    private Button saveButton;

    private FirebaseAuth authProfile;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absence_add);

        // Initialize Firebase Auth and Firestore
        authProfile = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Reference UI elements
        editTextFullName = findViewById(R.id.editText_full_name);
        editTextDate = findViewById(R.id.editText_date);
        editTextClass = findViewById(R.id.editText_class_ID);
        editTextAgent = findViewById(R.id.editText_agent);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveDataToFirestore());
    }

    private void saveDataToFirestore() {
        // Get user input
        String fullName = editTextFullName.getText().toString().trim();
        String date = editTextDate.getText().toString().trim();
        String classId = editTextClass.getText().toString().trim();
        String agent = editTextAgent.getText().toString().trim();

        // Validation
        if (fullName.isEmpty() || date.isEmpty() || classId.isEmpty() || agent.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get current user
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        if (firebaseUser != null) {
            String userId = firebaseUser.getUid();

            // Prepare data for Firestore
            Map<String, Object> absenceData = new HashMap<>();
            absenceData.put("fullName", fullName);
            absenceData.put("date", date);
            absenceData.put("classId", classId);
            absenceData.put("agent", agent);
            absenceData.put("userId", userId); // Include user ID in absence data

            // Save each absence as a separate document under the "Absences" collection
            firestore.collection("Absences")
                    .add(absenceData) // Use add() to create a new document
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(Absence_add_Activity.this, "Absence saved successfully!", Toast.LENGTH_SHORT).show();
                        clearFields(); // Clear fields after saving
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(Absence_add_Activity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearFields() {
        editTextFullName.setText("");
        editTextDate.setText("");
        editTextClass.setText("");
        editTextAgent.setText("");
    }
}
