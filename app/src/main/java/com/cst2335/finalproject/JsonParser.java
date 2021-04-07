package com.cst2335.finalproject;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParser {

    String TAG = "JsonParser";

    ArrayList<Ticket> listEvents;

    /**
     * parsing json and getting returning listEvents
     * @param url
     * @return listEvents
     */
    public ArrayList<Ticket> getJSONFromUrl(String url) {

        listEvents = new ArrayList<>();

        HttpResponse httpResponse = new HttpResponse();

        String jsonStr = httpResponse.getResponse(url);

        Log.e(TAG, "Response from url: " + jsonStr);

        if (jsonStr != null) {
            try {

                JSONObject root = new JSONObject(jsonStr);
                if(!root.has("_embedded"))
                {
                    return listEvents;
                }
                JSONObject _embedded = root.getJSONObject("_embedded");
                JSONArray events = _embedded.getJSONArray("events");

                // looping through All Events
                for (int i = 0; i < events.length(); i++) {
                    JSONObject c = events.getJSONObject(i);

                    String name =c.getString("name");
                    String starting_date = c.getJSONObject("dates").getJSONObject("start").getString("localDate");
                    StringBuilder sb= new StringBuilder();
                    sb.append("min: ");
                    if(c.has("priceRanges")) {
                        JSONArray priceRanges = c.getJSONArray("priceRanges");
                        JSONObject price = priceRanges.getJSONObject(0);

                        sb.append(price.getDouble("min"));
                        sb.append(" max: ");
                        sb.append(price.getDouble("max"));
                    } else {
                        sb.append("Price range not given,");
                    }
                    String price_range = sb.toString();
                    String urll = c.getString("url");
                    String promotional_banner_url = c.getJSONArray("images").getJSONObject(0).getString("url");

                    Ticket ticket = new Ticket(-1,name,starting_date,price_range,urll,promotional_banner_url);
                    listEvents.add(ticket);
                }

                return listEvents;

            } catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
            }

        } else {
            Log.e(TAG, "Couldn't get json from server.");
        }

        return null;

    }
}
