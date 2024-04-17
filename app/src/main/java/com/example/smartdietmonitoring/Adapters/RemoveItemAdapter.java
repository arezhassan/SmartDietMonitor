package com.example.smartdietmonitoring.Adapters;


import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.smartdietmonitoring.Activities.RemoveMeal;
import com.example.smartdietmonitoring.DataClass.FoodItem;
import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.List;

public class RemoveItemAdapter extends RecyclerView.Adapter<RemoveItemAdapter.ViewHolder> {

    private List<FoodItem> foodItemList;

    Context context;

    AlertDialog dialog;

    public RemoveItemAdapter(List<FoodItem> foodItemList, Context context) {
        this.foodItemList = foodItemList;
        this.context=context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.removemealitem, parent, false);
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

        ImageView ivDeleteFoodItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFoodItemName = itemView.findViewById(R.id.tvFoodItemName);
            tvServingSize = itemView.findViewById(R.id.tvFoodItemServingSize);
            ivDeleteFoodItem = itemView.findViewById(R.id.ivDeleteFoodItem);

            ivDeleteFoodItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCustomDialog();
                    String foodKey = foodItemList.get(getAdapterPosition()).getFoodKey();
                    Log.e("FoodKey",foodKey);
                    String currentDate = getCurrentDate();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("meals").child(FirebaseAuth.getInstance().getUid()).child(currentDate);

// Query to find the mealType node containing the foodKey
                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                                for (DataSnapshot mealSnapshot : dateSnapshot.getChildren()) {
                                    String mealType = dateSnapshot.getKey();
                                    Log.e("MealType: ", mealType);

                                    Log.e("Adapter Position1: ",String.valueOf(getAdapterPosition()));
                                    if (mealType.equals("breakfast") || mealType.equals("lunch") || mealType.equals("dinner")) {
                                        Log.e("Adapter Position2: ",String.valueOf(getAdapterPosition()));

                                        if (dateSnapshot.hasChild(foodKey)) {
                                            // Delete the foodKey node under the found mealType
                                            int index = -1;
                                            for (int i = 0; i < foodItemList.size(); i++) {
                                                if (foodItemList.get(i).getFoodKey().equals(foodKey)) {
                                                    index = i;
                                                    break;
                                                }
                                            }
                                            if (index != -1) {
                                                // Remove the item from foodItemList
                                                foodItemList.remove(index);
                                                // Notify adapter about the item removal
                                                notifyItemRemoved(index);
                                            }
                                            dateSnapshot.child(foodKey).getRef().removeValue();
                                            dialog.dismiss();
                                            Log.e("Adapter Position3: ",String.valueOf(getAdapterPosition()));
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            // Handle any errors
                        }
                    });

                }

            });


        }
    }
    public String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
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
    private void showCustomDialog() {
        // Inflate the custom layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialogitemremoved, null);


        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setIcon(R.drawable.emergency);
        builder.setCancelable(false);


        // Show the dialog
        dialog = builder.create();
        dialog.show();
    }


}









