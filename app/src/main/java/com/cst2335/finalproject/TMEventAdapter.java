package com.cst2335.finalproject;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class TMEventAdapter extends ArrayAdapter<Ticket> {
    /**
     * Constructor for EventAdapter
     * @param context
     * @param resource
     */
    public TMEventAdapter(@NonNull Context context, ArrayList<Ticket> resource) {
        super(context,0, resource);
    }

    /**
     * getView method to manipulate our items with data
     * @param position
     * @param convertView
     * @param parent
     * @return View
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Ticket ticket = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_event_item, parent, false);
        }

        TextView tv_name = ((TextView)convertView.findViewById(R.id.tv_name));
        tv_name.setText(ticket.getName());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), TMDetailActivity.class);

                if(ticket.getId() == -1)
                    i.putExtra("isAdded",false);
                else
                    i.putExtra("isAdded",true);

                i.putExtra("id",ticket.getId());
                i.putExtra("name", ticket.getName());
                i.putExtra("priceRange", ticket.getPrice_range());
                i.putExtra("startingDate", ticket.getStarting_date());
                i.putExtra("banner",ticket.getPromotional_banner_url());
                i.putExtra("url",ticket.getUrl());
                i.addFlags(FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(i);
            }
        });

        return convertView;
    }

}
