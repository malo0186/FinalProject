package com.cst2335.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;


import com.cst2335.finalproject.recipeSearch.RecipeSaveFragment;
import com.cst2335.finalproject.recipeSearch.RecipeSearch;
import com.google.android.material.navigation.NavigationView;

//Cal Maciborka

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar mainToolbar;
    private AlertDialog.Builder alertBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("Home");
        mainToolbar.setTitleTextColor(Color.WHITE);
        mainToolbar.setBackgroundColor(Color.DKGRAY);
        mainToolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.overflow_white, null));

        DrawerLayout drawer = findViewById(R.id.main_page_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, mainToolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.main_page_nav_view);
        navView.setNavigationItemSelectedListener(this);

        alertBuilder = new AlertDialog.Builder(this);

        ImageButton btn_covid19 = (ImageButton)findViewById(R.id.btn_covid19);
        btn_covid19.setOnClickListener(bt -> {
            Intent nextPage = new Intent(MainActivity.this, CovidMain.class);
            startActivity(nextPage);
        });



        ImageButton btn_tm = (ImageButton)findViewById(R.id.btn_tm);
        btn_tm.setOnClickListener(bt -> {
            Intent nextPage = new Intent(MainActivity.this, TMActivity.class);
            startActivity(nextPage);
        });


        ImageButton recipeIcon = (ImageButton)findViewById(R.id.recipe_icon);
        recipeIcon.setOnClickListener(bt -> {
            Intent nextPage = new Intent(MainActivity.this, RecipeSearch.class);
            startActivity(nextPage);
        });


        ImageButton recipeIcon2 = (ImageButton)findViewById(R.id.another_recipe_icon);
        recipeIcon2.setOnClickListener(bt -> {
            Intent nextPage = new Intent(MainActivity.this, RecipeSearchActivity.class);
            startActivity(nextPage);
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_help_main) {
            alertBuilder.setMessage(R.string.choose_which_app_to_launch)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dialog_okay_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle(R.string.help);
            alert.show();
        }

        if (id == R.id.action_covid_toolbar_button) {

            Intent nextPage = new Intent(MainActivity.this, CovidMain.class);
            startActivity(nextPage);
        }

        if (id == R.id.action_tm_toolbar_button) {

        }

        if (id == R.id.action_recipe_toolbar_icon1) {

            Intent nextPage = new Intent(MainActivity.this, RecipeSearch.class);
            startActivity(nextPage);
        }

        if (id == R.id.action_recipe_toolbar_icon2) {
            Intent nextPage = new Intent(MainActivity.this, RecipeSearchActivity.class);
            startActivity(nextPage);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            alertBuilder.setMessage(R.string.choose_which_app_to_launch)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dialog_okay_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle(R.string.help);
            alert.show();
        }

        if (id == R.id.action_covid_toolbar_button) {

            Intent nextPage = new Intent(MainActivity.this, CovidMain.class);
            startActivity(nextPage);
        }

        if (id == R.id.action_tm_toolbar_button) {

        }

        if (id == R.id.action_recipe_toolbar_icon1) {

            Intent nextPage = new Intent(MainActivity.this, RecipeSearch.class);
            startActivity(nextPage);
        }

        if (id == R.id.action_recipe_toolbar_icon2) {

        }
        return super.onOptionsItemSelected(item);
    }
}