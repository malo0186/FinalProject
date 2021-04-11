package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class TMFavoritesActivity extends AppCompatActivity {

    private ListView listEvents;
    private TMEventAdapter adapter;
    private TMDatabase tmDatabase;
    private ArrayList<Ticket> list;

    /**
     * this will be called when TMFavoritesActivity is about to be created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t_m_favorites);

        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);
        getSupportActionBar().setTitle(R.string.favorite);

        listEvents = findViewById(R.id.listEvents);

        tmDatabase = new TMDatabase(this);
    }

    /**
     * this will be called when TMFavoritesActivity will be resumed
     */
    @Override
    protected void onResume() {
        super.onResume();
        list = tmDatabase.getAllFavoriteEvents();
        adapter = new TMEventAdapter(this, list);
        listEvents.setAdapter(adapter);
        if(list.size() == 0) {
            Toast.makeText(this, "There's no events stored here!", Toast.LENGTH_SHORT).show();
        }
    }
}