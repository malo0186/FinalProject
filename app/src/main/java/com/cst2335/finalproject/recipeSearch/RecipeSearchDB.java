package com.cst2335.finalproject.recipeSearch;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RecipeSearchDB extends SQLiteOpenHelper {
    /**
     * START DatabaseHelper Class
     * @param DATABASE_NAME; Name of Database "MyRECIPE"
     * @param VERSION_NUM; Version number is 1
     * @param TABLE_NAME; Name of table 1/1 "SaveRecipe"
     * @param COL_ID; Name of first column
     * @param COL_RECIPE; Name of Second Column
     * @param FLAG; Used as a boolean value
     */

    public static final String DATABASE_NAME = "MyRECIPE";
    public static final int VERSION_NUM = 1;
    public static final String TABLE_NAME = "SaveRecipe";
    public static final String COL_ID = "IdNumber";
    public static final String COL_RECIPE = "Recipes";
    public static final String FLAG = "Flag";

    public RecipeSearchDB(Activity ctx) {
        //The factory parameter should be null, unless you know a lot about Database Memory management
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    } // END MyDatabaseOpenHelper

    public void onCreate(SQLiteDatabase db) {
        //Make sure you put spaces between SQL statements and Java strings:
        db.execSQL("CREATE TABLE "
                + TABLE_NAME + "( "
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_RECIPE + " TEXT, "
                + FLAG + " TEXT)"
        );
    } // End onCreate()

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database upgrade", "Old version:" + oldVersion + " newVersion:" + newVersion);
        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create a new table:
        onCreate(db);
    } // End onUpgrade()

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Database downgrade", "Old version:" + oldVersion + " newVersion:" + newVersion);
        //Delete the old table:
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //Create a new table:
        onCreate(db);
    } // End onDowngrade()

} // End MyDatabaseOpenHelper() class