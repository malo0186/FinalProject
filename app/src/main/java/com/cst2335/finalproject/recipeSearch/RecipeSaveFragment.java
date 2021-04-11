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

/**
 * This fragment is where the saved recipes are presented to the user.
 */
public class RecipeSaveFragment extends DialogFragment {

    ListView savedRecipes;
    private ArrayList<String> nameList;
    private ArrayAdapter<String> adapter;
    HashMap<String, String> recipeMap = new HashMap<>();
    private AlertDialog.Builder alertBuilder;
    private boolean noSaves = false;

    /**
     * Here the saved recipes fragment is initialized.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.recipe_save_fragment, container, false);

        // this is required to give the DialogFragment rounded corners
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

        // retrieve the saved recipes in shared preferences
        SharedPreferences sharedPreferences = this.getActivity().getSharedPreferences(RecipePage.SAVED_RECIPES_KEY, Context.MODE_PRIVATE);

        Map<String, ?> saveMap = sharedPreferences.getAll();

        // check if there are any saved recipes
        // show a toast message if it is empty, then return null
        if (saveMap.size() != 0) {
            // add the names (which are the keys) of the recipes to an ArrayList
            for (String key : saveMap.keySet()) {

                nameList.add(key);
            }
        } else {
            Toast.makeText(getActivity(), R.string.toast_dialog_no_saves, Toast.LENGTH_LONG).show();
            return null;
        }


        adapter.notifyDataSetChanged();

        // show the recipe information upon user-click
        savedRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long arg3) {

                Intent toRecipePage = new Intent(getActivity(), RecipePage.class);
                Bundle extras = new Bundle();

                // get the name of the recipe the user wishes to view
                String title = nameList.get(position);

                // the recipe data is stored in one string
                // using "*" as a delimiter between the URL and the ingredients
                String recipeData = (String) saveMap.get(title);
                String[] split = recipeData.split("\\*");

                // add the recipe data to a HashMap
                recipeMap.put(RecipeSearch.TITLE, title);
                recipeMap.put(RecipeSearch.HREF, split[0]);
                recipeMap.put(RecipeSearch.INGREDIENTS, split[1]);

                // add the data to a bundle, and flag the position as -1 to indicate a saved recipe
                extras.putSerializable(RecipeSearch.RECIPES_KEY, recipeMap);
                extras.putInt(RecipeSearch.POSITION_KEY, -1);

                // begin saved recipes fragment
                toRecipePage.putExtras(extras);
                startActivity(toRecipePage);
            }
        });

        // long press saved recipe allows user to delete the entry
        savedRecipes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // present the delete saved recipe alert dialog upon long click
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {

                alertBuilder.setMessage(R.string.toast_dialog_delete_save_text)
                        .setCancelable(false)
                        .setPositiveButton(R.string.toast_dialog_delete_save_text_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                // remove the saved recipe if the user confirms
                                editor.remove(nameList.get(pos));
                                editor.commit();
                                nameList.remove(pos);
                                adapter.notifyDataSetChanged();
                            }
                        });
                alertBuilder.setNegativeButton(R.string.toast_dialog_delete_save_text_no, new DialogInterface.OnClickListener() {
                    // do nothing if the user does not wish to delete saved recipe
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                AlertDialog alert = alertBuilder.create();
                alert.setTitle(R.string.toast_dialog_delete_save_text_title);
                alert.show();

                return true;
            }
        });

        return view;
    }
}
