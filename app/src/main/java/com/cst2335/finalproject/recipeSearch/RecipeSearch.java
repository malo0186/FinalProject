package com.cst2335.finalproject.recipeSearch;

import com.cst2335.finalproject.MainActivity;
import com.cst2335.finalproject.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The main landing page for the recipe search portion of this application.
 * @author Cal Malouin
 */
public class RecipeSearch extends AppCompatActivity {
    private SearchView searchView;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> nameList;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertBuilder;
    // these String keys are used throughout the recipe search application
    protected static final String URL = "http://www.recipepuppy.com/api/?q=";
    protected static final String TITLE = "title";
    protected static final String HREF = "href";
    protected static final String THUMBNAIL = "thumbnail";
    protected static final String INGREDIENTS = "ingredients";
    protected static final String RECIPES_KEY = "recipes";
    protected static final String POSITION_KEY = "position";
    protected static final String SAVES_FRAGMENT_TAG = "position";
    private String searchUrl;
    private String searchResultString;
    private Toolbar recipeToolbar;
    private int progress = 0;
    HashMap<String, String> recipeMap = new HashMap<>();

    /**
     * The search page of the recipe search application is initialized here.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        // set up the toolbar for this activity
        recipeToolbar = findViewById(R.id.recipe_toolbar);
        setSupportActionBar(recipeToolbar);
        getSupportActionBar().setTitle(R.string.recipe_search_toolbar_title);
        getSupportActionBar().setSubtitle(R.string.recipe_search_toolbar_subtitle);
        // display the home menu button on the left side
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.recipe_back_to_main_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recipeToolbar.setTitleTextColor(Color.WHITE);
        recipeToolbar.setSubtitleTextColor(Color.WHITE);
        recipeToolbar.setBackgroundColor(Color.DKGRAY);
        recipeToolbar.setOverflowIcon(ResourcesCompat.getDrawable(getResources(), R.drawable.overflow_white, null));

        searchView = (SearchView) findViewById(R.id.searchView);
        listView = (ListView) findViewById(R.id.searchResults);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        alertBuilder = new AlertDialog.Builder(this);

        nameList = new ArrayList<>();

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameList);
        listView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                // progress bar begins animation when user submits query text
                startProgressBar(progress);
                progressBar.setVisibility(View.VISIBLE);

                // append the url with the users query text
                // replace any whitespace with '+'
                searchUrl = URL + query.replaceAll("\\s+", "+");

                // call the Async task to retrieve the recipe JSON data from the server
                new GetRecipeNames().execute();

                // we want all the results to be shown; results are not filtered
                searchView.setQuery("", false);
                // close the keyboard when user has completed search
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                // reset the list when user begins typing another query
                nameList.removeAll(nameList);
                return false;
            }
        });

        // listener for the search results
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                // will take user to the recipe page, displaying info on selected recipe
                Intent toRecipePage = new Intent(RecipeSearch.this, RecipePage.class);
                Bundle extras = new Bundle();

                // pass the HashMap with the recipes, as well as the position of the chosen recipe
                extras.putSerializable(RECIPES_KEY, recipeMap);
                extras.putInt(POSITION_KEY, position);

                toRecipePage.putExtras(extras);
                startActivity(toRecipePage);
            }
        });
    }

    /**
     * The menu used in the toolbar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
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

        // show the saved recipe dialog fragment
        if (id == R.id.action_saves) {
            RecipeSaveFragment saveFragment = new RecipeSaveFragment();
            saveFragment.show(getSupportFragmentManager(), SAVES_FRAGMENT_TAG);
            return true;
        }

        // show the help alert dialog popup if the user selects 'Help' in the menu
        if (id == R.id.action_help) {
            alertBuilder.setMessage(R.string.alert_dialog_recipe_search_help_text)
                    .setCancelable(false)
                    .setPositiveButton(R.string.alert_dialog_okay_button, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle(R.string.help);
            alert.show();
        }

        // bring the user back to the home page to switch between functions in the application
        if (id == android.R.id.home) {
            Intent toMainMenu = new Intent(RecipeSearch.this, MainActivity.class);
            startActivity(toMainMenu);
        }

        // this will clear the search results and bring the search view back in to focus
        if (id == R.id.action_recipe_search_homepage) {
            Intent toSearch = new Intent(RecipeSearch.this, RecipeSearch.class);
            startActivity(toSearch);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This is the Async task which retrieves the recipe JSON data.
     */
    private class GetRecipeNames extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // show a progress dialog while the recipe data is being retrieved
            progressDialog = new ProgressDialog(RecipeSearch.this);
            progressDialog.setMessage("Searching...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * An HTTP Connection is created here to retrieve the recipe JSON data from the server.
         * @param arg0
         * @return
         */
        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                // open an HTTP connection
                URL tempUrl = new URL(searchUrl);
                HttpURLConnection conn = (HttpURLConnection) tempUrl.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                // convert the stream into a single String
                searchResultString = makeStreamAString(inputStream);

            } catch (Exception e) {
            }

            try {
                // store the results in a JSON array
                JSONObject jsonObj = new JSONObject(searchResultString);
                JSONArray recipeArray = jsonObj.getJSONArray("results");
                // loop through all Recipes
                for (int i = 0; i < recipeArray.length(); i++) {

                    // loop through the JSON array
                    JSONObject rowItem = recipeArray.getJSONObject(i);

                    String titleString = rowItem.getString(TITLE);

                    // store the names in an Array List to display results
                    nameList.add(titleString);

                    String pos = String.valueOf(i);

                    // store all recipe data in a Hashmap
                    // the key is appended with the position so that they are unique
                    recipeMap.put(pos + TITLE, titleString);
                    recipeMap.put(pos + HREF, rowItem.getString(HREF));
                    recipeMap.put(pos + INGREDIENTS, rowItem.getString(INGREDIENTS));
                    recipeMap.put(pos + THUMBNAIL, rowItem.getString(THUMBNAIL));
                }

            } catch (final JSONException e) {
            }

            SystemClock.sleep(500);

            return null;
        }

        /**
         * After the recipe JSON data is retrieved, any search dialog is removed.
         * @param result
         */
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            // hide searching progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            // hide progress bar
            if (progressBar.getProgress() == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            }
            // if no results, display a Toast message saying so
            if (nameList.isEmpty()) {
                Toast.makeText(RecipeSearch.this, R.string.toast_dialog_no_results, Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Seperate method to read the JSON data retrieved from the server, and return it as a String.
     * @param stream
     * @return
     */
    private String makeStreamAString(InputStream stream) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder builder = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
        } catch (IOException e) {
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
            }
        }
        return builder.toString();
    }

    /**
     * The progress bar gives the user feedback that their query is being processed.
     * @param progress
     */
    private void startProgressBar(final int progress) {
        progressBar.setProgress(progress);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startProgressBar(progress + 20);
            }
        });
        thread.start();
    }
}