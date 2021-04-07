package com.cst2335.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TMDatabase extends SQLiteOpenHelper {

    public static int DB_VERSION = 1;
    public static final String DB_NAME = "tm_database";

    public static final String table_1 = "fav_events";
    public static final String col_1 = "id";
    public static final String col_2 = "name";
    public static final String col_3 = "starting_date";
    public static final String col_4 = "price_range";
    public static final String col_5 = "url";
    public static final String col_6 = "promotional_banner_url";

    /**
     * Constructor for TMDatabase
     * @param context
     */
    public TMDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    /**
     * Overriding onCreate method of SQLiteOpenHelper to create table
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = "Create table "+table_1+" ( "+ col_1+" INTEGER PRIMARY KEY AUTOINCREMENT,"+col_2+ " VARCHAR2(100) UNIQUE, "+col_3
                +" VARCHAR2(100),"+col_4+" VARCHAR2(100) ,"+ col_5+ " VARCHAR2(100),"+col_6+" VARCHAR2(100) );";
        sqLiteDatabase.execSQL(query);
    }

    /**
     * Overriding onUpgrade method of SQLiteOpenHelper to update database
     * @param sqLiteDatabase
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /**
     * Saving favorite event to db and returning its id
     * @param ticket
     * @return id
     */
    public long saveFavoriteEvent(Ticket ticket){

        ContentValues values = new ContentValues();

        values.put("name", ticket.getName());
        values.put("starting_date", ticket.getStarting_date());
        values.put("price_range", ticket.getPrice_range());
        values.put("url", ticket.getUrl());
        values.put("promotional_banner_url", ticket.getPromotional_banner_url());


        SQLiteDatabase db = this.getWritableDatabase();

        return db.insert(table_1, null, values);
    }

    /**
     * deleting event based on id
     * @param id
     * @return true if event deleted false if event is not deleted
     */
    public boolean deleteFavoriteEvent(long id){
        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = "id=?";
        String[] whereArgs = new String[]{String.valueOf(id)};

        int result= db.delete(table_1, whereClause, whereArgs);

        if(result == 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * fetching all favorite events from database
     * @return favoriteEvents
     */
    public ArrayList<Ticket> getAllFavoriteEvents(){

        ArrayList<Ticket> favoriteEvents = new ArrayList<Ticket>();

        String sql = "SELECT * FROM "+table_1+" ORDER BY id ASC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Long id= Long.parseLong(cursor.getString(cursor.getColumnIndex(col_1)));
                String name= cursor.getString(cursor.getColumnIndex(col_2));
                String starting_date = cursor.getString(cursor.getColumnIndex(col_3));
                String price_range = cursor.getString(cursor.getColumnIndex(col_4));
                String url = cursor.getString(cursor.getColumnIndex(col_5));
                String promotional_banner_url = cursor.getString(cursor.getColumnIndex(col_6));
                favoriteEvents.add(new Ticket(id,name,starting_date,price_range,url,promotional_banner_url));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return favoriteEvents;
    }
}
