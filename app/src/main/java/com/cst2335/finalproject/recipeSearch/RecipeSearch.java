package com.cst2335.finalproject.recipeSearch;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import androidx.appcompat.widget.Toolbar;

import com.cst2335.finalproject.R;

public class RecipeSearch extends AppCompatActivity {
    /**
     * @param description: instructs the user in the Toolbar Overflow menu
     * @param TAG: return the simple name of the class
     * @param pDialog: is for the timed dialogue box
     * @param recipeListView: initialize the XML element
     * @param recipeBarStatus: assign object to XML
     * @param buttonSearch: is the main button within the activitiy. OnClickListener to pull JSON data from server and display in ListView
     * @param progress: integer assigned to variable to calculate 0 to 100 with 1 second delay
     * @param recipeBar: initialize the XML element
     * @param myAdapter: object for dynamic ListView Adapter
     * @param ITEM_SELECTED: used for fragment
     * @param ITEM_POSITION: used for fragment
     * @param ITEM_ID: used for fragment
     * @param url: string to URL address for Chicken Breast
     * @param ACTIVITY_NAME: Enum to declare which activity is running on the Debug Server Output
     * @param recipeList: array to store JSON parse
     */

    private String description = "Recipe Search";
    private String TAG = RecipeSearch.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView recipeListView;
    private Button buttonSearch;
    int progress = 0;
    ProgressBar recipeBar;
    BaseAdapter myAdapter;
    // FRAGMENT
    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ID = "ID";
    public static final int EMPTY_ACTIVITY = 345;
    private static String url = "http://www.recipepuppy.com/api/?i=onions,garlic&q=omelet&p=3";
    public static final String ACTIVITY_NAME = "Recipe Finder";
    ArrayList<String> recipeList = new ArrayList<>(Arrays.asList());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_search);
        //recipeList = new ArrayList<>();

/**
 * @param buttonSearch: initialize and reference to XML
 * @param recipeBar: initialize and reference XML
 * @param theList: initialize and reference XML
 * - OnClickListener added to show toast when row clicked
 * @param recipeListView; initialize and reference to XML
 *
 */
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        recipeBar = (ProgressBar) findViewById(R.id.progressBar);

        recipeListView = (ListView) findViewById(R.id.theList);
        recipeListView.setAdapter(myAdapter = new MyListAdapter());

/**
 * Start Fragment
 * @param isTablet: boolean variable to determine XML screen layout (tablet vs. phone)
 * @param recipeListView; set onClickListerner to open Fragment when user clicks on item
 * @param nextActivity; create an object to pass data to fragment
 */

        recipeListView.setOnItemClickListener((list, item, position, id) -> { // sets on click listener on the list view item

            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, recipeList.get(position));
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putLong(ITEM_ID, id);
        });
/**
 * START SHARED PREFERENCES
 * @param searchText; initialize value and reference to XML
 * @param chat; create local variable to get text from searchText
 */

        //Shared Preferences and create Log for Last searched item
        EditText searchText = findViewById(R.id.searchText); // Pulling from msgText to move a variable to Shared Preferences
        SharedPreferences prefs = getSharedPreferences("RecipeFinder", MODE_PRIVATE);
        String chat = prefs.getString("recipeText", "");
        searchText.setText(chat);

/**
 * START SearchButton onClickListener
 *@param buttonSearch; setOnClickListener to start, Shared Preference, ProgressBar and make visible, Pull Data from WebServer, Alert Box and Notify ListView of change with  notifyDataSetChanged()
 *@param onClick; method onClick save text to Shared Preference
 *@param GetRecipes(); Will subsequently pull the data from the WebServer and parse the JSON format
 *@param dbOpener; establish connection to SQLite3 Database
 */

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SHARED PREFERENCES
                SharedPreferences.Editor chatEditor = prefs.edit(); // SharedPrefs
                chatEditor.putString("recipeText", searchText.getText().toString());
                chatEditor.commit();

                setProgressValue(progress); // CALLS METHOD for Progress Bar
                new GetRecipes().execute();
                if (buttonSearch != null) {
                    recipeBar.setVisibility(View.VISIBLE);
                }
                System.out.println("1 RecipeList Print:           " + recipeList);
                System.out.println("RecipeListView Print:           " + recipeListView);
                myAdapter.notifyDataSetChanged();
            }
        });
