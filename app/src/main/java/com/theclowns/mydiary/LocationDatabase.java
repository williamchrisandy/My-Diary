package com.theclowns.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocationDatabase
{
    private DatabaseHelper databaseHelper;

    public LocationDatabase(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public void insertLocation(Location location)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_LOCATION_ID, location.getLocationId());
        values.put(DatabaseHelper.FIELD_LOCATION_NAME, location.getLocationName());
        values.put(DatabaseHelper.FIELD_LOCATION_LATITUDE, location.getLocationLatitude());
        values.put(DatabaseHelper.FIELD_LOCATION_LONGITUDE, location.getLocationLongitude());

        sqLiteDatabase.insert(DatabaseHelper.TABLE_LOCATION, null, values);
        sqLiteDatabase.close();
    }

    public Location getLocation(String locationId)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.FIELD_LOCATION_ID + " = ?";
        String[] selectionArgs = {locationId};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_LOCATION, null, selection, selectionArgs, null, null, null);

        Location location = null;

        if (cursor.moveToFirst())
        {
            String locationName;
            double locationLatitude, locationLongitude;

            locationId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_LOCATION_ID));
            locationName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_LOCATION_NAME));
            locationLatitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.FIELD_LOCATION_LATITUDE));
            locationLongitude = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.FIELD_LOCATION_LONGITUDE));

            location = new Location(locationId, locationName, locationLatitude, locationLongitude);
        }

        cursor.close();
        sqLiteDatabase.close();
        return location;
    }

    public void deleteLocation(String locationId)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        String tableName = DatabaseHelper.TABLE_LOCATION;
        String whereClause = DatabaseHelper.FIELD_LOCATION_ID + " = ?";
        String[] whereArgs = {locationId};

        sqLiteDatabase.delete(tableName, whereClause, whereArgs);
        sqLiteDatabase.close();
    }

    public boolean isLocationExistsInDatabase(String locationId)
    {
        boolean valid = false;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.FIELD_LOCATION_ID + " = ?";
        String[] selectionArgs = {locationId};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_LOCATION, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToNext()) valid = true;

        cursor.close();
        sqLiteDatabase.close();

        return valid;
    }
}
