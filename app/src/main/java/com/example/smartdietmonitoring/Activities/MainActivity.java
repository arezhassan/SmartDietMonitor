package com.example.smartdietmonitoring.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.smartdietmonitoring.Fragments.MealPlan;
import com.example.smartdietmonitoring.Fragments.Progress;
import com.example.smartdietmonitoring.Fragments.Today;
import com.example.smartdietmonitoring.OnAddFoodListener;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Utilities.CircleTransformation;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        getUserData();
        if(FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent i=new Intent(MainActivity.this,LoginPage.class);
            startActivity(i);
            finish();
        }
    }

    BottomNavigationView bottomnav;

    TextView tvUserName;

    ImageView ivAvatar;

    String userName, ImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        getUserData();
        setBottomNavBar();
        // Set TodayFragment as the default fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MealPlan()).commit();
    }

    private void setBottomNavBar() {
        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                Fragment selectedFragment = null;

                if (item.getItemId() == R.id.today) {
                    selectedFragment = new Today();
                } else if (item.getItemId() == R.id.progress) {
                    selectedFragment = new Progress();
                } else if (item.getItemId() == R.id.plan) {
                    selectedFragment = new MealPlan();
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                return true;
            }
        });
    }


    private void init() {
        bottomnav = findViewById(R.id.bottomnav);
        ivAvatar=findViewById(R.id.ivAvatar);
        tvUserName = findViewById(R.id.tvUserName);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(MainActivity.this, Profile.class);
                i.putExtra("userName",userName);
                startActivity(i);
            }
        });

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_checked},
                new int[]{}
        };

        int[] colors = new int[]{
                ContextCompat.getColor(this, R.color.white),  // Green color for active state
                ContextCompat.getColor(this, R.color.transparentwhite),  // Green color for active state
        };

        ColorStateList colorStateList = new ColorStateList(states, colors);

// Set the item icon and text tint list
        bottomnav.setItemIconTintList(colorStateList);

        bottomnav.setItemTextColor(colorStateList);
    }

    private void getUserData() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
                ImageUrl = snapshot.child("ImageUrl").getValue(String.class);
                if(ImageUrl!=null)
                    Picasso.get().load(ImageUrl).
                            transform(new CircleTransformation()). // Apply circular transformation
                            into(ivAvatar);
                tvUserName.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }}
}
