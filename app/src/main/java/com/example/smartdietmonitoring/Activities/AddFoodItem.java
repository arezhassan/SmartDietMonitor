package com.example.smartdietmonitoring.Activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.smartdietmonitoring.Adapters.FoodSearchResultAdapter;
import com.example.smartdietmonitoring.Apis.EdamamFoodSearchApi;
import com.example.smartdietmonitoring.R;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AddFoodItem extends AppCompatActivity {
    CardView cardSearchfood;
    TextInputEditText etSearch;

    RecyclerView recyclerView;
    List<String> foodItems;
    FoodSearchResultAdapter adapter;

    String mealType;

    LinearLayoutManager linearLayout;

    ProgressBar progressBarFoodSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food_item);
        initializeViews();
        setupListeners();
        foodItems = new ArrayList<>();
        adapter = new FoodSearchResultAdapter(this, foodItems, mealType);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(linearLayout);
    }

    private void initializeViews() {
        Intent i=getIntent();
        mealType=i.getStringExtra("mealType");
        linearLayout = new LinearLayoutManager(AddFoodItem.this);
        cardSearchfood = findViewById(R.id.cardSearchfood);
        recyclerView = findViewById(R.id.rvFoodItems);
        etSearch = findViewById(R.id.etSearch);
        progressBarFoodSearch = findViewById(R.id.progressBarFoodSearch);
        progressBarFoodSearch.setVisibility(View.GONE);


    }

    private void setupListeners() {
        cardSearchfood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarFoodSearch.setVisibility(View.VISIBLE);
                cardSearchfood.setVisibility(View.GONE);
                // Perform food search when the search card is clicked
                String query = etSearch.getText().toString().trim();
                if (!query.isEmpty()) {
                    performFoodSearch(query);

                } else {
                    progressBarFoodSearch.setVisibility(View.GONE);
                    cardSearchfood.setVisibility(View.VISIBLE);
                    Toast.makeText(AddFoodItem.this, "Enter a search query", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void performFoodSearch(String query) {
        new EdamamFoodSearchApi(new EdamamFoodSearchApi.EdamamApiListener() {
            @Override
            public void onEdamamApiResponse(JSONArray response) {
                foodItems.clear();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        foodItems.add(response.getString(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressBarFoodSearch.setVisibility(View.VISIBLE);
                        cardSearchfood.setVisibility(View.GONE);
                    }
                }
                adapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                progressBarFoodSearch.setVisibility(View.GONE);
                cardSearchfood.setVisibility(View.VISIBLE);
            }
        }).execute(query);
    }

    private List<String> parseFoodItems(JSONArray jsonArray) {
        List<String> foodItems = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                String foodItem = jsonArray.getString(i); // Retrieve each element as a String
                foodItems.add(foodItem);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing JSON array", e);
            progressBarFoodSearch.setVisibility(View.VISIBLE);
            cardSearchfood.setVisibility(View.GONE);
        }
        return foodItems;
    }


}

