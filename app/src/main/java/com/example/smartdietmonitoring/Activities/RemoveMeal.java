package com.example.smartdietmonitoring.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.Adapters.RemoveItemAdapter;
import com.example.smartdietmonitoring.Adapters.SingleFoodItemAdapter;
import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.Fragments.MealPlan;
import com.example.smartdietmonitoring.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class RemoveMeal extends AppCompatActivity {

    RecyclerView rvBreakfastItems, rvLunchItems, rvDinnerItems;

    ImageView ivBack;

    RemoveItemAdapter breakfastAdapter, lunchAdapter, dinnerAdapter;

    TextView tvFoodLogged, tvBreakfastItems, tvLunchItems, tvDinnerItems;

    ArrayList<FoodItem> breakfastItems;
    ArrayList<FoodItem> lunchItems;
    ArrayList<FoodItem> dinnerItems;

    LinearLayoutManager breakfastLayout;
    LinearLayoutManager lunchLayout;
    LinearLayoutManager dinnerLayout;

    AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_meal);
        //Init
        init();
        //getData


    }

    private void init() {
        //Init
        tvFoodLogged=findViewById(R.id.tvFoodLogged);
        tvBreakfastItems = findViewById(R.id.tvBreakfastItems);
        tvLunchItems = findViewById(R.id.tvLunchItems);
        tvDinnerItems= findViewById(R.id.tvDinnerItems);

        rvBreakfastItems = findViewById(R.id.rvBreakfastItems);
        rvLunchItems = findViewById(R.id.rvLunchItems);
        rvDinnerItems = findViewById(R.id.rvDinnerItems);

        ivBack = findViewById(R.id.ivBack);

        breakfastLayout = new LinearLayoutManager(this);
        lunchLayout = new LinearLayoutManager(this);
        dinnerLayout = new LinearLayoutManager(this);


        breakfastItems = new ArrayList<>();
        lunchItems = new ArrayList<>();
        dinnerItems = new ArrayList<>();


        //setupRecyclerView
        setupRecyclerViews();


        //Listeners
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                Intent i = new Intent(RemoveMeal.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

        //Setting Visibility Initially
        tvFoodLogged.setVisibility(View.GONE);

        //Notifying
        Log.e("Size1", String.valueOf(breakfastItems.size()));
        Log.e("Size2", String.valueOf(lunchItems.size()));
        Log.e("Size3", String.valueOf(dinnerItems.size()));

        if (breakfastItems.size()==0 && lunchItems.size()==0 && dinnerItems.size()==0){
            tvFoodLogged.setVisibility(View.VISIBLE);
            rvBreakfastItems.setVisibility(View.GONE);
            rvLunchItems.setVisibility(View.GONE);
            rvDinnerItems.setVisibility(View.GONE);
            tvBreakfastItems.setVisibility(View.GONE);
            tvLunchItems.setVisibility(View.GONE);
            tvDinnerItems.setVisibility(View.GONE);
        }



    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(RemoveMeal.this, MainActivity.class);
        startActivity(i);
        finish();
        super.onBackPressed();
    }

    private void setupRecyclerViews() {

        String breakfastItemsJson = getIntent().getStringExtra("breakFastItems");
        String lunchItemsJson = getIntent().getStringExtra("lunchItems");
        String dinnerItemsJson = getIntent().getStringExtra("dinnerItems");


        //Setting up lists
        breakfastItems = deserializeFoodItems(breakfastItemsJson);
        lunchItems = deserializeFoodItems(lunchItemsJson);
        dinnerItems = deserializeFoodItems(dinnerItemsJson);

        //Adapter
        breakfastAdapter = new RemoveItemAdapter(breakfastItems, this);
        lunchAdapter = new RemoveItemAdapter(lunchItems,this);
        dinnerAdapter = new RemoveItemAdapter(dinnerItems, this);

        //Layout Manager
        rvBreakfastItems.setLayoutManager(breakfastLayout);
        rvLunchItems.setLayoutManager(lunchLayout);
        rvDinnerItems.setLayoutManager(dinnerLayout);

        //Setting Adapter
        rvBreakfastItems.setAdapter(breakfastAdapter);
        rvLunchItems.setAdapter(lunchAdapter);
        rvDinnerItems.setAdapter(dinnerAdapter);




    }

    private ArrayList<FoodItem> deserializeFoodItems(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<FoodItem>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    private void showCustomDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(RemoveMeal.this).inflate(R.layout.dialog, null);

        // Initialize Lottie animation view
        LottieAnimationView animationView = dialogView.findViewById(R.id.dialog);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(RemoveMeal.this);
        builder.setView(dialogView);
        builder.setCancelable(false);


        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }

}