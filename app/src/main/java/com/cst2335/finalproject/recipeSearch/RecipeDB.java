package com.cst2335.finalproject.recipeSearch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.HashMap;
import java.util.Map;

/**
 * The database class for archiving user's saved recipes
 * @author Cal Malouin
 */
public class RecipeDB extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "recipeDB";
    private static final String TABLE_RECIPES = "recipes";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "recipe_name";
    private static final String KEY_DATA = "recipe_data";

    public RecipeDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Create/initialize the database
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_RECIPES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT," + KEY_DATA + " TEXT" + ")";
        db.execSQL(CREATE_RECIPE_TABLE);
    }

    /**
     * Handles version changes of the database
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);

        onCreate(db);
    }

    /**
     * Add recipes stored in SharedPreferences to the database
     * @param recipeMap
     */
    void addRecipes(HashMap<String, String> recipeMap) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // loop through the HashMap, storing data
        for (Map.Entry<String, String> entry : recipeMap.entrySet()) {

            values.put(KEY_NAME, entry.getKey());
            values.put(KEY_DATA, entry.getValue());

            db.insert(TABLE_RECIPES, null, values);
        }

        db.close();
    }

    /**
     * Get all recipes stored in the database, and return as a HashMap
     * @return
     */
    public HashMap<String, String> getAllRecipes() {
        HashMap<String, String> recipeMap = new HashMap<>();

        String selectQuery = "SELECT  * FROM " + TABLE_RECIPES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // loop through database data, storing in a HashMap to be returned
        if (cursor.moveToFirst()) {
            do {
                recipeMap.put(cursor.getString(1), cursor.getString(2));
            } while (cursor.moveToNext());
        }

        return recipeMap;
    }

}