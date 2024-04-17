package com.example.smartdietmonitoring.Activities;

import static com.example.smartdietmonitoring.Utilities.DateUtils.convertToFirebaseKey;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

public class ViewWeeklyProgress extends AppCompatActivity {



    int day1, day2, day3, day4, day5, day6, day7, avg, count;
    TextView tvAverageCalories;
    AnyChartView anyChartView;
    Cartesian cartesian;
    Column column;
    List<DataEntry> data;

    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_weekly_progress);
        init();
        setupChart();


    }
    private void init(){
        tvAverageCalories = findViewById(R.id.tvAverageCalories);
        ivBack=findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                onBackPressed();

            }
        });


    }
    public void onBackPressed(){
        super.onBackPressed();
        finish();
        Intent i=new Intent(this, MainActivity.class);
        startActivity(i);

    }
    private String getMonthName(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July",
                "August", "September", "October", "November", "December"};
        return monthNames[month - 1];
    }
    private void setupChart() {

        data = new ArrayList<>();
        cartesian = AnyChart.column();
        anyChartView = findViewById(R.id.any_chart_view);


        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
        int day = calendar.get(Calendar.DAY_OF_MONTH);

// Create a string representation of the date
        String currentDate = day + " " + getMonthName(month) + " " + year;
        String keyDate = convertToFirebaseKey(currentDate);

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("meals")
                .child(FirebaseAuth.getInstance().getUid());

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = (int) snapshot.getChildrenCount();

                DatabaseReference dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                        .child(FirebaseAuth.getInstance().getUid()).child(keyDate);
                if (count == 0) {
                    day1 = day2 = day3 = day4 = day5 = day6 = day7 = 0;
                    avg = 0;
                    tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                    data.clear();
                    data.add(new ValueDataEntry("Today", day1));
                    column = cartesian.column(data);
                    column.tooltip()
                            .titleFormat("{%X}")
                            .position(Position.CENTER_BOTTOM)
                            .anchor(Anchor.CENTER_BOTTOM)
                            .offsetX(0d)
                            .offsetY(5d)
                            .format("{%Value}{groupsSeparator: }");

                    cartesian.animation(true);
                    cartesian.title("Weekly Calorie Count");

                    cartesian.yScale().minimum(0d);

                    cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                    cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                    cartesian.interactivity().hoverMode(HoverMode.BY_X);

                    cartesian.xAxis(0).title("Days");
                    cartesian.yAxis(0).title("Calories");


                } else if (count == 1) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            data.add(new ValueDataEntry("Today", day1));
                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");
                            avg = day1;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (count == 2) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);

                            avg = avg + day2;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                            data.add(new ValueDataEntry("Day 1", day2));
                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (count == 3) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day1;
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);
                            data.add(new ValueDataEntry("Day 2", day2));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar1 = Calendar.getInstance();
                    int newyear1 = calendar.get(Calendar.YEAR);
                    int newmonth1 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday1 = calendar.get(Calendar.DAY_OF_MONTH) - 2;

// Create a string representation of the date
                    String newDate1 = newday1 + " " + getMonthName(newmonth1) + " " + newyear1;
                    String newKeyDate1 = convertToFirebaseKey(newDate1);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate1);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day3 = snapshot.child("totalCalories").getValue(Integer.class);
                            data.add(new ValueDataEntry("Day 1", day3));
                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");
                            avg = avg + day3;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else if (count == 4) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day1;
                            data.clear();
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day2 + avg;
                            data.add(new ValueDataEntry("Day 3", day2));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar1 = Calendar.getInstance();
                    int newyear1 = calendar.get(Calendar.YEAR);
                    int newmonth1 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday1 = calendar.get(Calendar.DAY_OF_MONTH) - 2;

// Create a string representation of the date
                    String newDate1 = newday1 + " " + getMonthName(newmonth1) + " " + newyear1;
                    String newKeyDate1 = convertToFirebaseKey(newDate1);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate1);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day3 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day3 + avg;
                            data.add(new ValueDataEntry("Day 2", day3));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar2 = Calendar.getInstance();
                    int newyear2 = calendar.get(Calendar.YEAR);
                    int newmonth2 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday2 = calendar.get(Calendar.DAY_OF_MONTH) - 3;

