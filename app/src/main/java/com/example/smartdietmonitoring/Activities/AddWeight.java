package com.example.smartdietmonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddWeight extends AppCompatActivity {

    NumberPicker weightPicker;

    TextView tvCurrentWeight;

    int currentWeight;

    AlertDialog dialog;


    CardView cardSaveWeight;

    int enteredWeight;

    FirebaseAuth mAuth;

    String userId;

    ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_weight);
        init();
        getCurrentWeight();
        listener();
    }

    private void listener() {
        cardSaveWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference user = FirebaseDatabase.getInstance().getReference()
                        .child("users").child(userId);
                user.child("weight").setValue(weightPicker.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showCustomDialog();
                            // Delay dismissal of dialog by 1000 milliseconds (1 second)
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.dismiss();
                                }
                            }, 1000); // 1000 milliseconds = 1 second
                        }
                    }
                });
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getCurrentWeight() {
        DatabaseReference user= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    currentWeight=snapshot.child("weight").getValue(Integer.class);
                    tvCurrentWeight.setText(String.valueOf(currentWeight)+ " KGs");
                    weightPicker.setValue(currentWeight);
                    weightPicker.setMaxValue(150);
                    weightPicker.setMinValue(1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



    private void init(){
        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getUid();

        tvCurrentWeight=findViewById(R.id.tvCurrentWeight);
        weightPicker=findViewById(R.id.weightPicker);
        cardSaveWeight=findViewById(R.id.cardSaveWeight);
        ivBack=findViewById(R.id.ivBack);
    }
    private void showCustomDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialogweightchanged, null);

        // Initialize Lottie animation view

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setCancelable(false);

        // Create the dialog
        dialog = builder.create();

        // Set dialog window attributes to position it at the bottom
        Window window = dialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
        window.setAttributes(layoutParams);

        // Show the dialog
        dialog.show();

    }


}