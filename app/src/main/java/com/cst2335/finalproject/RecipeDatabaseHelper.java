package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class RecipeDatabaseHelper extends SQLiteOpenHelper {


    public static SQLiteDatabase db = null;
    public static final String DB_NAME = "RecipeDB";
    public static int VERSION_NUM = 1;
    public static final String RECIPE_TABLE = "RecipeTable";
    public static final String PK_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_WEBSITE = "source_url";
    public static final String COL_INGREDIENTS = "ingredients";

    /**
     * This is the Constructor
     *
     * @param context Context, this is the context
     */
    public RecipeDatabaseHelper(Context context) {
        super(context, DB_NAME, null, VERSION_NUM);
    }

    /**
     * This is the onCreate, used for calling createTable from other Activities
     *
     * @param db SQLiteDatabase, this is the database
     */
    public void onCreate(SQLiteDatabase db) {
        RecipeDatabaseHelper.db = db;
        createTable(db, RECIPE_TABLE);
    }

    /**
     * This method makes sure that the db variable is correct
     *
     * @param db SQLiteDatabase, this is the database
     */
    public void onOpen(SQLiteDatabase db) {
        RecipeDatabaseHelper.db = db;
    }

    /**
     * This is the onUpgrade, used for a quick table clear
     *
     * @param db         SQLiteDatabase, this the database
     * @param oldVersion int, this is the old version number
     * @param newVersion int, this is the new version number
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        VERSION_NUM++;
        db.execSQL("DROP TABLE IF EXISTS " + RECIPE_TABLE);
        onCreate(db);
    }

    /**
     * This method inserts values into a given table an insertWithOnConflict() method is used with a
     * CONFLICT_IGNORE flag to discard insert failures
     *
     * @param tableName String, this is the name of the table
     * @param title     String, this is the value for the column of the title
     * @return returns true if successful or false if not
     */
    public static long insertItem(String tableName, String title, String ingredients, String url) {
        ContentValues content = new ContentValues();
        content.put(COL_TITLE, title);
        content.put(COL_INGREDIENTS, ingredients);
        content.put(COL_WEBSITE, url);
        return db.insertWithOnConflict(tableName, null, content, SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * This method deltes a given item or row from a given table
     *
     * @param title     String, the title of the column
     * @param tableName String, the name of the table you want to look in
     * @return returns int of row id
     */
    public static int deleteItem(String title, String tableName) {
        return db.delete(tableName, COL_TITLE + " = ?", new String[]{title});
    }

    /**
     * This method creates tables in a given database
     *
     * @param db    SQLiteDatabase, this is the database you want to place the table in
     * @param table String, this is the table name
     */
    public static void createTable(SQLiteDatabase db, String table) {
        String sql = "CREATE TABLE IF NOT EXISTS " + table + " ("
                + PK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " TEXT UNIQUE, "
                + COL_INGREDIENTS + " TEXT, "
                + COL_WEBSITE + " TEXT "
                + ");";
        db.execSQL(sql);
    }

    /**
     * This method checks if a record exists or not in a database table
     *
     * @param tableName  String, this is the name of the table you want to look in
     * @param field      String, this is the column you want to look in
     * @param fieldValue String, this is the value of the item you are looking for
     * @return returns boolean true or false
     */
    public static boolean doesRecordExist(String tableName, String field, String fieldValue) {
        String Query = "Select * from " + tableName + " where " + field + " like " + "\"" + fieldValue + "\"";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    /**
     * This method points a Cursor object at a table
     *
     * @param tableName String, this is the name of the table to grab the cursor for
     * @return returns a Cursor object
     */
    public Cursor getCursor(String tableName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.query(tableName, new String[]{"*"}, null, null, null, null, null);
    }
}