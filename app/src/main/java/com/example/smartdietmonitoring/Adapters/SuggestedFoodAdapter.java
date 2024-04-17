package com.example.smartdietmonitoring.Adapters;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdietmonitoring.Activities.ViewRecipe;
import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.R;

import java.util.List;

public class SuggestedFoodAdapter extends RecyclerView.Adapter<SuggestedFoodAdapter.ViewHolder> {

    private List<FoodItem> foodItemList;
    Context context;

    public SuggestedFoodAdapter(List<FoodItem> foodItemList, Context context) {


        this.foodItemList = foodItemList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlesuggesteditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);

        // Bind data to TextViews
        holder.tvFoodItemName.setText(foodItem.getFoodName());
        holder.tvServingSize.setText(String.valueOf(foodItem.getCalories())+ " Cals");


        holder.ivViewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRecipe(foodItem.getRecipeLink(), foodItem.getFoodName());
            }
        });

        holder.tvViewRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToRecipe(foodItem.getRecipeLink(),foodItem.getFoodName());
            }
        });

    }

    private void navigateToRecipe(String recipeLink, String recipeName) {

        Intent i=new Intent(context, ViewRecipe.class);
        i.putExtra("recipeLink",recipeLink);
        i.putExtra("recipeName",recipeName);
        context.startActivity(i);


    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodItemName;
        TextView tvServingSize, tvViewRecipe;

        ImageView ivViewRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodItemName = itemView.findViewById(R.id.tvFoodItemName);
            tvServingSize = itemView.findViewById(R.id.tvServingSize);
            ivViewRecipe= itemView.findViewById(R.id.ivViewRecipe);
            tvViewRecipe=itemView.findViewById(R.id.tvViewRecipe);
        }
    }
}
