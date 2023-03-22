package com.example.minigame1901575;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TableLayout gameTable;
    private TextView scoreView, levelView;
    private Button startButton;
    private ImageButton scoreButton;
    private TextView timerView;
    private int level;
    private int numViews;
    private int score;
    private String player_name;
    private boolean gameStarted;
    private ArrayList<View> viewList;
    private View highlightedView;
    private CountDownTimer timer;
    private DatabaseHelper dbHelper;
    private MediaPlayer mediaPlayer;
    private ImageButton soundButton;
    private boolean isSoundOn = true;

    private static final int TOP_SCORES_LIMIT = 25;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameTable = findViewById(R.id.gameTable);
        levelView = findViewById(R.id.levelView);
        scoreView = findViewById(R.id.scoreView);
        startButton = findViewById(R.id.startButton);
        scoreButton = findViewById(R.id.scoreButton);
        timerView = findViewById(R.id.timerView);

        dbHelper = new DatabaseHelper(this);

        // Initialize the sound button
        soundButton = findViewById(R.id.sound_button);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundOn) {
                    // Turn off the background music
                    isSoundOn = false;
                    soundButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    if (mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                } else {
                    // Turn on the background music
                    isSoundOn = true;
                    soundButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                }
            }
        });

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!gameStarted) {
                    startGame();
                } else {
                    endGame();
                }
            }
        });

        scoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                Intent intent = new Intent(MainActivity.this, ScoreTableActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the background music
        mediaPlayer = MediaPlayer.create(this, R.raw.backgroundmusicspace);
        mediaPlayer.setLooping(true);
        if (isSoundOn) {
            mediaPlayer.start();
        }
    }

    @SuppressLint({"StringFormatInvalid", "SetTextI18n"})
    private void startGame() {
        final MediaPlayer yellowbuttonsound = MediaPlayer.create(this,R.raw.yellowbuttonsound);

        gameStarted = true;
        startButton.setVisibility(View.GONE);
        scoreButton.setVisibility(View.GONE);
        level = 1;
        numViews = 2;
        score = 0;
        levelView.setText("Level: " + level);
        scoreView.setText("Score: " + score);
        timerView.setText("Time: " + 5);
        levelView.setTextColor(Color.WHITE);
        scoreView.setTextColor(Color.WHITE);
        timerView.setTextColor(Color.WHITE);
        viewList = new ArrayList<>();
        gameTable.removeAllViews();
        for (int i = 0; i < numViews; i++) {
            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            for (int j = 0; j < numViews; j++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.blue);
                TableRow.LayoutParams params = new TableRow.LayoutParams(320, 320, 1);
                params.setMargins(10, 10, 10, 10);
                view.setLayoutParams(params);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v == highlightedView) {
                            yellowbuttonsound.start();

                            score++;
                            scoreView.setText("Score: " + score);
                            scoreView.setTextColor(Color.WHITE);
                            setHighlightedView();
                        }
                    }
                });
                row.addView(view);
                viewList.add(view);
            }
            gameTable.addView(row);
        }
        setHighlightedView();
        timer = new CountDownTimer(5000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                timerView.setText("Time: " + millisUntilFinished / 1000);
                timerView.setTextColor(Color.WHITE);
            }

            @Override
            public void onFinish() {
                if (level < 5) {
                    level++;
                    numViews = level;
                    pauseGame();
                } else {
                    endGame();
                }
            }
        };
        timer.start();
    }

    @SuppressLint({"StringFormatInvalid", "SetTextI18n"})
    private void pauseGame() {
        gameStarted = false;
        startButton.setText("Start Game");
        timer.cancel();
        timerView.setText("Time: " + 0);
        timerView.setTextColor(Color.WHITE);
        displayEnterNameDialogPauseGame();
    }

    @SuppressLint({"StringFormatInvalid", "SetTextI18n"})
    private void endGame() {
        gameStarted = false;
        startButton.setText("Start Game");
        timer.cancel();
        timerView.setText("Time: " + 0);
        timerView.setTextColor(Color.WHITE);
        displayEnterNameDialogEndGame();
    }

    @SuppressLint({"StringFormatInvalid", "SetTextI18n"})
    private void startNextLevel() {
        final MediaPlayer yellowbuttonsound = MediaPlayer.create(this,R.raw.yellowbuttonsound);

        gameStarted = true;
        startButton.setVisibility(View.GONE);
        scoreButton.setVisibility(View.GONE);
        levelView.setText("Level: " + level);
        scoreView.setText("Score: " + score);
        timerView.setText("Time: " + 5);
        levelView.setTextColor(Color.WHITE);
        scoreView.setTextColor(Color.WHITE);
        timerView.setTextColor(Color.WHITE);
        numViews = level + 1;
        viewList = new ArrayList<>();
        gameTable.removeAllViews();
        for (int i = 0; i < numViews; i++) {
            TableRow row = new TableRow(this);
            row.setGravity(Gravity.CENTER);
            for (int j = 0; j < numViews; j++) {
                View view = new View(this);
                view.setBackgroundResource(R.drawable.blue);;
                TableRow.LayoutParams params = new TableRow.LayoutParams(220, 220, 1);
                params.setMargins(10, 10, 10, 10);
                view.setLayoutParams(params);
                view.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onClick(View v) {
                        if (v == highlightedView) {
                            yellowbuttonsound.start();

                            score++;
                            scoreView.setText("Score: " + score);
                            scoreView.setTextColor(Color.WHITE);
                            setHighlightedView();
                        }
                    }
                });
                row.addView(view);
                viewList.add(view);
            }
            gameTable.addView(row);
        }
        setHighlightedView();

        timer.start();
    }

    private void setHighlightedView() {
        if (highlightedView != null) {
            highlightedView.setBackgroundResource(R.drawable.blue);;
        }
        highlightedView = viewList.get(new Random().nextInt(viewList.size()));
        highlightedView.setBackgroundResource(R.drawable.yellow);;
    }

    private void displayEnterNameDialogPauseGame() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_enter_name, null);
        myDialog.setView(dialogView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final EditText nameEditText = dialogView.findViewById(R.id.dialog_name_edit_text);
        final Button continueButton = dialogView.findViewById(R.id.continueButton);
        final Button checkscoreButton = dialogView.findViewById(R.id.checkscoreButton);
        final Button endGameButton = dialogView.findViewById(R.id.endGameButton);

        endGameButton.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);

        checkscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lowestScore = dbHelper.getLowestScore();
                if (score > lowestScore || dbHelper.getTopScores(TOP_SCORES_LIMIT).size() < TOP_SCORES_LIMIT) {
                    Toast.makeText(MainActivity.this, "Congrats !!! Your score is in the Top 25.", Toast.LENGTH_SHORT).show();
                    checkscoreButton.setVisibility(View.GONE);
                    endGameButton.setVisibility(View.VISIBLE);
                    nameEditText.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(MainActivity.this, "Sorry, your score is not good enough to enter the Top 25 Scoreboard", Toast.LENGTH_SHORT).show();
                    displayScoreTable();
                    dialog.dismiss();
                }

            }
        });

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player_name = nameEditText.getText().toString().trim();
                if(player_name.isEmpty()){
                    nameEditText.setError("Name required!!!");
                    nameEditText.requestFocus();
                    return;
                }else{
                    addScoreToDatabase();
                    displayScoreTable();
                    startButton.setVisibility(View.VISIBLE);
                    scoreButton.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                startNextLevel();
            }
        });
        dialog.show();
    }

    @SuppressLint("SetTextI18n")
    private void displayEnterNameDialogEndGame() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_enter_name, null);
        myDialog.setView(dialogView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

        final TextView header = dialogView.findViewById(R.id.header);
        header.setText("Game Over");
        final EditText nameEditText = dialogView.findViewById(R.id.dialog_name_edit_text);
        final Button continueButton = dialogView.findViewById(R.id.continueButton);
        continueButton.setVisibility(View.GONE);
        final Button checkscoreButton = dialogView.findViewById(R.id.checkscoreButton);
        final Button endGameButton = dialogView.findViewById(R.id.endGameButton);

        endGameButton.setVisibility(View.GONE);
        nameEditText.setVisibility(View.GONE);

        checkscoreButton.setGravity(Gravity.CENTER);

        checkscoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lowestScore = dbHelper.getLowestScore();
                if (score > lowestScore || dbHelper.getTopScores(TOP_SCORES_LIMIT).size() < TOP_SCORES_LIMIT) {
                    Toast.makeText(MainActivity.this, "Congrats !!! Your score is in the Top 25.", Toast.LENGTH_SHORT).show();
                    checkscoreButton.setVisibility(View.GONE);
                    endGameButton.setVisibility(View.VISIBLE);
                    nameEditText.setVisibility(View.VISIBLE);
                }else{
                    Toast.makeText(MainActivity.this, "Sorry, your score is not good enough to enter the Top 25 Scoreboard", Toast.LENGTH_SHORT).show();
                    displayScoreTable();
                    dialog.dismiss();
                }

            }
        });

        endGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player_name = nameEditText.getText().toString().trim();
                if(player_name.isEmpty()){
                    nameEditText.setError("Name required!!!");
                    nameEditText.requestFocus();
                    return;
                }else{
                    addScoreToDatabase();
                    displayScoreTable();
                    startButton.setVisibility(View.VISIBLE);
                    scoreButton.setVisibility(View.VISIBLE);
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addScoreToDatabase() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_NAME, player_name);
        values.put(DatabaseHelper.COLUMN_SCORE, score);
        db.insert(DatabaseHelper.TABLE_NAME, null, values);
    }

    @SuppressLint("SetTextI18n")
    private void displayScoreTable() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                DatabaseHelper.COLUMN_ID ,
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
                sortOrder
        );

        TableLayout scoreTable = new TableLayout(this);
        scoreTable.setStretchAllColumns(true);
        scoreTable.setBackgroundColor(Color.parseColor("#00FFFF"));
        TableRow headerRow = new TableRow(this);
        TextView headerName = new TextView(this);
        headerName.setText("Name");
        headerName.setTextSize(20);
        headerName.setTextColor(Color.parseColor("#00008B"));
        headerName.setGravity(Gravity.CENTER);
        headerName.setPadding(10, 10, 10, 10);
        headerRow.addView(headerName);
        TextView headerScore = new TextView(this);
        headerScore.setText("Score");
        headerScore.setTextSize(20);
        headerScore.setTextColor(Color.parseColor("#00008B"));
        headerScore.setGravity(Gravity.CENTER);
        headerScore.setPadding(10, 10, 10, 10);
        headerRow.addView(headerScore);
        scoreTable.addView(headerRow);

        while (cursor.moveToNext()) {
            TableRow row = new TableRow(this);
            TextView nameView = new TextView(this);
            nameView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_NAME)));
            nameView.setTextColor(Color.parseColor("#5D3FD3"));
            nameView.setGravity(Gravity.CENTER);
            nameView.setPadding(10, 10, 10, 10);
            row.addView(nameView);
            TextView scoreView = new TextView(this);
            scoreView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SCORE)));
            scoreView.setTextColor(Color.parseColor("#5D3FD3"));
            scoreView.setGravity(Gravity.CENTER);
            scoreView.setPadding(10, 10, 10, 10);
            row.addView(scoreView);
            scoreTable.addView(row);
        }
        cursor.close();

        gameTable.removeAllViews();
        gameTable.addView(scoreTable);
    }

    public void onBackPressed() {
        // Stop the background music when the user presses the back button
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        dbHelper.close();
        super.onDestroy();
    }
}