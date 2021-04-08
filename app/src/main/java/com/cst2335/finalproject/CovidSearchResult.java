package com.cst2335.finalproject;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

/**
 * The purpose of this class is to show the list of provinces and case #s based on what the user
 * enter in the previous activity
 */
public class CovidSearchResult extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int counter = 0;
    CovidResultsQuery query = null;
    String result = null;
    String savedCountry;
    String savedDate;
    ProgressBar progressBar;
    MyListAdapter adt;
    ListView myList;
    CovidDetailsFragment df;
    boolean isTablet;

    private ArrayList<CovidMessage> data = new ArrayList<>();
    private ArrayList<CovidMessage> unsortedData = new ArrayList<>();
    private HashMap<Integer, String> dataDetails = new HashMap<>();
    private Bundle dataFromActivity;

    final static String ITEM_DETAILS = "DETAILS";
    private static final String CREATE_TABLE = ("CREATE TABLE IF NOT EXISTS covid_t(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT, COUNTRY VARCHAR(50), DATE VARCHAR(50), " +
            "PROVINCE VARCHAR(50), CASES INT, SAVE_ID VARHCAR(50));");

    /**
     * This is where the activity is initalized. Here, setContentView is called with a layout defining
     * the UI for the activity and findViewById is called on our widgets that we want to manipulate.
     *
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_search_result);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(getResources().getString(R.string.covidSearchResults));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = findViewById(R.id.covid_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(this);

        isTablet = findViewById(R.id.covidFragmentLocation) != null;

        SwitchCompat sw = findViewById(R.id.sortSwitch);
        sw.setOnCheckedChangeListener((cb, checked) -> {
            if (!checked) {
                data.clear();

                for (int i = 0; i < unsortedData.size(); i++) {
                    data.add(unsortedData.get(i));
                }

                adt.notifyDataSetChanged();
                Snackbar.make(sw, getResources().getString(R.string.covidSort), BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("REDO", click -> cb.setChecked(!checked))
                        .show();
            } else {
                Collections.sort(data);
                adt.notifyDataSetChanged();

                Snackbar.make(sw, getResources().getString(R.string.covidUnsort), BaseTransientBottomBar.LENGTH_LONG)
                        .setAction("UNDO", click -> cb.setChecked(!checked))
                        .show();
            }
        });

        dataFromActivity = getIntent().getExtras();
        String country = dataFromActivity.getString("Country");
        String fromDate = dataFromActivity.getString("FromDate");
        String toDate = dataFromActivity.getString("ToDate");

        savedCountry = country;
        savedDate = fromDate;

        fromDate = fromDate + "T00:00:00Z";
        toDate = toDate + "T23:59:59Z";

        myList = findViewById(R.id.covidList);
        adt = new MyListAdapter();
        myList.setAdapter(adt);

        myList.setOnItemLongClickListener((parent, view, pos, id) -> {
            CovidMessage msg = data.get(pos);
            Integer itemID = data.get(pos).getID();
            String itemDetails = dataDetails.get(itemID);

            AlertDialog.Builder adb = new AlertDialog.Builder(this)
                    .setTitle("Delete this entry?")
                    .setMessage(itemDetails)
                    .setNegativeButton("NO", (click, arg) ->{})
                    .setPositiveButton("YES", (click, arg) -> {
                        SQLiteDatabase db = openOrCreateDatabase("CovidDB", MODE_PRIVATE, null);
                        data.remove(pos);
                        db.delete("covid_t", "ID= ?", new String[] {Long.toString(msg.getID())});
                        adt.notifyDataSetChanged();
                        if(isTablet)
                            df.deleteFragment();
                    });

            adb.create().show();
            return true;
        });

        myList.setOnItemClickListener((theList, item, pos, id) -> {
            Integer itemID = data.get(pos).getID();
            String itemDetails = dataDetails.get(itemID);

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_DETAILS, itemDetails);

            if (isTablet) {
                df = new CovidDetailsFragment();
                df.setArguments(dataToPass);
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.covidFragmentLocation, df)
                        .commit();

            } else {
                Intent nextActivity = new Intent(this, CovidEmptyActivity.class);
                nextActivity.putExtras(dataToPass);
                startActivity(nextActivity);

            }
        });

        progressBar = findViewById(R.id.progressBar);

        query = new CovidResultsQuery();
        query.execute("https://api.covid19api.com/country/" + country + "/status/confirmed/live?from=" + fromDate + "&to=" + toDate);
    }

    /**
     * The purpose of this class is to connect to the API, get the search results from the resulting
     * JSON array, and store each object into our ArrayList
     *
     * @author Hamzeh
     */
    private class CovidResultsQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... args) {


            try {
                int progress = 0;
                publishProgress(progress);

                //Create URL object
                URL url = new URL(args[0]);

                //Open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }

                result = sb.toString();

                if (result != null && !result.trim().equalsIgnoreCase("[]")) {
                    JSONArray jArray = new JSONArray(result);

                    int progressStep = 100 / jArray.length();

                    if (progressStep == 0) {
                        progressStep = 1;
                    }
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject covidJSON = jArray.getJSONObject(i);

                        String province = covidJSON.getString("Province");
                        String latitude = covidJSON.getString("Lat");
                        String longitude = covidJSON.getString("Lon");
                        int cases = covidJSON.getInt("Cases");

                        if (cases > 0) {
                            counter++;
                            CovidMessage msg = new CovidMessage(counter, province, cases);
                            data.add(msg);
                            unsortedData.add(msg);

                            String details = "Latitude: " + latitude + "\nLongitude: " + longitude;
                            dataDetails.put(msg.getID(), details);
                        }

                        progress = progress + progressStep;
                        if (progress < 100) {
                            publishProgress(progress);


                        }
                    }
                }

                reader.close();
                urlConnection.disconnect();
                publishProgress(100);
            } catch (Exception e) {
                Log.e("The program has stopped", e.getMessage());
            }

            return "Task has been completed";
        }

        /**
         * This method is called whenever publishProgress is called. This method makes the progress
         * bar visible and updates it with the value passed in
         *
         * @param values the progress value to set the progress bar to
         */

        public void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        /**
         * This method executes after doInBackground finishes. Sole Purpose is to make the progress
         * bar invisible (since the task has been completed) and call notifyDataSetChanged to update
         * the list to properly show the new values
         *
         * @param s The string returned from doInBackground, in this case "Task has been completed"
         */


        protected void onPostExecute(String s) {
            progressBar.setVisibility(View.INVISIBLE);
            adt.notifyDataSetChanged();

            if (result != null && result.trim().equalsIgnoreCase("[]")) {
                AlertDialog.Builder adb = new AlertDialog.Builder(CovidSearchResult.this)
                        .setTitle("There is no data to show")
                        .setMessage("Select another day.")
                        .setPositiveButton("OK", (click, arg) -> {
                        });
                adb.create().show();
            }
        }
    }

    /**
     * This is a utility class for the ListView in this class only
     *
     * @author Hamzeh
     */

    class MyListAdapter extends BaseAdapter {


        /**
         * The purpose of this method is to get the list's size
         *
         * @return An Integer corresponding to the list's size
         */
        @Override
        public int getCount() {
            return data.size();
        }

        /**
         * The purpose of this method is to return the CovidMessage object at the selected index
         *
         * @param position, the index of the item in the list
         * @return A CovidMessage object at selected position
         */
        @Override
        public CovidMessage getItem(int position) {
            return data.get(position);
        }

        /**
         * The purpose of this method is to return the index of the object
         * @param position, the index of the item in the list
         * @return A long value corresponding to the position
         */
        @Override
        public long getItemId(int position) {
            return getItem(position).getID();
        }


        /**
         * The purpose of this method is to create and return a GUI representation for our list object
         *
         * @param position    , the index of the item in the list
         * @param convertView , the old View
         * @param parent      , the parent View
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = convertView;

            newView = inflater.inflate(R.layout.covid_listview_layout, parent, false);

            CovidMessage covidMessage = getItem(position);
            String province = covidMessage.getProvince();
            int numOfCases = covidMessage.getCases();


            TextView tvProv = newView.findViewById(R.id.provinceTxtView);
            tvProv.setText(province);
            TextView tvCases = newView.findViewById(R.id.casesTxtView);
            tvCases.setText("" + numOfCases);

            return newView;
        }
    }


    /**
     * This method is called whenever the user selects a menu icon from the toolbar
     *
     * @param item , the item that the user selected
     * @return a boolean value
     */


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.GoBackToHome):
                finish();
                break;
            case (R.id.covidSaveData):
                save();
                break;
            case (R.id.covidLoadData):
                load();
                break;
            case (R.id.covidHelpSearch):
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.covidHelpTitle))
                        .setMessage(getResources().getString(R.string.covidHelpResults))
                        .setPositiveButton("OK", (click, arg) -> {
                        });
                adb.create().show();
                break;
        }
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
            case (R.id.covidSaveData):
                save();
                break;
            case (R.id.covidLoadData):
                load();
                break;
            case (R.id.covidHelpSearch):
                android.app.AlertDialog.Builder adb = new android.app.AlertDialog.Builder(this)
                        .setTitle(getResources().getString(R.string.covidHelpTitle))
                        .setMessage(getResources().getString(R.string.covidHelpResults))
                        .setPositiveButton("OK", (click, arg) -> {
                        });
                adb.create().show();
                break;

        }

        DrawerLayout drawerLayout = findViewById(R.id.covid_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return false;

    }

    /**
     * The purpose of this method is to go to CovidDBload whenever the load icon is selected from
     * either the toolbaror the drawer
     *
     * @author Hamzeh
     */
    public void load() {
        Intent goToSaveData = new Intent(this, CovidDBLoad.class);
        startActivity(goToSaveData);

    }

    /**
     * The purpose of this method is to save the lsit currently on screne
     *
     * @author Hamzeh
     */
    public void save() {
        if (data.size() > 0) {
            SQLiteDatabase db = openOrCreateDatabase("CovidDB", MODE_PRIVATE, null);
            db.execSQL(CREATE_TABLE);
            String timeSaved = "" + new Date().getTime();

            for (int i = 0; i < data.size(); i++) {
                CovidMessage msg = data.get(i);
                String province = msg.getProvince();
                int cases = msg.getCases();

                db.execSQL("INSERT INTO covid_t (COUNTRY, DATE, PROVINCE, CASES, SAVE_ID) VALUES('"
                        + savedCountry + "','"
                        + savedDate + "','"
                        + province + "'," + cases + ",'" + timeSaved + "');");

            }
        }
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
        inflater.inflate(R.menu.covid_menu, menu);
        return true;

    }
}