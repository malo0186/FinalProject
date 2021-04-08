package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;


/**
 * The purpose of this class is to show the fragment when an object in our ListView is clicked on.
 * The fragment shows the location's details
 * @author Hamzeh Kaddoura
 */
public class CovidEmptyActivity extends AppCompatActivity {

    /**
     * This is where the activity is initialized. Here, setContentView is called with a layout defining
     * the UI for the activity and findViewById is called on our widgets that we want to manipulate
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_empty);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.covidLocationDetails));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle dataToPass = getIntent().getExtras();

        CovidDetailsFragment df = new CovidDetailsFragment();
        df.setArguments(dataToPass);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.covidFragmentLocation, df)
                .commit();
    }
}