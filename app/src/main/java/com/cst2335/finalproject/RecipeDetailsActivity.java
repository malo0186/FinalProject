package com.cst2335.finalproject;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @author Cal Maciborka
 */
public class RecipeDetailsActivity extends AppCompatActivity {
    private TextView recipeDetailsTitle, recipeDetailsIngredients, recipeDetailsURL;
    private Button saveRecipeBtn, backToSearchBtn;
    private ImageButton helpBtn;
    private static final String TABLE_NAME = "RecipeTable";
    private static SQLiteDatabase db = null;
    private static RecipeDatabaseHelper dbHelper = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        recipeDetailsTitle = findViewById(R.id.recipeDetailsTitle);
        recipeDetailsIngredients = findViewById(R.id.recipeDetailsIngredients);
        recipeDetailsURL = findViewById(R.id.recipeDetailsURL);

        saveRecipeBtn = findViewById(R.id.saveRecipeButton);
        backToSearchBtn = findViewById(R.id.detailsBackToListButton);
        helpBtn = findViewById(R.id.recipeDetailsHelpButton);

        Intent goToRecipeDetails = getIntent();
        RecipeItem singleRecipe = (RecipeItem) goToRecipeDetails.getSerializableExtra("recipeItem");

        recipeDetailsTitle.setText(singleRecipe.getTitle());
        recipeDetailsIngredients.setText(singleRecipe.getIngredients());
        recipeDetailsURL.setText(singleRecipe.getUrl());

        recipeDetailsURL.setOnClickListener(view -> {
            Toast.makeText(RecipeDetailsActivity.this, getText(R.string.launchURLToast), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(recipeDetailsURL.getText().toString())));

        });

        saveRecipeBtn.setOnClickListener(view -> {
            dbHelper = new RecipeDatabaseHelper(this);
            db = dbHelper.getWritableDatabase();
            dbHelper.onCreate(db);
            if (!RecipeDatabaseHelper.doesRecordExist(TABLE_NAME, "title", singleRecipe.getTitle())) {
                RecipeDatabaseHelper.insertItem(TABLE_NAME, singleRecipe.getTitle(), singleRecipe.getIngredients(), singleRecipe.getUrl());
                Toast.makeText(RecipeDetailsActivity.this, getText(R.string.recipeSavedToast), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(RecipeDetailsActivity.this, getText(R.string.recipeAlreadySavedToast), Toast.LENGTH_SHORT).show();
            }
        });

        backToSearchBtn.setOnClickListener(view -> {
            Intent goBackToSearch = new Intent(RecipeDetailsActivity.this, RecipeSearchActivity.class);
            startActivity(goBackToSearch);
        });
        helpBtn.setOnClickListener(view -> new AlertDialog.Builder(RecipeDetailsActivity.this)
                .setTitle(getText(R.string.activityHelpTitle))
                .setMessage(getText(R.string.recipeDetailsHelpMessage))
                .setNeutralButton(getText(R.string.alertOK), null)
                .show());

    }
}