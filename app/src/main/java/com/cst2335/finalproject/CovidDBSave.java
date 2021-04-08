package com.cst2335.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * The purpose of this class is to show the saved list selected from CovidDBLoad
 */

public class CovidDBSave extends AppCompatActivity {

    String country ="";
    String date = "";
    ArrayList<CovidData> data = new ArrayList<>();
    MyListAdapter adt;
    SQLiteDatabase db;
    ListView myList;


    /**
     * This is where the activity is initalized. Here, setContentView is called with a layout
     * defining the UI for activity and findViewById is called on our widgets that we want to manipulate
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_covid_db_save);

        Intent dataFromActivity = getIntent();
        String save_ID = dataFromActivity.getStringExtra(CovidDBLoad.SAVE_ID);

        myList = findViewById(R.id.covidSavedList);
        adt = new MyListAdapter();
        myList.setAdapter(adt);
        db = openOrCreateDatabase("CovidDB", MODE_PRIVATE, null);

        String params[] = new String[] {save_ID};
        Cursor result = db.rawQuery("SELECT COUNTRY, DATA, PROVINCE, CASES FROM covid_T WHERE SAVE_ID = ?", params);


        if (result != null) {
            result.moveToFirst();

            while (result.moveToNext()) {
                String savedCountry = result.getString(0);
                String savedDate = result.getString(1);
                String province = result.getString(2);
                Integer cases = result.getInt(3);

                country = savedCountry;
                date = savedDate;

                data.add(new CovidData(province, cases));
            }
            adt.notifyDataSetChanged();

            TextView tvCountry = findViewById(R.id.covidSavedCountry) ;
            tvCountry.setText(country);
            TextView tvDate = findViewById(R.id.covidSavedDate);
            tvDate.setText(date);
        }

    }

    /** This is a utility class for the ListView in this class only
     * @author  Hamzeh
     */

    public class MyListAdapter extends BaseAdapter {

        /**
         * The purpose of this method is to get the list's size
         *
         * @return An integer corresponding to the list's size
         */
        @Override
        public int getCount() {
            return data.size();

        }

        /**
         * The purpose of this method is to return the CovidData object at the selected index
         *
         * @param position, the index of the item in the list
         * @return A CovidData object at selected position
         */

        @Override
        public CovidData getItem(int position) {
            return data.get(position);
        }

        /**
         * The purpose of this method is to return the index of the object
         *
         * @param position, the index of the item in the list
         * @return A long value corresponding to the position
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /**
         * The purpose of this method is to create and return a GUI representation for our list object
         *
         * @param position,    the index of the item in the list
         * @param convertView, the old View
         * @param parent,      the parent View
         * @return
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View newView = convertView;

            newView = inflater.inflate(R.layout.covid_listview_layout, parent, false);
            

            CovidData covidData = getItem(position);
            String province = covidData.getProvince();
            int numOfCases = covidData.getCases();

            TextView tvProv = newView.findViewById(R.id.provinceTxtView);
            tvProv.setText(province);
            TextView tvCases = newView.findViewById(R.id.casesTxtView);
            tvCases.setText("" + numOfCases);

            return newView;
        }
    }

    /**
     * The purpose of this class is to create the object that our list holds
     * @author Hamzeh
     */
    public  class CovidData {
        String province = "";
        Integer cases;

        public CovidData(String province, Integer cases) {
            setProvince(province);
            setCases(cases);

        }

        public String getProvince() {return province;}
        public void setProvince(String province) {this.province = province; }

        public Integer getCases() {return  cases;}
        public void setCases(Integer cases) {this.cases = cases; }




}
}