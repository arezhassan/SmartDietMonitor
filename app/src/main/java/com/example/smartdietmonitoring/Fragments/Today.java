package com.example.smartdietmonitoring.Fragments;

import static android.content.ContentValues.TAG;
import static android.content.Context.CONNECTIVITY_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.smartdietmonitoring.Utilities.DateUtils.getCurrentDate;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.Activities.LoginPage;
import com.example.smartdietmonitoring.Activities.Profile;
import com.example.smartdietmonitoring.Activities.RegisterPage1;
import com.example.smartdietmonitoring.Activities.ViewCalorieBreakdown;
import com.example.smartdietmonitoring.Adapters.SingleFoodItemAdapter;
import com.example.smartdietmonitoring.Adapters.SuggestedFoodAdapter;
import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Utilities.CircleTransformation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Today extends Fragment {

    private int breakfastCals;
    private int lunchCals;
    private int dinnerCals;

    ImageView ivAvatar;
    AlertDialog dialog;

    ArrayList<FoodItem> suggestedFoods;
    SuggestedFoodAdapter suggestedFoodAdapter;

    LinearLayoutManager linearLayoutManager;

    RecyclerView rvSuggestedFoods;

    @Override
    public void onStart() {
        super.onStart();


        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent i = new Intent(requireContext(), LoginPage.class);
            startActivity(i);
            requireActivity().finish(); // Finish the activity if no user is signed in
        }


    }

    View view;


    TextView tvToday, tvUserName, tvViewProfile;
    CardView cardView, cardViewDinner, cardViewBreakfast, cardViewLunch;
    TextView tvCaloriesleft;
    ProgressBar progressCaloriesleft;
    TextView tvCaloriesconsumed;
    ProgressBar progressBar2;
    String username;
    TextView tvBreakfastcalories;
    TextView tvLunchcalories;
    TextView tvDinnercalories;

    int caloriesConsumed;
    int caloriesLeft;

    int goalCalories;


    String ImageUrl;
    double progressCaloriesConsumed;
    double progressCaloriesLeft;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_today, container, false);
        initializeViews();
        showCustomDialog();
        getData();


        return view;
    }


    private void getData() {


        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("users").child(FirebaseAuth.getInstance().getUid());
            // Add a ValueEventListener to fetch goalCalories from the database
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    // Check if the user node exists
                    if (dataSnapshot.exists()) {
                        // Retrieve goalCalories value from the database
                        goalCalories = dataSnapshot.child("goalCalories").getValue(Integer.class);
                        username = dataSnapshot.child("userName").getValue(String.class);
                        ImageUrl = dataSnapshot.child("ImageUrl").getValue(String.class);
                        if(ImageUrl!=null)
                            Picasso.get().load(ImageUrl).
                                    transform(new CircleTransformation()). // Apply circular transformation
                                    into(ivAvatar);

                        // Now you have goalCalories, you can perform further actions
                        // For example, calculate caloriesConsumed and caloriesLeft
                        calculateCalories();
                    } else {
                        // Handle case where user node does not exist
                        Log.d(TAG, "User node does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Handle potential errors
                    Log.e(TAG, "Failed to read value.", databaseError.toException());
                }
            });
        }
    }


    // Method to calculate caloriesConsumed and caloriesLeft based on fetched goalCalories
    private void calculateCalories() {
        // Perform calculations based on the fetched goalCalories
        // For example:
        // Getting Calories Consumed Today
        DatabaseReference totalCaloriesRef = FirebaseDatabase.getInstance().getReference()
                .child("meals").child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("totalCalories");

        totalCaloriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Check if the snapshot has a value
                if (snapshot.exists()) {
                    int totalCalories = snapshot.getValue(Integer.class);

                    // Use totalCalories where needed
                    Log.d("Total Calories", "Total calories: " + totalCalories);
                    caloriesConsumed = totalCalories;
                    caloriesLeft = goalCalories - caloriesConsumed;
                    tvCaloriesconsumed.setText(String.valueOf(totalCalories));
                    tvCaloriesleft.setText(String.valueOf(caloriesLeft));
                    tvUserName.setText(username);
                    calculateBreakfastCalories();
                    calculateLunchCalories();
                    calculateDinnerCalorie();
                    setProgressValues();

                } else {
                    // Handle the case where totalCalories node doesn't exist or has no value
                    Log.d("Total Calories", "No total calories found for the current date");
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database read error
                Log.e("Database Error", "Error fetching total calories: " + error.getMessage());
            }
        });


        // Now you have caloriesConsumed, caloriesLeft, and goalCalories
        // You can use them as needed in your application
    }


    private void setProgressValues() {
        // Calculate the percentage of calories consumed and left
        double percentageCaloriesConsumed = (double) caloriesConsumed / goalCalories * 100;
        double percentageCaloriesLeft = (double) caloriesLeft / goalCalories * 100;

        // Set the progress values for the progress bars
        progressCaloriesleft.setProgress((int) percentageCaloriesLeft);
        progressBar2.setProgress((int) percentageCaloriesConsumed);
        dialog.dismiss();
    }


    private void initializeViews() {

        suggestedFoods=new ArrayList<>();
        linearLayoutManager=new LinearLayoutManager(requireContext());
        suggestedFoodAdapter=new SuggestedFoodAdapter(suggestedFoods,requireContext());
        rvSuggestedFoods=view.findViewById(R.id.rvSuggestedFoods);
        rvSuggestedFoods.setAdapter(suggestedFoodAdapter);
        rvSuggestedFoods.setLayoutManager(linearLayoutManager);


        FoodItem item= new FoodItem();
        item.setFoodName("Chocolate Cake");
        item.setServingSize("0.5 Pound");
        item.setCalories(374);
        item.setRecipeLink("https://sugarspunrun.com/chocolate-cake/");


        FoodItem item1= new FoodItem();
        item1.setFoodName("Chicken Biryani");
        item1.setServingSize("250 Grams");
        item1.setCalories(310);
        item1.setRecipeLink("https://www.indianhealthyrecipes.com/chicken-biryani-recipe/");


        FoodItem item2= new FoodItem();
        item2.setFoodName("Bhindi Goshti");
        item2.setServingSize("250 Grams");
        item2.setCalories(310);
        item2.setRecipeLink("https://fatimacooks.net/bhindi-gosht-recipe-okra-lamb-mutton-curry/");





        suggestedFoods.add(item1);
        suggestedFoods.add(item2);
        suggestedFoods.add(item);




        suggestedFoodAdapter.notifyDataSetChanged();












        tvViewProfile=view.findViewById(R.id.tvViewProfile);
        tvToday = view.findViewById(R.id.tvToday);
        cardView = view.findViewById(R.id.cardView);
        tvCaloriesleft = view.findViewById(R.id.tvCaloriesleft);
        progressCaloriesleft = view.findViewById(R.id.progressCaloriesleft);
        tvCaloriesconsumed = view.findViewById(R.id.tvCaloriesconsumed);
        progressBar2 = view.findViewById(R.id.progressBar2);
        tvBreakfastcalories = view.findViewById(R.id.tvBreakfastcalories);
        tvLunchcalories = view.findViewById(R.id.tvLunchcalories);
        tvDinnercalories = view.findViewById(R.id.tvDinnercalories);
        tvUserName = view.findViewById(R.id.tvUserName);

        cardViewBreakfast = view.findViewById(R.id.cardViewBreakfast);
        cardViewLunch = view.findViewById(R.id.cardViewLunch);
        cardViewDinner = view.findViewById(R.id.cardViewDinner);

        ivAvatar=view.findViewById(R.id.ivAvatar);

        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getContext(), Profile.class);
                i.putExtra("userName",username);
                requireContext().startActivity(i);
            }
        });
        tvViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getContext(), Profile.class);
                i.putExtra("userName",username);
                requireContext().startActivity(i);
            }
        });
        tvUserName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i= new Intent(getContext(), Profile.class);
                i.putExtra("userName",username);
                requireContext().startActivity(i);
            }
        });


        // Now you can add any functionality you want to these views
        // For example, setting text or handling click events


        // Add more functionality here as needed


        // Set max progress for progress bars
        progressCaloriesleft.setMax(100);
        progressBar2.setMax(100);

        // Stop the rotation of the progress bars
        progressCaloriesleft.setIndeterminate(false);
        progressBar2.setIndeterminate(false);


        //Card View Listener




    }


    //Calculate Calories for textviews
    public void calculateBreakfastCalories() {
        DatabaseReference breakfastRef = FirebaseDatabase.getInstance().getReference()
                .child("meals").child(FirebaseAuth.getInstance().getUid()).child(getCurrentDate()).child("breakfast");

        // Attach a listener for retrieving data
        breakfastRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int totalCalories = 0;
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    int calories = childSnapshot.child("totalCalorie").getValue(Integer.class);
                    totalCalories += calories;

                }
                breakfastCals = totalCalories;
                tvBreakfastcalories.setText(String.valueOf(breakfastCals) + " Cals");
                cardViewBreakfast.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), ViewCalorieBreakdown.class);
                        i.putExtra("foodType", "Breakfast");
                        i.putExtra("foodCals", breakfastCals);
                        startActivity(i);


                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.err.println("Error fetching breakfast calories: " + databaseError.getMessage());
            }
        });


    }

    public void calculateLunchCalories() {
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
                lunchCals = totalCalories;
                tvLunchcalories.setText(String.valueOf(lunchCals) + " Cals");
                cardViewLunch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), ViewCalorieBreakdown.class);
                        i.putExtra("foodType", "Lunch");
                        i.putExtra("foodCals", lunchCals);
                        startActivity(i);

                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.err.println("Error fetching lunch calories: " + databaseError.getMessage());
            }
        });
    }

    private void calculateDinnerCalorie() {
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
                dinnerCals = totalCalories;
                tvDinnercalories.setText(String.valueOf(dinnerCals) + " Cals");

                cardViewDinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getContext(), ViewCalorieBreakdown.class);
                        i.putExtra("foodType", "Dinner");
                        i.putExtra("foodCals", dinnerCals);
                        startActivity(i);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle errors
                System.err.println("Error fetching dinner calories: " + databaseError.getMessage());
            }
        });
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



}
