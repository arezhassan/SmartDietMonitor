package com.example.smartdietmonitoring.Fragments;

import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;
import static com.example.smartdietmonitoring.Utilities.DateUtils.getCurrentDate;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Column;
import com.anychart.enums.Anchor;
import com.anychart.enums.HoverMode;
import com.anychart.enums.Position;
import com.anychart.enums.TooltipPositionMode;
import com.example.smartdietmonitoring.Activities.AddWeight;
import com.example.smartdietmonitoring.Activities.LoginPage;
import com.example.smartdietmonitoring.Activities.ViewWeeklyProgress;
import com.example.smartdietmonitoring.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Progress extends Fragment {


    View view;

    int day1, day2, day3, day4, day5, day6, day7, avg, count;
    TextView tvAverageCalories, tvWaterCount, tvStreakCount;

    CardView cardViewWeeklyReport;
    AnyChartView anyChartView;
    Cartesian cartesian;
    Column column;
    List<DataEntry> data;

    ProgressBar progressBarWater;


    ImageView ivPlusWater, ivMinusWater, ivMenu;

    String userId, userName;

    int waterCount = 0, streakCount = 0;

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_progress, container, false);

        //Test Bar Graph
        tvAverageCalories = view.findViewById(R.id.tvAverageCalories);
        tvWaterCount = view.findViewById(R.id.tvWaterCount);
        tvStreakCount = view.findViewById(R.id.tvStreakCount);
        cardViewWeeklyReport = view.findViewById(R.id.cardViewWeeklyReport);


        progressBarWater = view.findViewById(R.id.progressBarWater);
        ivPlusWater = view.findViewById(R.id.ivPlusWater);
        ivMinusWater = view.findViewById(R.id.ivMinusWater);
        ivMenu = view.findViewById(R.id.ivMenu);
        userId = FirebaseAuth.getInstance().getUid();
        progressBarWater.setMax(9);

        getWaterCount();
        getUserName();
        menu();
        navigateToWeeklyReports();
        getStreakCount();
        setWater();


        return view;
    }

    private void getUserName() {
        DatabaseReference user = FirebaseDatabase.getInstance().getReference()
                .child(userId);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists())
                    userName = snapshot.child("userName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void menu() {
        ivMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionsBottomSheetDialogFragment bottomSheetDialogFragment = OptionsBottomSheetDialogFragment.newInstance();
                bottomSheetDialogFragment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int id = v.getId();
                        if (id == R.id.ivEditProfile) {
                            navigateToEditProfile();
                            // Handle Edit Profile option
                        } else if (id == R.id.ivLogout) {
                            FirebaseAuth.getInstance().signOut();
                            navigateToLogin();
                            // Handle Logout option
                        } else if (id == R.id.ivTrackWeight) {
                            navigateToAddWeight();
                            // Handle Track Weight option
                        }
                        bottomSheetDialogFragment.dismiss(); // Dismiss the bottom sheet dialog after an option is selected
                    }
                });
                bottomSheetDialogFragment.show(requireActivity().getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

            }
        });
    }

    private void navigateToWeeklyReports() {
        cardViewWeeklyReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(requireContext(), ViewWeeklyProgress.class);
                requireContext().startActivity(i);
            }
        });
    }

    private void navigateToEditProfile() {
        Intent i = new Intent(requireContext(), com.example.smartdietmonitoring.Activities.Profile.class);
        i.putExtra("userName", userName);
        startActivity(i);
    }

    private void navigateToLogin() {
        Intent i = new Intent(requireContext(), LoginPage.class);
        startActivity(i);
        requireActivity().finish();
    }

    private void navigateToAddWeight() {
        Intent i = new Intent(requireContext(), AddWeight.class);
        startActivity(i);
    }

    private void getStreakCount() {

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("meals")
                .child(FirebaseAuth.getInstance().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                streakCount = (int) snapshot.getChildrenCount();
                tvStreakCount.setText(String.valueOf(streakCount) + " Days Streak");
                Log.d("Count Streak: ", String.valueOf(streakCount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setWater() {


        ivMinusWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterCount--;
                progressBarWater.setProgress(waterCount);
                if (userId != null && !userId.isEmpty()) {

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                            .child("meals").child(userId).child(getCurrentDate());
                    db.child("waterCount").setValue(waterCount);
                    tvWaterCount.setText(String.valueOf(waterCount) + " Out of 9 Glasses");
                }
            }
        });

        ivPlusWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterCount++;
                progressBarWater.setProgress(waterCount);
                if (userId != null && !userId.isEmpty()) {

                    DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                            .child("meals").child(userId).child(getCurrentDate());
                    db.child("waterCount").setValue(waterCount);
                    tvWaterCount.setText(String.valueOf(waterCount) + " Out of 9 Glasses");

                }
            }
        });


    }

    private void getWaterCount() {
        DatabaseReference db = FirebaseDatabase.getInstance().getReference()
                .child("meals").child(userId).child(getCurrentDate());
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("waterCount")) {
                    waterCount = snapshot.child("waterCount").getValue(Integer.class);
                    progressBarWater.setProgress(waterCount);
                    tvWaterCount.setText(String.valueOf(waterCount) + " Out of 9 Glasses");

                } else {
                    db.child("waterCount").setValue(waterCount);
                    tvWaterCount.setText(String.valueOf(waterCount) + " Out of 9 Glasses");
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


}