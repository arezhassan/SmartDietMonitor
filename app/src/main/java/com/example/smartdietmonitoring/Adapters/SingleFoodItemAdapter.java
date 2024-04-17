package com.example.smartdietmonitoring.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.R;

import java.util.List;

public class SingleFoodItemAdapter extends RecyclerView.Adapter<SingleFoodItemAdapter.ViewHolder> {

    private List<FoodItem> foodItemList;

    public SingleFoodItemAdapter(List<FoodItem> foodItemList) {
        this.foodItemList = foodItemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlefooditem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodItem foodItem = foodItemList.get(position);

        // Bind data to TextViews
        holder.tvFoodItemName.setText(foodItem.getFoodName());
        holder.tvServingSize.setText(foodItem.getServingSize());
    }

    @Override
    public int getItemCount() {
        return foodItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodItemName;
        TextView tvServingSize;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodItemName = itemView.findViewById(R.id.tvFoodItemName);
            tvServingSize = itemView.findViewById(R.id.tvServingSize);
        }
    }
}
