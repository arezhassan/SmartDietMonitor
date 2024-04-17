package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.chart.common.listener.ListenersInterface;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
import com.example.smartdietmonitoring.R;

import java.util.ArrayList;
import java.util.List;

public class ViewCalorieBreakdown extends AppCompatActivity {

    AnyChartView anyChartView;
    TextView tvCalories, tvFoodType;

    ImageView ivBack;

    String foodType;

    int calories;

    CardView addFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_calorie_breakdown);
        initializeViews();
        setChart();
        listener();
        setData();
    }

    private void setChart() {
        Pie pie = AnyChart.pie();


        pie.setOnClickListener(new ListenersInterface.OnClickListener(new String[]{"x", "value"}) {
            @Override
            public void onClick(com.anychart.chart.common.listener.Event event) {
                Toast.makeText(ViewCalorieBreakdown.this, event.getData().get("x") + ":" + event.getData().get("value"), Toast.LENGTH_SHORT).show();

            }

        });
        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Proteins", 10));
        data.add(new ValueDataEntry("Fibre", 30));
        data.add(new ValueDataEntry("Fat", 24));

        pie.data(data);

        pie.title("Macro-Nutrients");

        pie.labels().position("outside");



        pie.legend()
                .position("center-bottom")
                .itemsLayout(LegendLayout.HORIZONTAL)
                .align(Align.CENTER);

        anyChartView.setChart(pie);


}


    private void initializeViews() {
        anyChartView = findViewById(R.id.caloriePieChart);
        ivBack = findViewById(R.id.ivBack);
        tvCalories = findViewById(R.id.tvCalories);
        tvFoodType = findViewById(R.id.tvFoodType);
        addFood=findViewById(R.id.addFood);
        Intent i = getIntent();
        foodType = i.getStringExtra("foodType");
        calories=i.getIntExtra("foodCals",210) ;
        tvFoodType.setText(foodType + " Calorie breakdown");


    }

    private void setData() {
        tvCalories.setText(String.valueOf(calories) + " Kcals");
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
                Intent i= new Intent(ViewCalorieBreakdown.this, AddFoodItem.class);
                i.putExtra("foodType",foodType);
                i.putExtra("mealType",foodType);
                startActivity(i);
            }
        });
    }



}