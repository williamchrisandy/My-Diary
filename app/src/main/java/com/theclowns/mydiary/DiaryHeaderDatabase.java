package com.theclowns.mydiary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

public class DiaryHeaderDatabase
{
    private DatabaseHelper databaseHelper;

    public DiaryHeaderDatabase(Context context)
    {
        databaseHelper = new DatabaseHelper(context);
    }

    public void insertDiaryHeader(DiaryHeader diaryHeader)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_DIARY_TITLE, diaryHeader.getDiaryTitle());
        values.put(DatabaseHelper.FIELD_DIARY_DATE, diaryHeader.getDiaryDate());
        values.put(DatabaseHelper.FIELD_DIARY_DESCRIPTION, diaryHeader.getDiaryDescription());

        sqLiteDatabase.insert(DatabaseHelper.TABLE_DIARY_HEADER, null, values);
        sqLiteDatabase.close();
    }

    public Vector<DiaryHeader> getDiaryHeader()
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        Vector<DiaryHeader> diaryHeaders = new Vector<>();

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_HEADER, null, null, null, null, null, DatabaseHelper.FIELD_DIARY_DATE + " DESC");

        DiaryHeader diaryHeader = null;

        while (cursor.moveToNext())
        {
            int diaryId;
            String diaryTitle, diaryDate, diaryDescription;

            diaryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_ID));
            diaryTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_TITLE));
            diaryDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DATE));
            diaryDescription = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DESCRIPTION));

            diaryHeader = new DiaryHeader(diaryId, diaryTitle, diaryDate, diaryDescription);
            diaryHeaders.add(diaryHeader);
        }

        cursor.close();
        sqLiteDatabase.close();
        return diaryHeaders;
    }

    public DiaryHeader getDiaryHeader(int diaryId)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        String selection = DatabaseHelper.FIELD_DIARY_ID + " = ?";
        String[] selectionArgs = {String.valueOf(diaryId)};

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_HEADER, null, selection, selectionArgs, null, null, null);

        DiaryHeader diaryHeader = null;

        if (cursor.moveToFirst())
        {
            String diaryTitle, diaryDate, diaryDescription;

            diaryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_ID));
            diaryTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_TITLE));
            diaryDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DATE));
            diaryDescription = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DESCRIPTION));

            diaryHeader = new DiaryHeader(diaryId, diaryTitle, diaryDate, diaryDescription);
        }

        cursor.close();
        sqLiteDatabase.close();
        return diaryHeader;
    }

    public DiaryHeader getLastDiaryHeader()
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        Cursor cursor = sqLiteDatabase.query(DatabaseHelper.TABLE_DIARY_HEADER, null, null, null, null, null, DatabaseHelper.FIELD_DIARY_ID + " DESC", "1");
        DiaryHeader diaryHeader = null;

        if (cursor.moveToNext())
        {
            int diaryId;
            String diaryTitle, diaryDate, diaryDescription;

            diaryId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_ID));
            diaryTitle = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_TITLE));
            diaryDate = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DATE));
            diaryDescription = cursor.getString(cursor.getColumnIndex(DatabaseHelper.FIELD_DIARY_DESCRIPTION));

            diaryHeader = new DiaryHeader(diaryId, diaryTitle, diaryDate, diaryDescription);
        }

        cursor.close();
        sqLiteDatabase.close();
        return diaryHeader;
    }

    public void updateDiaryHeader(DiaryHeader diaryHeader)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        int diaryId = diaryHeader.getDiaryId();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.FIELD_DIARY_TITLE, diaryHeader.getDiaryTitle());
        values.put(DatabaseHelper.FIELD_DIARY_DATE, diaryHeader.getDiaryDate());
        values.put(DatabaseHelper.FIELD_DIARY_DESCRIPTION, diaryHeader.getDiaryDescription());

        String whereClause = DatabaseHelper.FIELD_DIARY_ID + " = ?";
        String[] whereArgs = {String.valueOf(diaryId)};
        sqLiteDatabase.update(DatabaseHelper.TABLE_DIARY_HEADER, values, whereClause, whereArgs);
        sqLiteDatabase.close();
    }

    public void deleteDiaryHeader(int diaryId)
    {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();

        String tableName = DatabaseHelper.TABLE_DIARY_HEADER;
        String whereClause = DatabaseHelper.FIELD_DIARY_ID + " = ?";
        String[] whereArgs = {String.valueOf(diaryId)};

        sqLiteDatabase.delete(tableName, whereClause, whereArgs);
        sqLiteDatabase.close();
    }
}
