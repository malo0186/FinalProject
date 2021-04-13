package com.cst2335.finalproject.recipeSearch;

import com.cst2335.finalproject.MainActivity;
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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * The recipe information page.
 * @author Cal Malouin
 */
public class RecipePage extends AppCompatActivity {

    private TextView titleView;
    private TextView ingredientsView;
    private TextView ingredientsList;
    private ImageButton recipeButton;
    private Toolbar recipeToolbar;
    private AlertDialog.Builder alertBuilder;
    boolean isASavedRecipe = false;
    // the data passed from the search page will be extracted into separate strings
    String title;
    String href;
    String ingredients;
    String saveData;
    int position;
    // the shared preferences key for the saved recipes
    protected static final String SAVED_RECIPES_KEY = "saved";
    HashMap<String, String> recipeMap = new HashMap<>();
    RecipeDB recipeDB;

    /**
     *
     * The information for the recipe selected by the user is passed here as a Bundle.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);

        // set up the toolbar for this activity
        recipeToolbar = findViewById(R.id.recipe_toolbar);
        titleView = findViewById(R.id.recipe_title);
        ingredientsView = findViewById(R.id.ingredients_title);
        ingredientsList = findViewById(R.id.ingredients_list);
        recipeButton = findViewById(R.id.recipe_btn);
        alertBuilder = new AlertDialog.Builder(this);
        setSupportActionBar(recipeToolbar);
        getSupportActionBar().setTitle(R.string.recipe_page_toolbar_title);
        getSupportActionBar().setSubtitle(R.string.recipe_search_toolbar_subtitle);
        // display the home menu button on the left side
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.recipe_back_to_main_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recipeToolbar.setTitleTextColor(Color.WHITE);
        recipeToolbar.setSubtitleTextColor(Color.WHITE);
        recipeToolbar.setBackgroundColor(Color.DKGRAY);
        recipeToolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.overflow_white, null));

        recipeMap = new HashMap<>();

        Bundle bundle = this.getIntent().getExtras();

        if(bundle != null) {
            recipeMap = (HashMap<String, String>) bundle.getSerializable(RecipeSearch.RECIPES_KEY);
            position = bundle.getInt(RecipeSearch.POSITION_KEY);
        }

        // the position is set to -1 if it is a previously saved recipe being shown
        if (position >= 0) {

            title = (String) recipeMap.get(position+RecipeSearch.TITLE);
            href = (String) recipeMap.get(position+RecipeSearch.HREF);
            ingredients = (String) recipeMap.get(position+RecipeSearch.INGREDIENTS);

            // an un-saved recipe needs to have the ingredients formatted
            ingredients = "\u2022  " + ingredients.replaceAll(",", "\n\u2022 ");
        }
        else {

            title = (String) recipeMap.get(RecipeSearch.TITLE);
            href = (String) recipeMap.get(RecipeSearch.HREF);
            ingredients = (String) recipeMap.get(RecipeSearch.INGREDIENTS);

            // this flag is set, so the save button won't be shown
            isASavedRecipe = true;
        }

        ingredientsView.setText(R.string.ingredients);
        titleView.setText(title);
        ingredientsList.setText(ingredients);

        // this will take the user to the web page of the recipe (opens browser)
        recipeButton.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(href));
                startActivity(browserIntent);
            }
        });
    }

    /**
     * The menu options in the toolbar do not include the save button if the recipe is already saved.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isASavedRecipe) {
            getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.recipe_page_menu, menu);
        }
        return true;
    }

    /**
     * A listener for the different menu options in the toolbar.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        // the save button is clicked
        if (id == R.id.action_favorite) {

            // store the current recipe data in shared preferences
            SharedPreferences sharedPref = this.getSharedPreferences(SAVED_RECIPES_KEY,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            saveData = href + "*" + ingredients;
            editor.putString(title, saveData);
            editor.apply();

            // Snackbar message that gives the user the option to undo the save
            Snackbar saveSnack = Snackbar.make(findViewById(R.id.action_favorite), R.string.snackbar_dialog_save_text, Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_dialog_undo_button, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            editor.remove(title);
                        }
                    });

                    saveSnack.show();

            return true;
        }

        // brings the user to the saved recipes fragment
        if (id == R.id.action_saves) {
            RecipeSaveFragment saveFragment = new RecipeSaveFragment();
            saveFragment.show(getSupportFragmentManager(), "savedRecipes");
            return true;
        }

        // calls the method to import saved recipes from the DB
        if (id == R.id.recipe_db_import) {

            importFromDB();

            return true;
        }

        // calls the method to export saved recipes to the DB
        if (id == R.id.recipe_db_export) {

            exportToDB();

            return true;
        }

        // alert dialog with information on how to use the recipe page
        if (id == R.id.action_help) {
            alertBuilder.setMessage(R.string.alert_dialog_recipe_page_help_text)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dialog_okay_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle(R.string.help);
            alert.show();
        }

        // brings the user back to the search page
        if (id == R.id.action_recipe_search_homepage) {
            Intent toRecipeSearch = new Intent(RecipePage.this, RecipeSearch.class);
            startActivity(toRecipeSearch);
        }

        // bring the user back to the home page to switch between functions in the application
        if (id == android.R.id.home) {
            Intent toMainMenu = new Intent(RecipePage.this, MainActivity.class);
            startActivity(toMainMenu);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Imports any saved recipes from the database, and adds them to the saved recipe ListView
     */
    private void importFromDB() {

        Toast.makeText(RecipePage.this, R.string.toast_dialog_import_from_db, Toast.LENGTH_LONG).show();

        SharedPreferences sharedPref = this.getSharedPreferences(RecipePage.SAVED_RECIPES_KEY,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        recipeDB = new RecipeDB(this);

        // store database data ina HashMap
        HashMap<String, String> dbData = recipeDB.getAllRecipes();

        // loop through HashMap, storing recipe information in SharedPreferences
        for (Map.Entry<String, String> entry : dbData.entrySet()) {

            editor.putString(entry.getKey(), entry.getValue());
            editor.apply();
        }
    }

    /**
     * Export any saved recipes to the database, removing them from the saved recipe ListView
     */
    private void exportToDB() {

        Toast.makeText(RecipePage.this, R.string.toast_dialog_export_to_db, Toast.LENGTH_LONG).show();

        SharedPreferences sharedPref = getSharedPreferences(RecipePage.SAVED_RECIPES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        HashMap<String, String> saveMap = (HashMap<String, String>) sharedPref.getAll();

        // add saved recipes from SharedPreferences to the database
        recipeDB = new RecipeDB(this);
        recipeDB.addRecipes(saveMap);

        // clear all recipes from SharedPreferences
        editor.clear();
        editor.commit();
    }
}