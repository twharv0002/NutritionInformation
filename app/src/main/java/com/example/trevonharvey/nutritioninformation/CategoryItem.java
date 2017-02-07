package com.example.trevonharvey.nutritioninformation;

/**
 * Created by Trevon Harvey on 4/19/2016.
 */
public class CategoryItem {
    private String ndbno;
    private String categoryName;
    private String name;

    public CategoryItem(String ndbno, String categoryName, String name){
        this.ndbno = ndbno;
        this.categoryName = categoryName;
        this.name = name;
    }

    public String getNdbno(){
        return ndbno;
    }

    public String getName(){
        return name;
    }

    public String getCategoryName(){
        return categoryName;
    }

    public String toString(){
        return name + "\n  Category: " + categoryName;
    }
}