//        Toolbar tbar = (Toolbar) findViewById(R.id.recipeToolbar);
//        setSupportActionBar(tbar);
        System.out.println("Database Connected");
        //CALL THE DATABASE HELPER CLASS
        try {
            RecipeSearchDB dbOpener = new RecipeSearchDB(this);
            SQLiteDatabase db = dbOpener.getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            System.out.println("SQL LITE EXCEPTION from RecipeFinder: " + e);
        }
    } // END onCreate()

    private class GetRecipes extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(RecipeSearch.this);
            pDialog.setMessage("Please wait... Downloading Recipes");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            // Making a request to url and getting response
            String urlString = establishConnection(url);
            Log.i(TAG, "Response from url: " + urlString);
            if (urlString != null) {
                try {
                    JSONObject jsonObj = new JSONObject(urlString);
                    JSONArray recipeArray = jsonObj.getJSONArray("results");
                    // loop through All Recipes
                    for (int i = 0; i < recipeArray.length(); i++) {
                        JSONObject rowItem = recipeArray.getJSONObject(i);

                        String titleString = rowItem.getString("title");
                        String sourceURLString = rowItem.getString("href");
                        String ingredients = rowItem.getString("ingredients");
                        String imageRecipeString = rowItem.getString("thumbnail");




/* ATTRIBUTES NOT USED IN THE JSON PARSING
                        String f2f_urlString = rowItem.getString("f2f_url");
                        String socialRankString = rowItem.getString("social_rank");
                        String publisherURLString = rowItem.getString("publisher_url");
*/

                        HashMap<String, String> recipeItems = new HashMap<>();

                        // adding each child node to HashMap key => value
                        recipeItems.put("\n" + "Title: ", titleString + "\n");
                        recipeItems.put("\n" + "URL: ", sourceURLString + "\n");
                        recipeItems.put("\n" + "Ingregients: ", ingredients + "\n");

                        recipeItems.put("\n" + "Image: ", imageRecipeString + "\n");

                        System.out.println("Print out Recipes: " + recipeItems);
                        //recipeList.add(String.valueOf(recipeItems));
                        recipeList.add(String.valueOf(recipeItems));
                        System.out.println("2 RecipeList Print:           " + recipeList);

                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    } // End void run
                });
            } // End else
            return null;
        } // End doInBackground()

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
            myAdapter.notifyDataSetChanged();
        } // END onPostExecute
    } // End GetRecipes Class Async

    private class MyListAdapter extends BaseAdapter {

        public int getCount() {
            return recipeList.size();
        } //This function tells how many objects to show

        public String getItem(int position) {
            return recipeList.get(position);
        }  //This returns the string at position p

        public long getItemId(int p) {
            return p;
        } //This returns the database id of the item at position p

        public View getView(int p, View recycled, ViewGroup parent) {
            View thisRow = recycled;

            if (recycled == null)
                thisRow = getLayoutInflater().inflate(R.layout.activity_recipe_search_row, null);

            TextView titleString = thisRow.findViewById(R.id.txtViewTitle);

            titleString.setText("Recipe Suggestions: " + p + " is" + getItem(p) + "\n");
/*
            TextView sourceURLString = thisRow.findViewById(R.id.txtViewSourceURL  );
            sourceURLString.setText( "Source URL-------: " + p + " is\n " + getItem(p) +"\n");
            TextView recipeIDString = thisRow.findViewById(R.id.txtViewRecipe_id  );
            recipeIDString.setText( "Recipe ID: " + p + " is" + getItem(p) +"\n");
            TextView publisherString = thisRow.findViewById(R.id.txtViewPublisher  );
            publisherString.setText( "Publisher: " + p + " is\n " + getItem(p) +"\n");
            TextView imageRecipeString = thisRow.findViewById(R.id.txtViewImage_url  );
            imageRecipeString.setText( "Image-------: " + p + " is\n " + getItem(p) +"\n");
*/
            return thisRow;
        } // End Recycler View
    } // End MyListAdapter

    /**
     * @param progress is an Integer value from 0 to 100 with a 1 second delay
     */
    private void setProgressValue(final int progress) {
        recipeBar.setProgress(progress);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(900);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setProgressValue(progress + 25);
            }
        });
        thread.start();
    } // END setProgressValue

    //This function only gets called on the phone. The tablet never goes to a new activity

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EMPTY_ACTIVITY) {
            if (resultCode == RESULT_OK) //if you hit the delete button instead of back button
            {
                long id = data.getLongExtra(ITEM_ID, 0);
                deleteMessageId((int) id);
            }
        }
    }

    public void deleteMessageId(int id) {
        Log.i("Delete this message:", " id=" + id);
        recipeList.remove(id);
        myAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ACTIVITY_NAME, "onResume()");
    }

    @Override
    protected void onDestroy() {
        Log.e(ACTIVITY_NAME, "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.e(ACTIVITY_NAME, "onStart()");
        super.onStart();
    }

    @Override
    protected void onPause() {
        Log.e(ACTIVITY_NAME, "onPause()");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.e(ACTIVITY_NAME, "onStop()");
        super.onStop();
    }

    public String establishConnection(String reqUrl) {
        /**
         * START Establish connection using the HttpURLConnection library
         * @param url; store the url path
         * @param TAG; inform debug error (e) which exception was triggered
         */

        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // read the response
            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(inputStream);
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    } // End establishConnection()



    private String convertStreamToString(InputStream is) {
        /**
         * START convert data stream to readable string by initializing the BufferReader
         * @param reader; local variable to store input
         * @param sb; create local variable to build string and append
         * @return sb.toString(); input to string plus append
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    } // End convertStreamToString()
} // END RecipeFinder.java