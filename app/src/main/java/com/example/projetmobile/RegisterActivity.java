package com.example.projetmobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender, radioGroupRegisterUser;
    private RadioButton radioButtonRegisterGenderSelected, radioButtonRegisterUserSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        // Initializing UI components
        editTextRegisterFullName = findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPwd = findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd = findViewById(R.id.editText_register_confirm_password);
        radioGroupRegisterGender = findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterUser = findViewById(R.id.radio_group_register_user);
        progressBar = findViewById(R.id.progressbar);  // Use the correct ID 'progressbar' (case-sensitive)

        // Reset radio groups
        radioGroupRegisterGender.clearCheck();
        radioGroupRegisterUser.clearCheck();

        // Register Button Click Listener
        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(v -> {
            int selectedGenderId = radioGroupRegisterGender.getCheckedRadioButtonId();
            int selectedUserId = radioGroupRegisterUser.getCheckedRadioButtonId();

            if (selectedGenderId == -1 || selectedUserId == -1) {
                Toast.makeText(RegisterActivity.this, "Please select both gender and role", Toast.LENGTH_LONG).show();
                return;
            }

            radioButtonRegisterGenderSelected = findViewById(selectedGenderId);
            radioButtonRegisterUserSelected = findViewById(selectedUserId);

            String textFullName = editTextRegisterFullName.getText().toString();
            String textEmail = editTextRegisterEmail.getText().toString();
            String textPwd = editTextRegisterPwd.getText().toString();
            String textConfirmPwd = editTextRegisterConfirmPwd.getText().toString();

            if (TextUtils.isEmpty(textFullName)) {
                editTextRegisterFullName.setError("Full name is required");
                editTextRegisterFullName.requestFocus();
            } else if (TextUtils.isEmpty(textEmail)) {
                editTextRegisterEmail.setError("Email is required");
                editTextRegisterEmail.requestFocus();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                editTextRegisterEmail.setError("Please enter a valid email address");
                editTextRegisterEmail.requestFocus();
            } else if (TextUtils.isEmpty(textPwd)) {
                editTextRegisterPwd.setError("Password is required");
                editTextRegisterPwd.requestFocus();
            } else if (TextUtils.isEmpty(textConfirmPwd)) {
                editTextRegisterConfirmPwd.setError("Please confirm your password");
                editTextRegisterConfirmPwd.requestFocus();
            } else if (!textPwd.equals(textConfirmPwd)) {
                editTextRegisterConfirmPwd.setError("Passwords do not match");
                editTextRegisterConfirmPwd.requestFocus();
            } else {
                String textGender = radioButtonRegisterGenderSelected.getText().toString();
                String textUser = radioButtonRegisterUserSelected.getText().toString();
                progressBar.setVisibility(View.VISIBLE); // Show progress bar while registering
                registerUser(textFullName, textEmail, textGender, textUser, textPwd);
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textGender, String textUser, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.createUserWithEmailAndPassword(textEmail, textPwd)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar after registration

                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
//ajouter les donnes a realtime database
// Add the missing role parameter (textUser)
                            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textFullName, textGender, textUser, textUser);
                            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("registred users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "User registered successfully. Please verify your email.", Toast.LENGTH_LONG).show();

                                        // Redirect to login or another activity after successful registration
                               Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();  // Close RegisterActivity to prevent returning to it after registration
                                    }
                                    else {
                                        Toast.makeText(RegisterActivity.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                    }


                                }
                            });




                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
