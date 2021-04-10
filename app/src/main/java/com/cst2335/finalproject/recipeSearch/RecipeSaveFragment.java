package com.cst2335.finalproject.recipeSearch;

import com.cst2335.finalproject.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeSaveFragment extends DialogFragment {

    ListView savedRecipes;
    private ArrayList<String> nameList;
    private ArrayAdapter<String> adapter;
    HashMap<String, String> recipeMap = new HashMap<>();
    private AlertDialog.Builder alertBuilder;
    private boolean noSaves = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.recipe_save_fragment, container, false);

        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        savedRecipes = (ListView) view.findViewById(R.id.savedRecipeDialog);
        savedRecipes.setLongClickable(true);
        nameList = new ArrayList<>();

        alertBuilder = new AlertDialog.Builder(getActivity());

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, nameList);
        savedRecipes.setAdapter(adapter);

        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences("Saved", Context.MODE_PRIVATE);

        Map<String, ?> saveMap = sharedPreferences.getAll();

        if (saveMap.size() != 0) {
            for (String key : saveMap.keySet()) {

                nameList.add(key);
            }
        } else {
            Toast.makeText(getActivity(), "No Saved Recipes", Toast.LENGTH_LONG).show();
            return null;
        }


        adapter.notifyDataSetChanged();

        savedRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                Intent toRecipePage = new Intent(getActivity(), RecipePage.class);
                Bundle extras = new Bundle();

                String title = nameList.get(position);

                String recipeData = (String) saveMap.get(title);
                String[] split = recipeData.split("\\*");

                recipeMap.put("title", title);
                recipeMap.put("href", split[0]);
                recipeMap.put("ingredients", split[1]);

                extras.putSerializable("recipes", recipeMap);
                extras.putInt("position", -1);

                toRecipePage.putExtras(extras);
                startActivity(toRecipePage);
            }
        });

        savedRecipes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                alertBuilder.setMessage("Would you like to delete this saved recipe?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove(nameList.get(pos));
                                editor.commit();
                                nameList.remove(pos);
                                adapter.notifyDataSetChanged();
                            }
                        });
                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.setTitle("Delete Recipe");
                alert.show();

                return true;
            }
        });

        return view;
    }
}
