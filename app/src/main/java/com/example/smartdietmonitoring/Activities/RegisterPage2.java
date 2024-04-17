package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterPage2 extends AppCompatActivity {

    NumberPicker weightPicker;
    ImageView ivNext;
    int weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reigster_page2);
        setupWeightPicker();
        listener();



        }

    private void listener() {
        ivNext=findViewById(R.id.ivNext);
        ivNext.setOnClickListener(v -> {
            weight=weightPicker.getValue();
            setupNextActivity();
            addToDb();
        });
    }

    private void addToDb() {
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("users");
        db.child(FirebaseAuth.getInstance().getUid()).child("weight").setValue(weight);

    }

    private void setupNextActivity(){
        Intent i= new Intent(RegisterPage2.this, RegisterPage3.class);
        i.putExtra("weight", weight);
        startActivity(i);
        }
        private void setupWeightPicker(){
            weightPicker=findViewById(R.id.weightPicker);
            weightPicker.setMinValue(30);
            weightPicker.setMaxValue(200);
            weightPicker.setValue(70);
        }
}
