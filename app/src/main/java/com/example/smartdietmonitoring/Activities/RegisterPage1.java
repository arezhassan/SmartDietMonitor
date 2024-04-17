package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage1 extends AppCompatActivity {

    EditText etEmail, etPassword, etName;

    String email, password, userName;

    CardView cardRegister;

    TextView tvAlreadyUser;

    LottieAnimationView animation;

    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page1);
        initializeViews();
        listener();

    }

    private void listener() {
        tvAlreadyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(RegisterPage1.this, LoginPage.class);
                startActivity(i);
                finish();
            }
        });
        cardRegister.setOnClickListener(v -> {
            cardRegister.setVisibility(View.GONE);
            animation.setVisibility(View.VISIBLE);
            getUserData();
        });
    }

    private void getUserData() {
        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Enter a valid email.");
            cardRegister.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else if (etPassword.getText().toString().isEmpty()) {
            etEmail.setError("Passwords cannot be empty");
            cardRegister.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else if (etPassword.getText().toString().length() < 8) {
            etEmail.setError("Password Length should be 8 characters.");
            cardRegister.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else if (etName.getText().toString().isEmpty()) {
            etName.setError("Name cannot be empty.");
            cardRegister.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else {
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString();
            userName=etName.getText().toString();
            registerNewUser(email, password);
        }


    }

    private void registerNewUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                navigateToNextScreen();
                addUserToDb();
            }

            private void addUserToDb() {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference usersRef = database.getReference().child("users"); // Reference to "users" node
                String userId = mAuth.getUid(); // Get the user ID
// Create a new child node under "users" with the user ID
                DatabaseReference userRef = usersRef.child(userId);
// Set user details under the user ID node
                userRef.child("email").setValue(email); // Example: Set username
                userRef.child("userName").setValue(userName); // Example: Set username

// You can set other user details as needed
            }
        });
    }

    private void navigateToNextScreen() {
        Intent i = new Intent(RegisterPage1.this, RegisterPage2.class);
        startActivity(i);
        finish();
    }


    private void initializeViews() {
        tvAlreadyUser=findViewById(R.id.tvAlreadyUser);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        etName =findViewById(R.id.etName);
        cardRegister = findViewById(R.id.cardRegister);
        etPassword = findViewById(R.id.etPassword);
        animation = findViewById(R.id.lottieAnimationView);

    }
}