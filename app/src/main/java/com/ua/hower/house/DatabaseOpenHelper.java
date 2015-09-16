package com.ua.hower.house;
/**
 * Created by Prudhvi on 12/15/2014.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseOpenHelper extends SQLiteOpenHelper {

    private static final String Database_Name = "HowerHouseDB.db";
    private static final String create_hh_table = "CREATE TABLE  HH_TABLE (   TOUR_STOP REAL PRIMARY KEY  NOT NULL , " +
                                                                                         " TOUR_TITLE TEXT   NOT NULL , " +
                                                                                         " OBJECTID TEXT NOT NULL , " +
                                                                                         " OBJECTNAME TEXT NOT NULL , " +
                                                                                         " TOUR_CONTENT TEXT NOT NULL , " +
                                                                                         " LOCATION TEXT NOT NULL , " +
                                                                                         " IMAGEPATH TEXT NOT NULL ," +
                                                                                         " IMAGECRC INTEGER NOT NULL )";

    private static final String create_hh_items = "CREATE TABLE  HH_ITEMS (   OBJECTID TEXT  PRIMARY KEY NOT NULL , " +
                                                                                         " FILEPATH TEXT NOT NULL , " +
                                                                                         " FILECRC TEXT NOT NULL  )";
    private static final int DATABASE_VERSION = 5;
    private Context context;
    private static DatabaseOpenHelper helper;

    public DatabaseOpenHelper(Context context) {
        super(context.getApplicationContext(), Database_Name, null, DATABASE_VERSION);
        Log.e("context is ", context.toString());
    }


    public static synchronized DatabaseOpenHelper getInstance(Context context)
    {
        if(helper == null)
        {
            helper = new DatabaseOpenHelper(context);
        }

        return helper;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

        try {

            db.execSQL(create_hh_table);
            db.execSQL(create_hh_items);

        } catch (Exception e) {
            Log.e("Exception create table", "" + e.toString());
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        try {
            db.execSQL("DROP TABLE IF EXISTS HH_TABLE ");
            db.execSQL("DROP TABLE IF EXISTS HH_ITEMS ");
            // create new tables
            onCreate(db);

        } catch (Exception e) {
            Log.e("Exception drop table", "" + e.toString());

        }

    }

}
