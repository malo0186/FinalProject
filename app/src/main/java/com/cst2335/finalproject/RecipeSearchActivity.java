package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

//Cal Maciborka

public class RecipeSearchActivity extends AppCompatActivity {
    private static final String SHARED_PREF = "SharedPref";
    private static final String TEXT = "text";

    private EditText searchRecipeEditText;
    private Button searchForRecipeBtn, savedRecipesBtn;
    private ImageButton helpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        searchRecipeEditText = findViewById(R.id.searchRecipeEditText);
        searchForRecipeBtn = findViewById(R.id.searchRecipeButton);
        savedRecipesBtn = findViewById(R.id.savedRecipesButton);
        helpBtn = findViewById(R.id.mainHelpButton);

        searchForRecipeBtn.setOnClickListener(view -> {
            String text = searchRecipeEditText.getText().toString();
            if (TextUtils.isEmpty(text)) {
                Toast.makeText(RecipeSearchActivity.this, getText(R.string.enterTextToast), Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(RecipeSearchActivity.this, RecipeListActivity.class);
                intent.putExtra("recipeText", searchRecipeEditText.getText().toString());
                startActivity(intent);
            }
        });
        savedRecipesBtn.setOnClickListener(view -> {
            Intent intent = new Intent(RecipeSearchActivity.this, SavedRecipesActivity.class);
            startActivity(intent);
        });
        helpBtn.setOnClickListener(view -> new AlertDialog.Builder(RecipeSearchActivity.this)
                .setTitle(getText(R.string.activityHelpTitle))
                .setMessage(getText(R.string.mainActivityHelpMessage))
                .setNeutralButton(getText(R.string.alertOK), null)
                .show());
        loadSearchText();

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveSearchText();

    }

    public void saveSearchText() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TEXT, searchRecipeEditText.getText().toString());
        editor.apply();
    }

    public void loadSearchText() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);

        String text = sharedPreferences.getString(TEXT, "");
        searchRecipeEditText.setText(text);
    }
}