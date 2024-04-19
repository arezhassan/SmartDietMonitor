package com.example.smartdietmonitoring.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartdietmonitoring.Activities.MainActivity;
import com.example.smartdietmonitoring.R;

public class Launcher extends AppCompatActivity {

    private static final long DELAY_TIME_MS = 1300;// 2 seconds delay

    TextView tvPersonalizing;
    String activity;
    int idealWeight, goalCalories;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        Intent i = getIntent();

        activity = i.getStringExtra("activityName");
        idealWeight = i.getIntExtra("idealWeight", 30);
        goalCalories = i.getIntExtra("goalCalories", 2000);

        tvPersonalizing=findViewById(R.id.tvPersonalizing);
        tvPersonalizing.setVisibility(View.GONE);

        if (activity!=null && activity.equals("RegisterPage3")){
            tvPersonalizing.setVisibility(View.VISIBLE);
        }else{
            tvPersonalizing.setVisibility(View.GONE);
        }

        // Using a Handler to post a delayed Runnable to navigate to MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent to start MainActivity
                Intent intent;
                if (activity!=null && activity.equals("RegisterPage3")) {
                    intent = new Intent(Launcher.this, BMISetup.class);
                    intent.putExtra("idealWeight", idealWeight);
                    intent.putExtra("goalCalories", goalCalories);
                } else {
                    intent = new Intent(Launcher.this, MainActivity.class);
                }
                startActivity(intent);
                finish();

                // Finish the current activity
                finish();
            }
        }, DELAY_TIME_MS);
    }
}
