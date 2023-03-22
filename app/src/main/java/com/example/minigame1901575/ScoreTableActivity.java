package com.example.minigame1901575;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

public class ScoreTableActivity extends AppCompatActivity {

    private TableLayout scoreTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_table);

        Intent musicIntent = new Intent(this, MusicService.class);
        startService(musicIntent);

        // Get a reference to the score table layout
        scoreTable = findViewById(R.id.scoreTable);

        // Display the scores in the score table
        displayScores();
    }

    @SuppressLint("SetTextI18n")
    private void displayScores() {
        // Create an instance of the DatabaseHelper class
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Query the scores table and get the top 25 scores
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_SCORE
        };
        String sortOrder = DatabaseHelper.COLUMN_SCORE + " DESC";
        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder,
                "25"
        );

        // Add each score to the score table
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME));
            int score = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE));


            TableRow row = new TableRow(this);

            TextView nameTextView = new TextView(this);
            nameTextView.setText(name);
            nameTextView.setTextColor(Color.parseColor("#FFFFFF"));
            nameTextView.setPadding(10, 10, 10, 10);
            nameTextView.setGravity(Gravity.CENTER);
            row.addView(nameTextView);

            TextView scoreTextView = new TextView(this);
            scoreTextView.setText(Integer.toString(score));
            scoreTextView.setTextColor(Color.parseColor("#FFFFFF"));
            scoreTextView.setPadding(10, 10, 10, 10);
            scoreTextView.setGravity(Gravity.CENTER);
            row.addView(scoreTextView);

            scoreTable.addView(row);
        }

        cursor.close();
        db.close();
    }
}