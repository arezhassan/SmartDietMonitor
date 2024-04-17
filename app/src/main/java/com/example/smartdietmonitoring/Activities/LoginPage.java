package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {


    EditText etEmail, etPassword;

    String email, password;

    CardView cardLogin;

    LottieAnimationView animation;

    FirebaseAuth mAuth;
    TextView tvNotAUser;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        initializeViews();
        listener();

    }

    private void listener() {
        cardLogin.setOnClickListener(v -> {
            cardLogin.setVisibility(View.GONE);
            animation.setVisibility(View.VISIBLE);
            getUserData();
        });
        tvNotAUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginPage.this, RegisterPage1.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void getUserData() {
        if (etEmail.getText().toString().isEmpty()) {
            etEmail.setError("Enter a valid email.");
            cardLogin.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else if (etPassword.getText().toString().isEmpty()) {
            etEmail.setError("Passwords cannot be empty");
            cardLogin.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        } else if (etPassword.getText().toString().length() < 8) {
            etEmail.setError("Password Length should be 8 characters.");
            cardLogin.setVisibility(View.VISIBLE);
            animation.setVisibility(View.GONE);
        }  else {
            email = etEmail.getText().toString().trim();
            password = etPassword.getText().toString();
            login(email, password);
        }


    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                navigateToNextScreen();
            }
        });
    }

    private void navigateToNextScreen() {
        Intent i = new Intent(LoginPage.this, MainActivity.class);
        startActivity(i);
        finish();
    }


    private void initializeViews() {
        tvNotAUser = findViewById(R.id.tvNotAUser);
        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.etEmail);
        cardLogin = findViewById(R.id.cardLogin);
        etPassword = findViewById(R.id.etPassword);
        animation = findViewById(R.id.lottieAnimationView);
    }

}