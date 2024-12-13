package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class UserProfileActivity extends AppCompatActivity {

    private TextView TextViewWelcome, TextViewFullName, TextViewEmail, TextViewGender, TextViewRole;
    private String fullName, email, gender, role;
    private ImageView imageView;
    private FirebaseAuth authprofile;
    private Button buttonAddAbsence;
    private Button buttonListAbsence;
    private Button buttonLesabcences;
    private Button buttonstats;
    private Button buttonBell;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Initialize Views
        TextViewWelcome = findViewById(R.id.textView_show_welcome);
        TextViewFullName = findViewById(R.id.textView_show_full_name);
        TextViewEmail = findViewById(R.id.textView_show_email);
        TextViewGender = findViewById(R.id.textView_show_gender);
        TextViewRole = findViewById(R.id.textView_show_role);
        imageView = findViewById(R.id.imageView_profile_dp);
        buttonBell = findViewById(R.id.button_bell);
        buttonstats= findViewById(R.id.admin_statistique);

        // Initially hide the bell button - it will be shown later if user is admin
        buttonBell.setVisibility(View.GONE);
        buttonstats.setVisibility(View.GONE);

        // Initialize the Add Absence button
        buttonstats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, AdminStat.class);
                startActivity(intent);
            }
        });
        buttonAddAbsence = findViewById(R.id.button_add_absence);
        buttonAddAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, Absence_add_Activity.class);
                startActivity(intent);
            }
        });

        // Initialize the List Absence button
        buttonListAbsence = findViewById(R.id.buttonListAbsence);
        buttonListAbsence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, UserAbsenceActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the Les Abcences button
        buttonLesabcences = findViewById(R.id.buttonLesabcences);
        buttonLesabcences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, AllabsencesActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the Bell Button for Notifications
        buttonBell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfileActivity.this, adminActivityNotif.class);
                startActivity(intent);
            }
        });

        // Initialize Firebase Auth
        authprofile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authprofile.getCurrentUser();

        if (firebaseUser == null) {
            Toast.makeText(UserProfileActivity.this, "Something went wrong. Please verify the issue.", Toast.LENGTH_SHORT).show();
        } else {
            showUserProfile(firebaseUser);
        }
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("registred users");

        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readUserDetails != null) {
                    fullName = readUserDetails.fullName != null ? readUserDetails.fullName : "N/A";
                    email = firebaseUser.getEmail() != null ? firebaseUser.getEmail() : "N/A";
                    gender = readUserDetails.gender != null ? readUserDetails.gender : "N/A";
                    role = readUserDetails.role != null ? readUserDetails.role : "N/A";

                    TextViewWelcome.setText("Welcome, " + fullName + "!");
                    TextViewFullName.setText(fullName);
                    TextViewEmail.setText(email);
                    TextViewGender.setText(gender);
                    TextViewRole.setText(role);

                    // Check user role and show/hide bell button accordingly
                    if ("Admin".equals(role)) {
                        buttonBell.setVisibility(View.VISIBLE);
                        buttonstats.setVisibility(View.VISIBLE);
                        buttonAddAbsence.setVisibility(View.GONE);
                        buttonListAbsence.setVisibility(View.GONE);
                        // Save the FCM token for Admin users
                        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult();
                            // Save the token to Firebase database for the admin user
                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("registred users");
                            userRef.child(userID).child("fcmToken").setValue(token);
                        });
                    } else {
                        buttonBell.setVisibility(View.GONE);
                        buttonstats.setVisibility(View.GONE);
                        buttonAddAbsence.setVisibility(View.VISIBLE);
                        buttonListAbsence.setVisibility(View.VISIBLE);
                    }

                    // Set the profile image based on gender
                    if ("male".equalsIgnoreCase(gender)) {
                        imageView.setImageResource(R.drawable.ic_user_pic);
                    } else if ("female".equalsIgnoreCase(gender)) {
                        imageView.setImageResource(R.drawable.ic_female);
                    } else {
                        imageView.setImageResource(R.drawable.ic_user_pic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}