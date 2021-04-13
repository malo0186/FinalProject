package com.cst2335.finalproject;

import java.io.Serializable;

public class RecipeItem implements Serializable {
    String title;
    String ingredients;
    String url;

    public RecipeItem(String title, String ingredients, String url) {
        this.title = title;
        this.ingredients = ingredients;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getUrl() {
        return url;
    }

}
