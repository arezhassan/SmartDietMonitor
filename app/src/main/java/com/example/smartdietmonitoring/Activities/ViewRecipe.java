package com.example.smartdietmonitoring.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smartdietmonitoring.R;

public class ViewRecipe extends AppCompatActivity {

    TextView tvRecipeName;

    WebView webViewRecipe;

    ImageView ivBack;

    String recipeLink, recipeName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        initialize();
        listener();
        setUpWebView();

    }

    private void setUpWebView() {

        webViewRecipe.getSettings().setJavaScriptEnabled(true); // Enable JavaScript if needed
        webViewRecipe.loadUrl(recipeLink);


    }

    private void listener() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initialize() {
        Intent i = getIntent();
        recipeLink = i.getStringExtra("recipeLink");
        recipeName = i.getStringExtra("recipeName");

        tvRecipeName = findViewById(R.id.tvRecipeName);
        ivBack = findViewById(R.id.ivBack);
        webViewRecipe = findViewById(R.id.webviewRecipe);


        if (recipeName != null && !recipeName.isEmpty()) {
            tvRecipeName.setText(recipeName);
        }


    }
}