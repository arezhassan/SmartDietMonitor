package com.example.smartdietmonitoring.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdietmonitoring.Activities.LoginPage;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Utilities.CircleTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class Profile extends Fragment {

View view;

TextView tvIdealWeight, tvUserName, tvUserEmail, tvCurrentWeight;

ImageView ivEditProfile, ivAvatar, ivLogout, ivAddWeight;

FirebaseAuth mAuth;

String userId, email, profilePic, userName;

int idealWeight, currentWeight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_profile, container, false);
        init();
        loadUserData();
        listener();
        return view;

    }

    private void loadUserData() {
        DatabaseReference user= FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId);
        user.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    email=snapshot.child("email").getValue(String.class);
                    userName=snapshot.child("userName").getValue(String.class);
                    profilePic=snapshot.child("ImageUrl").getValue(String.class);
                    idealWeight=snapshot.child("idealWeight").getValue(Integer.class);
                    currentWeight=snapshot.child("weight").getValue(Integer.class);
                    tvUserName.setText(userName);
                    tvUserEmail.setText(email);
                    tvIdealWeight.setText(String.valueOf(idealWeight)+ " KG");
                    tvCurrentWeight.setText(String.valueOf(currentWeight)+ " KG");
                    Picasso.get().load(profilePic).transform(new CircleTransformation()).into(ivAvatar);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listener() {
        ivEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToEditProfile();
            }
        });
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                navigateToMainPage();
            }
        });

        ivAddWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToAddWeight();
            }
        });



    }

    private void navigateToMainPage() {
        Intent i=new Intent(requireContext(), LoginPage.class);
        startActivity(i);
    }

    private void navigateToEditProfile() {
        Intent i=new Intent(requireContext(), com.example.smartdietmonitoring.Activities.Profile.class);
        i.putExtra("userName",userName);
        startActivity(i);
    }
    private void navigateToAddWeight() {
        Intent i=new Intent(requireContext(), com.example.smartdietmonitoring.Activities.AddWeight.class);
        startActivity(i);
    }

    private void init() {
        tvIdealWeight=view.findViewById(R.id.tvIdealWeight);
        tvCurrentWeight=view.findViewById(R.id.tvCurrentWeight);
        tvUserEmail=view.findViewById(R.id.tvUserEmail);
        tvUserName=view.findViewById(R.id.tvUserName);

        ivAvatar=view.findViewById(R.id.ivAvatar);
        ivEditProfile=view.findViewById(R.id.ivEditProfile);
        ivLogout=view.findViewById(R.id.ivLogout);
        ivAddWeight=view.findViewById(R.id.ivAddWeight);

        mAuth=FirebaseAuth.getInstance();
        userId=mAuth.getUid();
    }


}