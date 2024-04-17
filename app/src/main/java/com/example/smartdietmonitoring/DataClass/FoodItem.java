package com.example.smartdietmonitoring.DataClass;

import java.io.Serializable;

public class FoodItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String foodName;
    private String servingSize;

    private String foodKey;
    private int calories;

    private String recipeLink;

    public FoodItem() {

    }

    public FoodItem(String foodName, String servingSize, int calories) {
        this.foodName = foodName;
        this.servingSize = servingSize;
        this.calories = calories;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String name) {
        this.foodName = name;
    }

    public String getServingSize() {
        return servingSize;
    }

    public void setServingSize(String servingSize) {
        this.servingSize = servingSize;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public String getFoodKey() {
        return foodKey;
    }

    public String getRecipeLink() {
        return recipeLink;
    }

    public void setRecipeLink(String recipeLink) {
        this.recipeLink = recipeLink;
    }

    public void setFoodKey(String foodKey) {
        this.foodKey = foodKey;
    }
}
