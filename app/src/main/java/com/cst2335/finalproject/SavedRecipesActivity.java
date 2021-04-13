package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SavedRecipesActivity extends AppCompatActivity {

    private static final String TABLE_NAME = "RecipeTable";
    private static SQLiteDatabase db = null;
    private static RecipeDatabaseHelper dbHelper = null;
    private Button backBtn;
    private ImageButton helpBtn;

    ListView savedRecipesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_recipes);

        savedRecipesList = findViewById(R.id.savedRecipesList);
        backBtn = findViewById(R.id.savedRecipesBackToSearchButton);
        helpBtn = findViewById(R.id.savedRecipesHelpButton);

        backBtn.setOnClickListener(view -> {
            Intent goBackToSearch = new Intent(SavedRecipesActivity.this, RecipeSearchActivity.class);
            startActivity(goBackToSearch);
        });

        dbHelper = new RecipeDatabaseHelper(this);
        Cursor curs = dbHelper.getCursor(TABLE_NAME);
        curs.moveToFirst();
        ArrayList<RecipeItem> recipeItemList = new ArrayList<>();

        while (!curs.isAfterLast()) {
            RecipeItem singleRecipe = new RecipeItem(curs.getString(curs.getColumnIndex("title")),
                    curs.getString(curs.getColumnIndex("ingredients")),
                    curs.getString(curs.getColumnIndex("source_url")));
            recipeItemList.add(singleRecipe);
            curs.moveToNext();
        }
        RecipeAdapter adapter = new RecipeAdapter(this, recipeItemList);
        savedRecipesList.setAdapter(adapter);

        savedRecipesList.setOnItemClickListener((adapterView, view, position, id) -> {
            RecipeItem item = (RecipeItem) adapterView.getItemAtPosition(position);
            Intent goToRecipeDetails = new Intent(SavedRecipesActivity.this, RecipeDetailsActivity.class);
            goToRecipeDetails.putExtra("recipeItem", item);
            startActivity(goToRecipeDetails);
        });

        savedRecipesList.setOnItemLongClickListener((adapterView, view, position, id) -> {
            new AlertDialog.Builder(SavedRecipesActivity.this)
                    .setTitle("Recipe Deleter")
                    .setMessage("Do you want to delete this recipe?")
                    .setPositiveButton(("yes"), (dialog, which) -> {
                        RecipeDatabaseHelper.deleteItem(recipeItemList.get(position).getTitle(), TABLE_NAME);
                        recipeItemList.remove(position);
                        adapter.notifyDataSetChanged();
                        //make a snackbar
                        Snackbar snackbar = Snackbar
                                .make(view, "Recipe Deleted!",
                                        Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true;
        });
        helpBtn.setOnClickListener(view -> new AlertDialog.Builder(SavedRecipesActivity.this)
                .setTitle(getText(R.string.activityHelpTitle))
                .setMessage(getText(R.string.savedRecipesHelpMessage))
                .setNeutralButton(getText(R.string.alertOK), null)
                .show());


    }
}