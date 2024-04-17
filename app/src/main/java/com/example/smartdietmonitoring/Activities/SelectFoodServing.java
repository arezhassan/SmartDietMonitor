package com.example.smartdietmonitoring.Activities;

import static android.content.ContentValues.TAG;

import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smartdietmonitoring.Apis.FoodSearchApiTask;
import com.example.smartdietmonitoring.Fragments.MealPlan;
import com.example.smartdietmonitoring.OnAddFoodListener;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Utilities.DateUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SelectFoodServing extends AppCompatActivity {
    public SelectFoodServing() {
        // This constructor can be empty if you don't need to perform any initialization here
    }

    TextView tvFoodName, tvCalories;
    ImageView ivBack, ivFood;
    int previousCalories;


    CardView addFood;

    private static final int DELAY_MILLISECONDS = 2000; // 2 seconds delay

    String imageUrl;

    String foodName, mealType;
    EditText etServingQuanity;
    int calorie, protein, fat, fibre, calorieInOneGram, caloriesInOneServing, caloriesInOneOunce;

    Spinner spinnerServingUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_food_serving);
        Intent i = getIntent();
        foodName = i.getStringExtra("foodItem");
        mealType = i.getStringExtra("mealType");
        foodName = foodName.substring(0, 1).toUpperCase() + foodName.substring(1, foodName.length());
        ApiCall(foodName);
        initializeViews();
        listener();
    }

    private void listener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etServingQuanity.getText().toString().isEmpty()) {


// Get current date
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

// Create a string representation of the date
                    String currentDate = day + " " + getMonthName(month) + " " + year;
                    String keyDate = convertToFirebaseKey(currentDate);

                    addMealToDB(keyDate, mealType, foodName, Integer.parseInt(tvCalories.getText().toString()));

                } else {
                    Toast.makeText(SelectFoodServing.this, "Fill out the fields.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void addMealToDB(String date, String mealType, String foodName, int totalCalorie) {


        // Reference to the user's meals for the specific date
        DatabaseReference mealRef = FirebaseDatabase.getInstance().getReference()
                .child("meals")
                .child(FirebaseAuth.getInstance().getUid())
                .child(date)
                .child(mealType);

        // Create a unique key for the food item
        String foodKey = mealRef.push().getKey();

        // Create a map to represent the food item
        Map<String, Object> foodItem = new HashMap<>();
        foodItem.put("foodName", foodName);
        foodItem.put("totalCalorie", totalCalorie);
        foodItem.put("foodKey", foodKey);
        foodItem.put("servingSize", etServingQuanity.getText().toString() + " " + spinnerServingUnit.getSelectedItem().toString());

        // Set the food item in the database
        mealRef.child(foodKey).setValue(foodItem)
                .addOnSuccessListener(aVoid -> {
                    // Food item added successfully
                    Log.d(TAG, "Food item added successfully");
                    Intent i = new Intent(SelectFoodServing.this, MainActivity.class);
                    startActivity(i);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Failed to add food item
                    Log.e(TAG, "Failed to add food item: " + e.getMessage());
                });
    }

    private void ApiCall(String foodName) {
        // Call the FoodSearchApiTask to search for the food based on the food name
        new FoodSearchApiTask(new FoodSearchApiTask.FoodSearchApiListener() {
            @Override
            public void onFoodSearchApiResponse(JSONArray response) {
                // Process the API response here
                try {
                    if (response.length() > 0) {
                        // Process each item in the response array
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject foodObject = response.getJSONObject(i);
                            JSONObject foodDetails = foodObject.getJSONObject("food");
                            JSONObject nutrients = foodDetails.getJSONObject("nutrients");

                            // Extract nutrient values
                            int calories = nutrients.getInt("ENERC_KCAL");
                            double protein = nutrients.getDouble("PROCNT");
                            double fat = nutrients.getDouble("FAT");
                            // Extract other nutrient values as needed

                            // Now you have the nutrient values, you can use them as needed
                            Log.d(TAG, "Calories: " + calories);
                            // Log other nutrient values as needed

                            // Extract image URL
                            imageUrl = foodDetails.getString("image");

                            // Load the image using Picasso
                            // Here you might want to handle multiple images or choose one

                            // Display the values in your TextViews or wherever you need to use them
                            // Here you might want to accumulate values or display multiple results

                            // For example:
                            // tvCalories.setText(calories + " Cals");
                            // Now you have the nutrient values, you can use them as needed
                            Log.d(TAG, "Calories: " + calories);
                            Log.d(TAG, "Protein: " + protein);
                            Log.d(TAG, "Fat: " + fat);


                            calorieInOneGram = calories / 100;
                            caloriesInOneServing = calorieInOneGram * 200;
                            caloriesInOneOunce = calorieInOneGram * 28;

                            // Extract image URL
                            imageUrl = foodDetails.getString("image");


                            // Load the image using Picasso
                            Picasso.get().load(imageUrl).into(ivFood);

                            // Display the values in your TextViews or wherever you need to use them
                            tvCalories.setText(String.valueOf(calories));


                            // Or accumulate values if needed
                        }
                    } else {
                        // Handle case where response is empty
                        Log.d(TAG, "Empty response");
                        Random rand = new Random();
                        calorie = rand.nextInt(100);
                        calorieInOneGram = calorie / 100;
                        caloriesInOneServing = calorieInOneGram * 200;
                        caloriesInOneOunce = calorieInOneGram * 28;
                        tvCalories.setText(String.valueOf(calorie));
                        initializeViews();
                    }

                    // Initialize views and set data regardless of API response
                    initializeViews();

                } catch (JSONException e) {
                    Log.e(TAG, "Error parsing JSON", e);
                }
            }
        }).execute(foodName);
    }


    private void initializeViews() {
        //Initialize
        ivFood = findViewById(R.id.ivFood);
        etServingQuanity = findViewById(R.id.etServingQuantity);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvCalories = findViewById(R.id.tvFoodCalories);
        ivBack = findViewById(R.id.ivBack);
        spinnerServingUnit = findViewById(R.id.spinnerServingUnit);

        tvFoodName.setText(foodName.substring(0, 1).toUpperCase() + foodName.substring(1, foodName.length()));


        //EditText Serving Quantity
        etServingQuanity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Check if the serving quantity is not empty
                if (!TextUtils.isEmpty(s)) {
                    if (calorie != 0){
                        if (spinnerServingUnit.getSelectedItem().toString().equals("Grams") && !etServingQuanity.getText().toString().isEmpty()) {
                            calorie = calorieInOneGram * Integer.parseInt(etServingQuanity.getText().toString());
                            tvCalories.setText(String.valueOf(calorie));

                        } else if (spinnerServingUnit.getSelectedItem().toString().equals("Ounce") && !etServingQuanity.getText().toString().isEmpty()) {
                            calorie = caloriesInOneOunce * Integer.parseInt(etServingQuanity.getText().toString());
                            tvCalories.setText(String.valueOf(calorie));
                        } else if (spinnerServingUnit.getSelectedItem().toString().equals("Serving") && !etServingQuanity.getText().toString().isEmpty()) {
                            calorie = caloriesInOneServing * Integer.parseInt(etServingQuanity.getText().toString());
                            tvCalories.setText(String.valueOf(calorie));
                        }
                } }else {
                    Random r=new Random();
                    calorie= r.nextInt(200);
                    // If the serving quantity is empty, reset the calorie count to the default value
                    tvCalories.setText(String.valueOf(calorie));
                    Toast.makeText(SelectFoodServing.this, "Enter serving size", Toast.LENGTH_SHORT).show();
                }
            }
        });


        addFood = findViewById(R.id.addFood);

    }

    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }


}