package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RecipeListActivity extends AppCompatActivity {
    private final static String TAG = "RecipeList";
    List<RecipeItem> recipeItemList = new ArrayList<>();
    ListView recipeListView;
    private ProgressBar progressBar;
    private Button backButton;
    private ImageButton helpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_list);

        backButton = findViewById(R.id.recipeListBackToSearchButton);
        helpBtn = findViewById(R.id.recipeListHelpButton);
        progressBar = findViewById(R.id.recipeProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        recipeListView = findViewById(R.id.recipeListView);
        RecipeQuery query = new RecipeQuery();
        query.execute();


        // instantiate the custom list adapter
        RecipeAdapter adapter = new RecipeAdapter(this, (ArrayList<RecipeItem>) recipeItemList);

        // get the ListView and attach the adapter
        recipeListView.setAdapter(adapter);
        recipeListView.setOnItemClickListener((adapterView, view, position, id) -> {
            RecipeItem item = (RecipeItem) adapterView.getItemAtPosition(position);
            Intent goToRecipeDetails = new Intent(RecipeListActivity.this, RecipeDetailsActivity.class);
            goToRecipeDetails.putExtra("recipeItem", item);
            startActivity(goToRecipeDetails);
        });
        backButton.setOnClickListener(view -> {
            Intent goBackToSearch = new Intent(RecipeListActivity.this, RecipeSearchActivity.class);
            startActivity(goBackToSearch);
        });
        helpBtn.setOnClickListener(view -> new AlertDialog.Builder(RecipeListActivity.this)
                .setTitle(getText(R.string.activityHelpTitle))
                .setMessage(getText(R.string.recipeListHelpMessage))
                .setNeutralButton(getText(R.string.alertOK), null)
                .show());
    }


    private class RecipeQuery extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... strings) {

            Intent data = getIntent();

            String queryURL = "http://www.recipepuppy.com/api/?q=" + data.getStringExtra("recipeText") + "&p=3";
            publishProgress(50);

            String res = null;

            try {

                String jsonString = getUrlString(queryURL);
                Log.i(TAG, "Received JSON: " + jsonString);
                JSONObject jsonObject = new JSONObject(jsonString);
                JSONArray result = jsonObject.getJSONArray("results");

                for (int j = 0; j < result.length(); j++) {
                    JSONObject r = result.getJSONObject(j);
                    RecipeItem item = new RecipeItem(r.getString("title"),
                            r.getString("ingredients"),
                            r.getString("href"));
                    recipeItemList.add(item);
                }
                publishProgress(100);


            } catch (JSONException je) {
                res = "Failed to parse JSON";

            } catch (IOException e) {
                res = "Failed to fetch items";
            }
            return res;

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.INVISIBLE);
        }

        public String getUrlString(String urlSpec) throws IOException {
            return new String(getUrlBytes(urlSpec));
        }

        public byte[] getUrlBytes(String urlSpec) throws IOException {
            URL url = new URL(urlSpec);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = connection.getInputStream();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException(connection.getResponseMessage() +
                            ": with " +
                            urlSpec);
                }

                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                return out.toByteArray();
            } catch (MalformedURLException mfe) {
                Log.i(TAG, "Malformed URL exception.");
            } catch (IOException ioe) {
                Log.i(TAG, "IO Exception." + ioe.getMessage());
            } finally {
                connection.disconnect();
            }
            return null;
        }

    }
}