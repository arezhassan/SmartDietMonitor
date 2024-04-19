package com.example.smartdietmonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.smartdietmonitoring.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPassword extends AppCompatActivity {

    String email, userId;
    EditText etEmail;
    CardView cardSendResetLink;
    FirebaseAuth mAuth;

    ImageView ivBack;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
        listener();
    }

    private void listener() {
        cardSendResetLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInput();
            }
        });
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getInput() {
        if(etEmail.getText().toString().isEmpty()){
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            etEmail.requestFocus();
        }else{
            email=etEmail.getText().toString();
            sendResetLink(email);
        }
    }

    private void sendResetLink(String s) {
        mAuth.sendPasswordResetEmail(s).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ForgetPassword.this, "Link sent to your email address. Please check.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init(){
        ivBack=findViewById(R.id.ivBack);
        mAuth=FirebaseAuth.getInstance();
        email="";
        userId=mAuth.getUid();
        etEmail=findViewById(R.id.etEmail);
        cardSendResetLink=findViewById(R.id.cardSendResetLink);
    }
}