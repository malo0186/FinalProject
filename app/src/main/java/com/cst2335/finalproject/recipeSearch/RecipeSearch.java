package com.cst2335.finalproject.recipeSearch;

import com.cst2335.finalproject.MainActivity;
import com.cst2335.finalproject.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
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


public class RecipeSearch extends AppCompatActivity {
    private SearchView searchView;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<String> nameList;
    private ArrayAdapter<String> adapter;
    private ProgressDialog progressDialog;
    private AlertDialog.Builder alertBuilder;
    private String url = "http://www.recipepuppy.com/api/?q=";
    private String searchUrl;
    private String searchResultString;
    private Toolbar recipeToolbar;
    private int progress = 0;
    HashMap<String, String> recipeMap = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);

        recipeToolbar = findViewById(R.id.recipe_toolbar);
        setSupportActionBar(recipeToolbar);
        getSupportActionBar().setTitle("Recipe Search");
        recipeToolbar.setTitleTextColor(Color.WHITE);
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

                startProgressBar(progress);
                progressBar.setVisibility(View.VISIBLE);

                searchUrl = url + query.replaceAll("\\s+", "+");

                new GetRecipeNames().execute();

                searchView.setQuery("", false);
                searchView.clearFocus();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                nameList.removeAll(nameList);
                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                Intent toRecipePage = new Intent(RecipeSearch.this, RecipePage.class);
                Bundle extras = new Bundle();

                extras.putSerializable("recipes", recipeMap);
                extras.putInt("position", position);

                toRecipePage.putExtras(extras);
                startActivity(toRecipePage);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.recipe_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_saves) {
            RecipeSaveFragment saveFragment = new RecipeSaveFragment();
            saveFragment.show(getSupportFragmentManager(), "savedRecipes");
            return true;
        }

        if (id == R.id.action_help) {
            alertBuilder.setMessage("Enter the name of a dish in the search box to find associated recipes.\n\nClick on a recipe to get more information.")
                    .setCancelable(false)
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            AlertDialog alert = alertBuilder.create();
            alert.setTitle("Help");
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class GetRecipeNames extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(RecipeSearch.this);
            progressDialog.setMessage("Searching...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            try {
                URL tempUrl = new URL(searchUrl);
                HttpURLConnection conn = (HttpURLConnection) tempUrl.openConnection();
                conn.setRequestMethod("GET");
                // read the response
                InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                searchResultString = makeStreamAString(inputStream);

            } catch (Exception e) {
            }

            try {
                JSONObject jsonObj = new JSONObject(searchResultString);
                JSONArray recipeArray = jsonObj.getJSONArray("results");
                // loop through All Recipes
                for (int i = 0; i < recipeArray.length(); i++) {

                    JSONObject rowItem = recipeArray.getJSONObject(i);

                    String titleString = rowItem.getString("title");

                    nameList.add(titleString);

                    String pos = String.valueOf(i);

                    recipeMap.put(pos + "title", titleString);
                    recipeMap.put(pos + "href", rowItem.getString("href"));
                    recipeMap.put(pos + "ingredients", rowItem.getString("ingredients"));
                    recipeMap.put(pos + "thumbnail", rowItem.getString("thumbnail"));
                }

            } catch (final JSONException e) {
            }

            SystemClock.sleep(500);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            if (progressBar.getProgress() == 100) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            if (nameList.isEmpty()) {
                Toast.makeText(RecipeSearch.this, "No Results", Toast.LENGTH_LONG).show();
            }
            adapter.notifyDataSetChanged();
        }
    }

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