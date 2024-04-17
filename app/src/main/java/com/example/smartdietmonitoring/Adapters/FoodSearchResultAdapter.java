package com.example.smartdietmonitoring.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdietmonitoring.Activities.MainActivity;
import com.example.smartdietmonitoring.R;
import com.example.smartdietmonitoring.Activities.SelectFoodServing;

import java.util.List;

public class FoodSearchResultAdapter extends RecyclerView.Adapter<FoodSearchResultAdapter.ViewHolder> {
    private Context context;
    private List<String> foodItems;

    String foodId, mealType;

    public FoodSearchResultAdapter(Context context, List<String> foodItems, String mealType) {
        this.context = context;
        this.foodItems = foodItems;
        this.mealType=mealType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefoodsearchitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String foodItem = foodItems.get(position);
        holder.bind(foodItem);
    }

    @Override
    public int getItemCount() {
        return foodItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewFoodItem;
        ImageView ivAddFoodItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewFoodItem = itemView.findViewById(R.id.tvSearchedFoodName);
            ivAddFoodItem = itemView.findViewById(R.id.ivAddFood);
        }

        public void bind(String foodItem) {
            textViewFoodItem.setText(foodItem);
            ivAddFoodItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SelectFoodServing.class);
                    i.putExtra("foodItem", foodItem);
                    i.putExtra("foodId", foodId);
                    i.putExtra("mealType",mealType);
                    context.startActivity(i);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, SelectFoodServing.class);
                    i.putExtra("foodItem", foodItem);
                    i.putExtra("foodId", foodId);
                    i.putExtra("mealType",mealType);
                    context.startActivity(i);
                    if (context instanceof Activity) {
                        ((Activity) context).finish();
                    }
                }
            });
        }
    }
}
