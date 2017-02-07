package com.example.trevonharvey.nutritioninformation;

import java.text.NumberFormat;

/**
 * Created by Trevon Harvey on 4/18/2016.
 */
public class Food {
    private String name;
    private String carb;
    private String protein;
    private String fat;
    private String sugar;
    private String sodium;
    private String fiber;
    private String potassium;
    private String calories;

    public Food(String name, double carb, double protein, double fat, double sugar, double sodium, double fiber, double potassium, double calories){

        NumberFormat numberFormat = NumberFormat.getInstance();
        this.name = name;
        this.carb = String.valueOf(carb);
        this.protein = String.valueOf(protein);
        this.fat = String.valueOf(fat);
        this.sugar = String.valueOf(sugar);
        this.sodium = String.valueOf(sodium);
        this.fiber = String.valueOf(fiber);
        this.potassium = String.valueOf(potassium);
        this.calories = String.valueOf(calories);
    }

    public String getName(){
        return name;
    }

    public String getCarb(){
        return carb;
    }

    public String getProtein(){
        return protein;
    }

    public String getFat(){
        return fat;
    }

    public String getSugar(){
        return sugar;
    }

    public String getSodium(){
        return sodium;
    }

    public String getFiber(){
        return fiber;
    }

    public String getPotassium(){
        return potassium;
    }

    public String getCalories(){
        return calories;
    }
}