package com.theclowns.mydiary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "DiaryDatabase";
    private static final int DB_VERSION = 1;

    /*  Location  */
    public static final String TABLE_LOCATION = "Location";
    public static final String FIELD_LOCATION_ID = "LocationId";
    public static final String FIELD_LOCATION_NAME = "LocationName";
    public static final String FIELD_LOCATION_LATITUDE = "LocationLatitude";
    public static final String FIELD_LOCATION_LONGITUDE = "LocationLongitude";

    private static final String CREATE_LOCATION =
            "CREATE TABLE IF NOT EXISTS " + TABLE_LOCATION +
                    " (" +
                    FIELD_LOCATION_ID + " TEXT PRIMARY KEY, " +
                    FIELD_LOCATION_NAME + " TEXT NOT NULL, " +
                    FIELD_LOCATION_LATITUDE + " REAL NOT NULL, " +
                    FIELD_LOCATION_LONGITUDE + " REAL NOT NULL" +
                    ")";

    private static final String DROP_LOCATION = "DROP TABLE IF EXISTS " + TABLE_LOCATION;

    /*  Diary Header  */
    public static final String TABLE_DIARY_HEADER = "DiaryHeader";
    public static final String FIELD_DIARY_ID = "DiaryId";
    public static final String FIELD_DIARY_TITLE = "DiaryTitle";
    public static final String FIELD_DIARY_DATE = "DiaryDate";
    public static final String FIELD_DIARY_DESCRIPTION = "DiaryDescription";

    private static final String CREATE_DIARY_HEADER =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DIARY_HEADER +
                    " (" +
                    FIELD_DIARY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FIELD_DIARY_TITLE + " TEXT, " +
                    FIELD_DIARY_DATE + " TEXT, " +
                    FIELD_DIARY_DESCRIPTION + " TEXT" +
                    ")";

    private static final String DROP_DIARY_HEADER = "DROP TABLE IF EXISTS " + TABLE_DIARY_HEADER;

    /*  Diary Detail  */
    public static final String TABLE_DIARY_DETAIL = "DiaryDetail";

    private static final String CREATE_DIARY_DETAIL =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DIARY_DETAIL +
                    " (" +
                    FIELD_DIARY_ID + " INTEGER REFERENCES " + TABLE_DIARY_HEADER + " (" + FIELD_DIARY_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                    FIELD_LOCATION_ID + " INTEGER REFERENCES " + TABLE_LOCATION + " (" + FIELD_LOCATION_ID + ") ON UPDATE CASCADE ON DELETE CASCADE, " +
                    "PRIMARY KEY (" + FIELD_DIARY_ID + ", " + FIELD_LOCATION_ID + ")" +
                    ")";

    private static final String DROP_DIARY_DETAIL = "DROP TABLE IF EXISTS " + TABLE_DIARY_DETAIL;


    public DatabaseHelper(@Nullable Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_LOCATION);
        db.execSQL(CREATE_DIARY_HEADER);
        db.execSQL(CREATE_DIARY_DETAIL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DROP_DIARY_DETAIL);
        db.execSQL(DROP_DIARY_HEADER);
        db.execSQL(DROP_LOCATION);
        onCreate(db);
    }
}
