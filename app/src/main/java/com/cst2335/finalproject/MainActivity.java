package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cst2335.finalproject.recipeSearch.RecipeSearch;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.buttonSearch);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent goToProfile = new Intent(MainActivity.this, RecipeSearch.class);
                startActivity(goToProfile);
            }
        });
    }
}