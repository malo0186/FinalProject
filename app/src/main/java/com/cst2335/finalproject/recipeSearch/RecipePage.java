package com.cst2335.finalproject.recipeSearch;

import com.cst2335.finalproject.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class RecipePage extends AppCompatActivity {

    private TextView titleView;
    private TextView ingredientsView;
    private TextView ingredientsList;
    private ImageButton recipeButton;
    private Toolbar recipeToolbar;
    private AlertDialog.Builder alertBuilder;
    boolean isASavedRecipe = false;
    String title;
    String href;
    String ingredients;
    String saveData;
    int position;
    HashMap<String, String> recipeMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);

        recipeToolbar = findViewById(R.id.recipe_toolbar);
        titleView = findViewById(R.id.recipe_title);
        ingredientsView = findViewById(R.id.ingredients_title);
        ingredientsList = findViewById(R.id.ingredients_list);
        recipeButton = findViewById(R.id.recipe_btn);
        alertBuilder = new AlertDialog.Builder(this);
        setSupportActionBar(recipeToolbar);
        getSupportActionBar().setTitle("Recipe Information");
        recipeToolbar.setTitleTextColor(Color.WHITE);
        recipeToolbar.setBackgroundColor(Color.DKGRAY);
        recipeToolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.overflow_white, null));

        recipeMap = new HashMap<>();

        Bundle bundle = this.getIntent().getExtras();

        if(bundle != null) {
            recipeMap = (HashMap<String, String>) bundle.getSerializable("recipes");
            position = bundle.getInt("position");
        }

        if (position >= 0) {

            title = (String) recipeMap.get(position+"title");
            href = (String) recipeMap.get(position+"href");
            ingredients = (String) recipeMap.get(position+"ingredients");

            ingredients = "\u2022  " + ingredients.replaceAll(",", "\n\u2022 ");
        }
        else {

            title = (String) recipeMap.get("title");
            href = (String) recipeMap.get("href");
            ingredients = (String) recipeMap.get("ingredients");

            isASavedRecipe = true;
        }

        ingredientsView.setText(R.string.ingredients);
        titleView.setText(title);
        ingredientsList.setText(ingredients);

        recipeButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
                startActivity(browserIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isASavedRecipe) {
            getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.recipe_page_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_favorite) {

            SharedPreferences sharedPref = this.getSharedPreferences("Saved",Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            saveData = href + "*" + ingredients;
            editor.putString(title, saveData);
            editor.apply();

            Snackbar saveSnack = Snackbar.make(findViewById(R.id.action_favorite), "Recipe Saved", Snackbar.LENGTH_LONG)
                    .setAction("Undo", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            editor.remove(title);
                        }
                    });

                    saveSnack.show();

            return true;
        }

        if (id == R.id.action_saves) {
            RecipeSaveFragment saveFragment = new RecipeSaveFragment();
            saveFragment.show(getSupportFragmentManager(), "savedRecipes");
            return true;
        }

        if (id == R.id.action_help) {
            alertBuilder.setMessage("Click on the 'Go to Recipe' button for more information on this recipe.\n\nClick the floppy disk icon in the top right to save this recipe for later.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle("Help");
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}