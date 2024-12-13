package com.example.projetmobile;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

public class AllabsencesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private LinearLayout absenceListContainer;
    private SearchView searchViewName;
    private Button buttonFilterDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allabsences);

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Reference UI elements
        absenceListContainer = findViewById(R.id.absenceListContainer);
        searchViewName = findViewById(R.id.searchViewName);
        buttonFilterDate = findViewById(R.id.buttonFilterDate);

        // Fetch all absences initially
        fetchAllAbsences();

        // Add listener to SearchView for name filtering
        searchViewName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterAbsencesByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterAbsencesByName(newText);
                return true;
            }
        });

        // Add listener to the date filter button
        buttonFilterDate.setOnClickListener(v -> showDatePicker());
    }

    private void fetchAllAbsences() {
        firestore.collection("Absences")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absenceListContainer.removeAllViews(); // Clear the list first
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No absences found", Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            addAbsenceToUI(document);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch absences: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterAbsencesByName(String name) {
        firestore.collection("Absences")
                .orderBy("fullName") // Ensure "fullName" is indexed in Firestore
                .startAt(name)
                .endAt(name + "\uf8ff")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absenceListContainer.removeAllViews(); // Clear the list first
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No absences found for name: " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            addAbsenceToUI(document);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to filter by name: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void filterAbsencesByDate(String date) {
        firestore.collection("Absences")
                .whereEqualTo("date", date)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absenceListContainer.removeAllViews(); // Clear the list first
                    if (queryDocumentSnapshots.isEmpty()) {
                        Toast.makeText(this, "No absences found for date: " + date, Toast.LENGTH_SHORT).show();
                    } else {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            addAbsenceToUI(document);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to filter by date: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
            String selectedDate = selectedYear + "-" + (selectedMonth + 1) + "-" + selectedDay;
            filterAbsencesByDate(selectedDate);
        }, year, month, day).show();
    }

    private void addAbsenceToUI(QueryDocumentSnapshot document) {
        String fullName = document.getString("fullName");
        String date = document.getString("date");
        String classId = document.getString("classId");
        String agent = document.getString("agent");

        // Create a TextView for each absence
        TextView textView = new TextView(this);
        textView.setText("Name: " + fullName +
                "\nDate: " + date +
                "\nClass: " + classId +
                "\nAgent: " + agent);
        textView.setPadding(16, 16, 16, 16);
        textView.setBackgroundResource(android.R.color.background_light);
        textView.setTextSize(16);

        // Add the TextView to the container
        absenceListContainer.addView(textView);
    }
}
