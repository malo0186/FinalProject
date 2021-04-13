package com.cst2335.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<RecipeItem> recipeItemList;

    /**
     * This is the Constructor
     *
     * @param context        Context, this is the context
     * @param recipeItemList ArrayList<RecipeItem>, this is the ArrayList with Recipe Items in it
     */
    public RecipeAdapter(Context context, ArrayList<RecipeItem> recipeItemList) {
        this.context = context;
        this.recipeItemList = recipeItemList;
    }

    /**
     * @return returns int of total items in list
     */
    @Override
    public int getCount() {
        return recipeItemList.size();
    }

    /**
     * @param position int, this is the position of the item in the ArrayList
     * @return returns Object of Item at given ArrayList position
     */
    @Override
    public Object getItem(int position) {
        return recipeItemList.get(position);
    }

    /**
     * @param position int, this is the position of the item in the database
     * @return returns long of Item at given database position
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * @param position    int, this is the Item's position in the ArrayList
     * @param convertView View, this is used to efficiently reuse old views
     * @param parent      ViewGroup, this is used to group the Objects and layouts
     * @return returns convertView to be reused
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.activity_listview_row, parent, false);
        }

        RecipeItem currentItem = (RecipeItem) getItem(position);
        TextView recipeTitleText = convertView.findViewById(R.id.recipeListTitleName);

        recipeTitleText.setText(currentItem.getTitle());

        return convertView;
    }
}
