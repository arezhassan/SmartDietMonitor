package com.example.smartdietmonitoring.Fragments;

import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.Activities.MainActivity;
import com.example.smartdietmonitoring.Activities.RemoveMeal;
import com.example.smartdietmonitoring.Adapters.SingleFoodItemAdapter;
import com.example.smartdietmonitoring.Activities.AddFoodItem;
import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class MealPlan extends Fragment {
    View view;
    CardView cardBreakfast, cardAddBreakfast, cardLunch, cardAddLunch, cardDinner, cardAddDinner;
    RecyclerView rvBreakFastItems, rvLunchItems, rvDinnerItems;
    TextView tvBreakfastCalories, tvLunchCalories, tvDinnerCalories, tvDeleteItems;


    AlertDialog dialog;



    ArrayList<FoodItem> breakfastItems;
    SingleFoodItemAdapter breakfastAdapter;


    ArrayList<FoodItem> lunchItems;
    SingleFoodItemAdapter lunchAdapter;


    ArrayList<FoodItem> dinnerItems;
    SingleFoodItemAdapter dinnerAdapter;


    String mealType;

    LinearLayoutManager breakfastLayout;
    LinearLayoutManager lunchLayout;
    LinearLayoutManager dinnerLayout;
    private int totalDayCalories = 0; // Define a global variable



    public void onStart(){
        super.onStart();
        if(!isNetworkAvailable()){
            showInternetDialog();
        }else{
            showCustomDialog();
            calculateBreakfastCalories();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_meal_plan, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        //Bind Views


        cardBreakfast = view.findViewById(R.id.cardBreakfast);
        cardAddBreakfast = view.findViewById(R.id.cardAddBreakfast);
        cardLunch = view.findViewById(R.id.cardLunch);
        cardAddLunch = view.findViewById(R.id.cardAddLunch);
        cardDinner = view.findViewById(R.id.cardDinner);
        cardAddDinner = view.findViewById(R.id.cardAddDinner);

        rvBreakFastItems = view.findViewById(R.id.rvBreakFastItems);
        rvLunchItems = view.findViewById(R.id.rvLunchItems);
        rvDinnerItems = view.findViewById(R.id.rvDinnerItems);

        tvBreakfastCalories = view.findViewById(R.id.tvBreakfastCalories);
        tvLunchCalories = view.findViewById(R.id.tvLunchCalories);
        tvDinnerCalories = view.findViewById(R.id.tvDinnerCalories);
        tvDeleteItems=view.findViewById(R.id.tvDeleteItems);

        //calculating calories







        // Set up any event listeners or additional functionality here
        //RecyclerView
        breakfastItems = new ArrayList<>();

        breakfastLayout = new LinearLayoutManager(getContext());
        breakfastAdapter = new SingleFoodItemAdapter(breakfastItems);
        rvBreakFastItems.setAdapter(breakfastAdapter);
        rvBreakFastItems.setLayoutManager(breakfastLayout);
        getBreakfastData();


        lunchItems = new ArrayList<>();

        lunchLayout = new LinearLayoutManager(getContext());
        lunchAdapter = new SingleFoodItemAdapter(lunchItems);
        rvLunchItems.setAdapter(lunchAdapter);
        rvLunchItems.setLayoutManager(lunchLayout);
        getLunchData();


        dinnerItems = new ArrayList<>();

        dinnerLayout = new LinearLayoutManager(getContext());
        dinnerAdapter = new SingleFoodItemAdapter(dinnerItems);
        rvDinnerItems.setAdapter(dinnerAdapter);
        rvDinnerItems.setLayoutManager(dinnerLayout);
        getDinnerData();

        tvDeleteItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireContext(), RemoveMeal.class);
                i.putExtra("breakFastItems", serializeFoodItems(breakfastItems));
                i.putExtra("lunchItems", serializeFoodItems(lunchItems));
                i.putExtra("dinnerItems", serializeFoodItems(dinnerItems));
                startActivity(i);
                ((Activity) requireContext()).finish();
            }
        });


        cardAddBreakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealType = "breakfast";
                Intent i = new Intent(getContext(), AddFoodItem.class);
                i.putExtra("mealType", mealType);
                startActivity(i);
            }
        });
        cardLunch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealType = "lunch";
                Intent i = new Intent(getContext(), AddFoodItem.class);
                i.putExtra("mealType", mealType);
                startActivity(i);
            }
        });
        cardAddDinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealType = "dinner";
                Intent i = new Intent(getContext(), AddFoodItem.class);
                i.putExtra("mealType", mealType);
                startActivity(i);
            }
        });


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

    private void getBreakfastData() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                    .child("meals")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(getCurrentDate())
                    .child("breakfast");

            breakfastRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                        // Assuming each child node represents a food item
                        FoodItem foodItem = foodSnapshot.getValue(FoodItem.class);
                        if (foodItem != null) {
                            breakfastItems.add(foodItem);
                        }
                    }
                    breakfastAdapter.notifyDataSetChanged();

                    // Now you have all breakfast food items in the ArrayList
                    // You can perform further operations with breakfastList
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors here
                }
            });
        }
    }

    private void getLunchData() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference lunchData = FirebaseDatabase.getInstance().getReference()
                    .child("meals")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(getCurrentDate())
                    .child("lunch");

            lunchData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                        // Assuming each child node represents a food item
                        FoodItem foodItem = foodSnapshot.getValue(FoodItem.class);
                        if (foodItem != null) {
                            lunchItems.add(foodItem);
                        }
                    }
                    lunchAdapter.notifyDataSetChanged();

                    // Now you have all breakfast food items in the ArrayList
                    // You can perform further operations with breakfastList
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors here
                }
            });
        }
    }


    private void getDinnerData() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference dinnerData = FirebaseDatabase.getInstance().getReference()
                    .child("meals")
                    .child(FirebaseAuth.getInstance().getUid())
                    .child(getCurrentDate())
                    .child("dinner");

            dinnerData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot foodSnapshot : snapshot.getChildren()) {
                        // Assuming each child node represents a food item
                        FoodItem foodItem = foodSnapshot.getValue(FoodItem.class);
                        if (foodItem != null) {
                            dinnerItems.add(foodItem);
                        }
                    }
                    dinnerAdapter.notifyDataSetChanged();

                    // Now you have all breakfast food items in the ArrayList
                    // You can perform further operations with breakfastList
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle errors here
                }
            });
        }
    }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }


    private void calculateBreakfastCalories() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                    .child("meals").child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("breakfast");
            breakfastRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalCalories = 0;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        int calories = childSnapshot.child("totalCalorie").getValue(Integer.class);
                        totalCalories += calories;
                    }
                    totalDayCalories += totalCalories; // Accumulate breakfast calories
                    tvBreakfastCalories.setText(totalCalories + " Cals");
                    calculateLunchCalories();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    System.err.println("Error fetching breakfast calories: " + databaseError.getMessage());
                    dialog.dismiss();

                }
            });
        }

        // Attach a listener for retrieving data

    }

    private void calculateLunchCalories() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference lunchRef = FirebaseDatabase.getInstance().getReference()
                    .child("meals").child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("lunch");

            // Attach a listener for retrieving data
            lunchRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalCalories = 0;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        int calories = childSnapshot.child("totalCalorie").getValue(Integer.class);
                        totalCalories += calories;
                    }
                    totalDayCalories += totalCalories; // Accumulate lunch calories
                    tvLunchCalories.setText(totalCalories + " Cals");
                    calculateDinnerCalorie();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    System.err.println("Error fetching lunch calories: " + databaseError.getMessage());
                    dialog.dismiss();

                }
            });
        }
    }

    private void calculateDinnerCalorie() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference dinnerRef = FirebaseDatabase.getInstance().getReference()
                    .child("meals").child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("dinner");

            // Attach a listener for retrieving data
            dinnerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int totalCalories = 0;
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                        int calories = childSnapshot.child("totalCalorie").getValue(Integer.class);
                        totalCalories += calories;
                    }
                    totalDayCalories += totalCalories; // Accumulate dinner calories
                    AddTotalCaloriesToDB(totalDayCalories);
                    tvDinnerCalories.setText(totalCalories + " Cals");
                    dialog.dismiss();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle errors
                    System.err.println("Error fetching dinner calories: " + databaseError.getMessage());
                    dialog.dismiss();

                }
            });
        }
    }

    private String serializeFoodItems(ArrayList<FoodItem> foodItems) {
        Gson gson = new Gson();
        return gson.toJson(foodItems);
    }


    private void AddTotalCaloriesToDB(int calories) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("meals")
                    .child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("totalCalories");
            db.setValue(calories);
        }
    }
    private void showCustomDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog, null);

        // Initialize Lottie animation view
        LottieAnimationView animationView = dialogView.findViewById(R.id.dialog);

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setCancelable(false);


        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }
    private void refreshData() {
        // Clear existing data lists
        breakfastItems.clear();
        lunchItems.clear();
        dinnerItems.clear();

        // Refresh breakfast, lunch, and dinner data
        getBreakfastData();
        getLunchData();
        getDinnerData();

        // Refresh calorie calculations
        calculateBreakfastCalories();
        calculateLunchCalories();
        calculateDinnerCalorie();
    }
    private void showInternetDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialognointernet, null);

        // Initialize Lottie animation view

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setView(dialogView);
        builder.setCancelable(false);


        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireActivity().getSystemService (Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}


