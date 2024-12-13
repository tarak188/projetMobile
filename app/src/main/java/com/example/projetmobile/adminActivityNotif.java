package com.example.projetmobile;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class adminActivityNotif extends AppCompatActivity {

    private TextView adminNotifTextView;
    private EditText filterDateEditText, filterNameEditText;
    private List<Reclamation> reclamationsList = new ArrayList<>();
    private List<Reclamation> filteredReclamationsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_notif);

        adminNotifTextView = findViewById(R.id.adminNotifTextView);
        filterDateEditText = findViewById(R.id.filterDateEditText);
        filterNameEditText = findViewById(R.id.filterNameEditText);

        // Firebase reference to the "reclamations" node
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("reclamations");

        // Fetch reclamations
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                reclamationsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    String reclamationId = data.child("reclamationId").getValue(String.class);
                    String absenceId = data.child("absenceId").getValue(String.class);
                    String details = data.child("details").getValue(String.class);
                    String date = data.child("date").getValue(String.class);
                    String name = data.child("name").getValue(String.class);

                    Reclamation reclamation = new Reclamation(reclamationId, absenceId, details, date, name);
                    reclamationsList.add(reclamation);
                }
                filterReclamations();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                adminNotifTextView.setText("Failed to load notifications: " + error.getMessage());
            }
        });

        // Trigger filtering when user inputs values
        filterDateEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterReclamations();
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        filterNameEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                filterReclamations();
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });
    }

    private void filterReclamations() {
        String filterDate = filterDateEditText.getText().toString().trim();
        String filterName = filterNameEditText.getText().toString().trim();

        filteredReclamationsList.clear();

        for (Reclamation reclamation : reclamationsList) {
            boolean matchesDate = filterDate.isEmpty() || reclamation.getDate().contains(filterDate);
            boolean matchesName = filterName.isEmpty() || reclamation.getName().contains(filterName);

            if (matchesDate && matchesName) {
                filteredReclamationsList.add(reclamation);
            }
        }

        // Display filtered notifications
        StringBuilder notifications = new StringBuilder();
        for (Reclamation reclamation : filteredReclamationsList) {
            notifications.append("Reclamation ID: ").append(reclamation.getReclamationId())
                    .append("\nAbsence ID: ").append(reclamation.getAbsenceId())
                    .append("\nDetails: ").append(reclamation.getDetails())
                    .append("\nDate: ").append(reclamation.getDate())
                    .append("\nName: ").append(reclamation.getName())
                    .append("\n\n");
        }

        adminNotifTextView.setText(notifications.toString());
    }

    // Reclamation class to store reclamation details
    public static class Reclamation {
        private String reclamationId;
        private String absenceId;
        private String details;
        private String date;
        private String name;

        public Reclamation(String reclamationId, String absenceId, String details, String date, String name) {
            this.reclamationId = reclamationId;
            this.absenceId = absenceId;
            this.details = details;
            this.date = date;
            this.name = name;
        }

        public String getReclamationId() {
            return reclamationId;
        }

        public String getAbsenceId() {
            return absenceId;
        }

        public String getDetails() {
            return details;
        }

        public String getDate() {
            return date;
        }

        public String getName() {
            return name;
        }
    }
}
