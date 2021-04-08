package com.cst2335.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This is the applicatio's home page. Here, the user is asked to enter a country and selected a
 * date, which will determine the results that show up on the next activity
 * @author Hamzeh
 */
public class CovidMain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Sharedprefs variables
    public static final String COVIDPREFS = "COVIDPREFS";
    public static final String COUNTRY = "Country";
    public static final String FROM_DATE = "FromDate";
    public static final String TO_DATE = "ToDate";

    SharedPreferences prefs = null;

    EditText countryEditTxt;
    EditText dateEditText;
    Button searchButton;
    String toDate = "";

    /**
     * This is where the activity isinitialized. Here, setContentView is called with a layout defining
     * The UI for the activity and findViewById is called on our widgets that we want to manipulate.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_main);

        prefs = getSharedPreferences(COVIDPREFS, Context.MODE_PRIVATE);
        String savedCountry = prefs.getString(COUNTRY, "");
        String savedDate = prefs.getString(FROM_DATE, "");
        toDate = prefs.getString(TO_DATE, "");

        countryEditTxt = findViewById(R.id.covidCountryEditText);
        countryEditTxt.setText(savedCountry);

        dateEditText = findViewById(R.id.covidDateEditText);
        dateEditText.setText(savedDate);

        searchButton = findViewById(R.id.covidSearchBtn);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.covidAppTitle));

        DrawerLayout drawer = findViewById(R.id.covid_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        DatePickerDialog.OnDateSetListener dPicker = (datePicker, y, m, d) -> {
            final Calendar calendar = Calendar.getInstance();
            Date now = calendar.getTime();

            calendar.set(Calendar.YEAR, y);
            calendar.set(Calendar.MONTH, m);
            calendar.set(Calendar.DAY_OF_MONTH, d);

            Date date = calendar.getTime();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            toDate = dateFormat.format(date);
            dateEditText.setText(toDate);

        };

        dateEditText.setFocusable(false);
        dateEditText.setKeyListener(null);

        dateEditText.setOnClickListener(click -> {
            final Calendar calendar = Calendar.getInstance();
            int d = calendar.get(Calendar.DAY_OF_MONTH);
            int m = calendar.get(Calendar.MONTH);
            int y = calendar.get(Calendar.YEAR);

            DatePickerDialog datePicker = new DatePickerDialog(this, dPicker, y, m, d);
            datePicker.show();

        });

        searchButton.setOnClickListener(click -> {
            String country = countryEditTxt.getText().toString().trim();
            String fromDate = dateEditText.getText().toString().trim();

            if (country.equalsIgnoreCase("")) {
                Snackbar.make(searchButton, "Country field cannot be empty!", Snackbar.LENGTH_LONG);

            } else if (fromDate.equalsIgnoreCase("")) {
                Snackbar.make(searchButton, "Date field cannot be empty!", Snackbar.LENGTH_LONG).show();

            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(COUNTRY, country)
                        .putString(FROM_DATE, fromDate)
                        .putString(TO_DATE, fromDate)
                        .commit();

                Intent search = new Intent(this, CovidSearchResult.class);
                search.putExtra(COUNTRY, country);
                search.putExtra(FROM_DATE, fromDate);
                search.putExtra(TO_DATE, toDate);
                startActivity(search);
            }
        });

    }

    /**
     * This method is called whenever the user selects a menu icon from the toolbar
     *
     * @param item , the item that the user selected
     * @return a boolean value
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.GoBackToHome):
                finish();
                break;
            case (R.id.covidHelp):
                AlertDialog.Builder adb = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.covidHelpTitle))
                        .setMessage(getResources().getString(R.string.covidHelpMain))
                        .setPositiveButton("OK", (click, arg) -> {
                        });
                adb.create().show();
                break;
        }
        return true;
    }

    /**
     * This method initializes the contents of the Activity's standard options menu. Our menu items
     * get placed into our inflated Menu.
     *
     * @param menu The options menu in which you place your items
     * @return a boolean value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.covid_home_menu, menu);
        return true;
    }

    /**
     * This method is called whenever the user selects a menu icon from the drawer
     *
     * @param item, the item that the user selected
     * @return a boolean value
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.GoBackToHome):
                finish();
                break;
            case (R.id.covidHelp):
                AlertDialog.Builder adb = new AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.covidHelpTitle))
                        .setMessage(getResources().getString(R.string.covidHelpMain))
                        .setPositiveButton("OK", (click, arg) -> {
                        });
                adb.create().show();
                break;
        }
        return true;
    }
}