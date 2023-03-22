package com.example.minigame1901575;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "scores.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "scores";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_SCORE = "score";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_NAME + " TEXT, " +
                    COLUMN_SCORE + " INTEGER);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Score> getTopScores(int limit) {

        List<Score> scores = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID ,
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_SCORE
        };
        String sortOrder = DatabaseHelper.COLUMN_SCORE + " DESC";
        String limitClause = limit > 0 ? String.valueOf(limit) : null;
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                limitClause
        );
        while (cursor.moveToNext()) {
            String name = (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            String score = (cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE)));
            scores.add(new Score(name, score));
        }
        cursor.close();
        db.close();
        return scores;
    }

    public int getLowestScore() {
        int lowestScore = 0;
        SQLiteDatabase db = getReadableDatabase();
        String[] columns = {COLUMN_SCORE};
        String orderBy = COLUMN_SCORE + " ASC";
        String limitClause = "1";
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, orderBy, limitClause);
        if (cursor.moveToFirst()) {
            lowestScore = (cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE)));
        }
        cursor.close();
        db.close();
        return lowestScore;
    }
}