// Create a string representation of the date
                    String newDate2 = newday2 + " " + getMonthName(newmonth2) + " " + newyear2;
                    String newKeyDate2 = convertToFirebaseKey(newDate2);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate2);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day4 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day4 + avg;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                            data.add(new ValueDataEntry("Day 1", day4));

                            column = cartesian.column(data);
                            cartesian = AnyChart.column();
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (count == 5) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day1;
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day2 + avg;
                            data.add(new ValueDataEntry("Day 4", day2));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar1 = Calendar.getInstance();
                    int newyear1 = calendar.get(Calendar.YEAR);
                    int newmonth1 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday1 = calendar.get(Calendar.DAY_OF_MONTH) - 2;

// Create a string representation of the date
                    String newDate1 = newday1 + " " + getMonthName(newmonth1) + " " + newyear1;
                    String newKeyDate1 = convertToFirebaseKey(newDate1);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate1);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day3 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day3 + avg;
                            data.add(new ValueDataEntry("Day 3", day3));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar2 = Calendar.getInstance();
                    int newyear2 = calendar.get(Calendar.YEAR);
                    int newmonth2 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday2 = calendar.get(Calendar.DAY_OF_MONTH) - 3;

// Create a string representation of the date
                    String newDate2 = newday2 + " " + getMonthName(newmonth2) + " " + newyear2;
                    String newKeyDate2 = convertToFirebaseKey(newDate2);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate2);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day4 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day4 + avg;
                            data.add(new ValueDataEntry("Day 2", day4));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar3 = Calendar.getInstance();
                    int newyear3 = calendar.get(Calendar.YEAR);
                    int newmonth3 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday3 = calendar.get(Calendar.DAY_OF_MONTH) - 4;

// Create a string representation of the date
                    String newDate3 = newday3 + " " + getMonthName(newmonth3) + " " + newyear3;
                    String newKeyDate3 = convertToFirebaseKey(newDate3);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day5 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day5 + avg;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                            data.add(new ValueDataEntry("Day 1", day5));
                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (count == 6) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day1;
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day2 + avg;
                            data.add(new ValueDataEntry("Day 5", day2));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar1 = Calendar.getInstance();
                    int newyear1 = calendar.get(Calendar.YEAR);
                    int newmonth1 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday1 = calendar.get(Calendar.DAY_OF_MONTH) - 2;

// Create a string representation of the date
                    String newDate1 = newday1 + " " + getMonthName(newmonth1) + " " + newyear1;
                    String newKeyDate1 = convertToFirebaseKey(newDate1);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate1);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day3 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day3 + avg;
                            data.add(new ValueDataEntry("Day 4", day3));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar2 = Calendar.getInstance();
                    int newyear2 = calendar.get(Calendar.YEAR);
                    int newmonth2 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday2 = calendar.get(Calendar.DAY_OF_MONTH) - 3;

// Create a string representation of the date
                    String newDate2 = newday2 + " " + getMonthName(newmonth2) + " " + newyear2;
                    String newKeyDate2 = convertToFirebaseKey(newDate2);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate2);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day4 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day4 + avg;
                            data.add(new ValueDataEntry("Day 3", day4));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar3 = Calendar.getInstance();
                    int newyear3 = calendar.get(Calendar.YEAR);
                    int newmonth3 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday3 = calendar.get(Calendar.DAY_OF_MONTH) - 4;

// Create a string representation of the date
                    String newDate3 = newday3 + " " + getMonthName(newmonth3) + " " + newyear3;
                    String newKeyDate3 = convertToFirebaseKey(newDate3);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day5 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day5 + avg;
                            data.add(new ValueDataEntry("Day 2", day5));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar4 = Calendar.getInstance();
                    int newyear4 = calendar.get(Calendar.YEAR);
                    int newmonth4 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday4 = calendar.get(Calendar.DAY_OF_MONTH) - 5;

