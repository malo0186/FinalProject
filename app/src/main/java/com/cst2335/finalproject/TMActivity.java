package com.cst2335.finalproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TMActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String API_KEY = "Kk5lyOuUpAIFUZDsYqf6f0wBXedDwdGZ";
    private TextView input_radius,input_City;
    private Button btn_search,btn_favorite;
    private ListView list_events;
    private TMEventAdapter adapter;
    private ProgressBar progressbar;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * called when TMActivity is about to be created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_m);

        getSupportActionBar().setTitle(R.string.main_toolbar_title);

        input_City = findViewById(R.id.input_City);
        input_radius = findViewById(R.id.input_radius);
        btn_search = findViewById(R.id.btn_search);
        btn_favorite = findViewById(R.id.btn_favorite);
        progressbar = findViewById(R.id.progressbar);
        list_events = findViewById(R.id.list_events);

        sharedPreferences = getSharedPreferences("tm", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        input_City.setText(sharedPreferences.getString("last_city",""));
        input_radius.setText(sharedPreferences.getString("last_radius",""));

        btn_search.setOnClickListener(this);
        btn_favorite.setOnClickListener(this);
    }

    /**
     * creating toolbar menu
     * @param menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    /**
     * Handling on click of toolbar menu
     * @param item
     * @return boolean
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_help:

                String details = getString(R.string.main_details);

                openHelperDialog(details);

                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return false;
    }

    /**
     * create and show Helper Alert Dialog
     * @param details
     */
    private void openHelperDialog(String details) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Help").setMessage(details).setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * handling onclick of buttons
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.btn_search:
                    if(!input_City.getText().toString().isEmpty() && !input_radius.getText().toString().isEmpty()) {
                        editor.putString("last_city",input_City.getText().toString());
                        editor.putString("last_radius",input_radius.getText().toString());
                        editor.commit();

                        StringBuilder sb = new StringBuilder();
                        sb.append("https://app.ticketmaster.com/discovery/v2/events.json?apikey=");
                        sb.append(API_KEY);
                        sb.append("&city=");
                        sb.append(input_City.getText().toString());
                        sb.append("&radius=");
                        sb.append(input_radius.getText().toString());

                        GetEvents getEvents= new GetEvents();
                        getEvents.execute(sb.toString());

                    } else {
                        Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                    }
                break;

            case R.id.btn_favorite:
                startActivity(new Intent(TMActivity.this,TMFavoritesActivity.class));
                break;
        }
    }

    public class GetEvents extends AsyncTask<String, Void, ArrayList<Ticket>> {
        /**
         * show Progress dialog before fetching list of events
         */
        @Override
        protected void onPreExecute() {
            progressbar.setVisibility(View.VISIBLE);
        }

        /**
         * parsing and fetching list of events
         * @param strings
         * @return listEvents
         */
        @Override
        protected ArrayList<Ticket> doInBackground(String... strings) {
            try {
                JsonParser jsonParser = new JsonParser();
                String url = strings[0];
                return jsonParser.getJSONFromUrl(url);
            } catch (Exception e) {
                progressbar.setVisibility(View.GONE);
            }
            return null;
        }

        /**
         * getting data and filling listView
         * @param list
         */
        @Override
        protected void onPostExecute(ArrayList<Ticket> list) {
            if(list.size() ==0) {
                Toast.makeText(TMActivity.this, "No Events Found.", Toast.LENGTH_SHORT).show();
            } else {
                adapter = new TMEventAdapter(getApplicationContext(), list);
                list_events.setAdapter(adapter);
            }
            progressbar.setVisibility(View.GONE);
        }

    }
}