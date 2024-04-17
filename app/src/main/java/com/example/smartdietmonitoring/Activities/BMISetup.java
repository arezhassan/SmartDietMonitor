package com.example.smartdietmonitoring.Activities;

import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.Calendar;

public class BMISetup extends AppCompatActivity {


    TextView tvGoalCalories, tvIdealWeight;

    ImageView ivNext;

    int idealWeight, goalCalories;

    String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmisetup);
        initializeViews();
        listener();
    }

    private void listener() {
        ivNext.setOnClickListener(v -> {
          navigateToNext();
        });
    }

    private void navigateToNext() {
        Intent i=new Intent(BMISetup.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void initializeViews() {
        userName= FirebaseAuth.getInstance().getUid();
        Intent i = getIntent();
        setFirstMeal();
        idealWeight = i.getIntExtra("idealWeight", 30);
        goalCalories = i.getIntExtra("goalCalories", 2000);
        ivNext = findViewById(R.id.ivNext);
        tvGoalCalories = findViewById(R.id.tvGoalCalories);
        tvIdealWeight = findViewById(R.id.tvIdealWeight);


        //Setup TextView
        tvIdealWeight.setText(String.valueOf(idealWeight) + " KG");
        tvGoalCalories.setText(String.valueOf(goalCalories)+ " Kcals");
    }
    private void setFirstMeal(){
        DatabaseReference meals= FirebaseDatabase.getInstance().getReference()
                .child("meals");
        meals.child(userName).child(getCurrentDate()).child("totalCalories").setValue(0);
    }
    private String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        String currentDate = day + " " + getMonthName(month) + " " + year;
        String keyDate = convertToFirebaseKey(currentDate);
        return keyDate;
    }
    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }
}