// Create a string representation of the date
                    String newDate4 = newday4 + " " + getMonthName(newmonth4) + " " + newyear4;
                    String newKeyDate4 = convertToFirebaseKey(newDate4);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day6 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day6 + avg;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                            data.add(new ValueDataEntry("Day 1", day6));

                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else if (count == 7) {
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day1 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day1;
                            data.add(new ValueDataEntry("Today", day1));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Calendar newcalendar = Calendar.getInstance();
                    int newyear = calendar.get(Calendar.YEAR);
                    int newmonth = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday = calendar.get(Calendar.DAY_OF_MONTH) - 1;

// Create a string representation of the date
                    String newDate = newday + " " + getMonthName(newmonth) + " " + newyear;
                    String newKeyDate = convertToFirebaseKey(newDate);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day2 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day2 + avg;
                            data.add(new ValueDataEntry("Day 6", day2));

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar1 = Calendar.getInstance();
                    int newyear1 = calendar.get(Calendar.YEAR);
                    int newmonth1 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday1 = calendar.get(Calendar.DAY_OF_MONTH) - 2;

// Create a string representation of the date
                    String newDate1 = newday1 + " " + getMonthName(newmonth1) + " " + newyear1;
                    String newKeyDate1 = convertToFirebaseKey(newDate1);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate1);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day3 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day3 + avg;
                            data.add(new ValueDataEntry("Day 5", day3));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar2 = Calendar.getInstance();
                    int newyear2 = calendar.get(Calendar.YEAR);
                    int newmonth2 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday2 = calendar.get(Calendar.DAY_OF_MONTH) - 3;

// Create a string representation of the date
                    String newDate2 = newday2 + " " + getMonthName(newmonth2) + " " + newyear2;
                    String newKeyDate2 = convertToFirebaseKey(newDate2);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate2);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day4 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day4 + avg;
                            data.add(new ValueDataEntry("Day 4", day4));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar3 = Calendar.getInstance();
                    int newyear3 = calendar.get(Calendar.YEAR);
                    int newmonth3 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday3 = calendar.get(Calendar.DAY_OF_MONTH) - 4;

// Create a string representation of the date
                    String newDate3 = newday3 + " " + getMonthName(newmonth3) + " " + newyear3;
                    String newKeyDate3 = convertToFirebaseKey(newDate3);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day5 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day5 + avg;
                            data.add(new ValueDataEntry("Day 3", day5));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar4 = Calendar.getInstance();
                    int newyear4 = calendar.get(Calendar.YEAR);
                    int newmonth4 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday4 = calendar.get(Calendar.DAY_OF_MONTH) - 5;

// Create a string representation of the date
                    String newDate4 = newday4 + " " + getMonthName(newmonth4) + " " + newyear4;
                    String newKeyDate4 = convertToFirebaseKey(newDate4);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day6 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day6 + avg;
                            data.add(new ValueDataEntry("Day 2", day6));


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    Calendar newcalendar5 = Calendar.getInstance();
                    int newyear5 = calendar.get(Calendar.YEAR);
                    int newmonth5 = calendar.get(Calendar.MONTH) + 1; // Month is zero-based, so add 1
                    int newday5 = calendar.get(Calendar.DAY_OF_MONTH) - 6;

// Create a string representation of the date
                    String newDate5 = newday5 + " " + getMonthName(newmonth5) + " " + newyear5;
                    String newKeyDate5 = convertToFirebaseKey(newDate4);

                    dbnew = FirebaseDatabase.getInstance().getReference().child("meals")
                            .child(FirebaseAuth.getInstance().getUid()).child(newKeyDate3);
                    dbnew.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists() && snapshot.hasChild("totalCalories"))
                                day7 = snapshot.child("totalCalories").getValue(Integer.class);
                            avg = day7 + avg;
                            avg = avg / count;
                            tvAverageCalories.setText(String.valueOf(avg) + " Cals");
                            data.add(new ValueDataEntry("Day 1", day7));
                            column = cartesian.column(data);
                            column.tooltip()
                                    .titleFormat("{%X}")
                                    .position(Position.CENTER_BOTTOM)
                                    .anchor(Anchor.CENTER_BOTTOM)
                                    .offsetX(0d)
                                    .offsetY(5d)
                                    .format("{%Value}{groupsSeparator: }");

                            cartesian.animation(true);
                            cartesian.title("Weekly Calorie Count");

                            cartesian.yScale().minimum(0d);

                            cartesian.yAxis(0).labels().format("{%Value}{groupsSeparator: }");

                            cartesian.tooltip().positionMode(TooltipPositionMode.POINT);
                            cartesian.interactivity().hoverMode(HoverMode.BY_X);

                            cartesian.xAxis(0).title("Days");
                            cartesian.yAxis(0).title("Calories");

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                Log.e("COUNT", String.valueOf(count));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        anyChartView.setChart(cartesian);
    }
}