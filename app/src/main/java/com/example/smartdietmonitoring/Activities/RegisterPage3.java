package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage3 extends AppCompatActivity {


    NumberPicker feetPicker, inchPicker;
    ImageView ivNext;

    int feet, inches, weight, idealWeight, goalCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page3);
        listener();
        setupHeightPickers();


    }

    private void listener() {
        Intent i = getIntent();
        weight = i.getIntExtra("weight", 30);
        getIdealWeight();
        determineGoalCalorie();
        ivNext = findViewById(R.id.ivNext);
        ivNext.setOnClickListener(v -> {
            feet = feetPicker.getValue();
            inches = inchPicker.getValue();
            setupNextActivity();
            addToDb();
        });
    }

    private void addToDb() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users");
        db.child(FirebaseAuth.getInstance().getUid()).child("height_feet").setValue(feet);
        db.child(FirebaseAuth.getInstance().getUid()).child("height_inches").setValue(inches);
    }


    private void setupNextActivity() {
        Intent i = new Intent(RegisterPage3.this, Launcher.class);
        i.putExtra("idealWeight", idealWeight);
        i.putExtra("goalCalories", goalCalories);
        i.putExtra("activityName","RegisterPage3");
        startActivity(i);
        finish();
    }

    private void setupHeightPickers() {

        feetPicker = findViewById(R.id.feetPicker);
        feetPicker.setMinValue(0);
        feetPicker.setMaxValue(7);
        feetPicker.setValue(4);


        inchPicker = findViewById(R.id.inchPicker);
        inchPicker.setMinValue(0);
        inchPicker.setMaxValue(11);
        inchPicker.setValue(7);

    }


    private void getIdealWeight() {
        // Calculate BMR
        double bmr = (10 * (weight * 0.453592)) + (6.25 * ((feet * 30.48) + (inches * 2.54))) + 5;

        // Ideal weight can be calculated as BMR / 10 (because 1 kg = 10 calories)
        idealWeight = (int) (bmr / 10);
        idealWeight=idealWeight+30;

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        db.child("idealWeight").setValue(idealWeight);
    }

    private void determineGoalCalorie() {
        // Calculate BMR
        double bmr = (10 * (weight * 0.453592) )+ (6.25 * ((feet * 30.48) + (inches * 2.54)) )+ 5; // Simplified Mifflin-St Jeor equation

        // Adjust BMR based on activity level
        double activityFactor = 1.55; // Example: Moderately active
        double tdee = bmr * activityFactor;

        // Set goal calorie intake as TDEE
        goalCalories = (int) tdee;
        goalCalories=goalCalories+1350;
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        db.child("goalCalories").setValue(goalCalories);
    }
}