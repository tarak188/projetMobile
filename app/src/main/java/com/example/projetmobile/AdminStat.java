package com.example.projetmobile;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class AdminStat extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView absencesTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_stat);

        absencesTextView = findViewById(R.id.absencesTextView);

        db = FirebaseFirestore.getInstance();

        fetchAbsences();
    }

    private void fetchAbsences() {
        CollectionReference absencesRef = db.collection("Absences");

        absencesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Integer> absencesCount = new HashMap<>();

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String fullName = document.getString("fullName");
                    if (fullName != null) {
                        absencesCount.put(fullName, absencesCount.getOrDefault(fullName, 0) + 1);
                    }
                }

                displayAbsences(absencesCount);
            } else {
                absencesTextView.setText("Error fetching data.");
            }
        });
    }

    private void displayAbsences(Map<String, Integer> absencesCount) {
        StringBuilder absencesText = new StringBuilder();

        for (Map.Entry<String, Integer> entry : absencesCount.entrySet()) {
            absencesText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" absences\n");
        }

        absencesTextView.setText(absencesText.toString());
    }
}
