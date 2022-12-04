package com.theclowns.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

public class DiaryDetailDatabase
{
    private DatabaseHelper databaseHelper;

    public DiaryDetailDatabase(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public void insertDiaryDetail(DiaryDetail diaryDetail)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_DIARY_ID, diaryDetail.getDiaryId());
        values.put(DatabaseHelper.FIELD_LOCATION_ID, diaryDetail.getLocationId());

        sqLiteDatabase.insert(DatabaseHelper.TABLE_DIARY_DETAIL, null, values);
        sqLiteDatabase.close();
    }

    public Vector<DiaryDetail> getDiaryDetail(int diaryId)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Vector<DiaryDetail> diaryDetails = new Vector<>();

        String selection = DatabaseHelper.FIELD_DIARY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(diaryId)};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_DETAIL, null, selection, selectionArgs, null, null, null);

        DiaryDetail diaryDetail = null;

        while (cursor.moveToNext())
        {
            String locationId;
            locationId = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_LOCATION_ID));

            diaryDetail = new DiaryDetail(diaryId, locationId);
            diaryDetails.add(diaryDetail);
        }

        cursor.close();
        sqLiteDatabase.close();
        return diaryDetails;
    }

    public void deleteDiaryDetail(int diaryId, String locationId)
    {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        String tableName = DatabaseHelper.TABLE_DIARY_DETAIL;
        String whereClause = DatabaseHelper.FIELD_DIARY_ID + " =? AND " + DatabaseHelper.FIELD_LOCATION_ID + " = ?";
        String[] whereArgs = {String.valueOf(diaryId), locationId};

        db.delete(tableName, whereClause, whereArgs);
        db.close();
    }

    public boolean isLocationInAnyDiary(String locationId)
    {
        boolean valid = false;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.FIELD_LOCATION_ID + " = ?";
        String[] selectionArgs = {locationId};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_DETAIL, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToNext()) valid = true;

        cursor.close();
        sqLiteDatabase.close();

        return valid;
    }

    public boolean isLocationInTheDiary(int diaryId, String locationId)
    {
        boolean valid = false;
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection =  DatabaseHelper.FIELD_DIARY_ID + " =? AND " + DatabaseHelper.FIELD_LOCATION_ID + " =?";
        String[] selectionArgs = {String.valueOf(diaryId), locationId};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_DETAIL, null, selection, selectionArgs, null, null, null);

        if (cursor.moveToNext()) valid = true;

        cursor.close();
        sqLiteDatabase.close();

        return valid;
    }
}
